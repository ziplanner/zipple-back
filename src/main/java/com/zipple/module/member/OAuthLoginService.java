package com.zipple.module.member;

import com.zipple.common.auth.jwt.JwtTokenProvider;
import com.zipple.common.auth.jwt.token.AuthTokens;
import com.zipple.common.auth.jwt.token.AuthTokensGenerator;
import com.zipple.common.oauth.OAuthInfoResponse;
import com.zipple.common.oauth.OAuthLoginParams;
import com.zipple.common.oauth.RequestOAuthInfoService;
import com.zipple.common.utils.GetMember;
import com.zipple.module.member.common.entity.User;
import com.zipple.module.member.common.entity.category.AgentType;
import com.zipple.module.member.common.repository.UserRepository;
import com.zipple.module.member.email.model.UserType;
import com.zipple.module.member.oauth.model.AccessTokenRenewResponse;
import com.zipple.module.member.oauth.model.AuthLoginResponse;
import com.zipple.module.member.oauth.model.RoleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long SERVICE_REFRESH_TOKEN_EXPIRATION = 604800L;
    private static final long KAKAO_REFRESH_TOKEN_EXPIRATION = 604800L;
    private static final long KAKAO_ACCESS_TOKEN_EXPIRATION = 86400L;

    private final UserRepository userRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final RestTemplate restTemplate;
    private final GetMember getMember;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthLoginResponse login(OAuthLoginParams params) {
        Map<String, Object> response = requestOAuthInfoService.request(params);
        OAuthInfoResponse oAuthInfoResponse = (OAuthInfoResponse) response.get("o_auth_info");
        String kakaoAccessToken = response.get("access_token").toString();
        String kakaoRefreshToken = response.get("refresh_token").toString();

        Long userId = findOrCreateMember(oAuthInfoResponse);
        AuthTokens authTokens = authTokensGenerator.generate(userId);

        saveTokens(userId,kakaoAccessToken, kakaoRefreshToken, authTokens.getRefreshToken());

        User user = userRepository.findById(userId).orElseThrow();
        boolean isRegistered = user.getAgentUser() == null || user.getGeneralUser() == null;

        return new AuthLoginResponse(authTokens.getAccessToken(), authTokens.getRefreshToken(), isRegistered);
    }

    protected Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return userRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(User::getId)
                .orElseGet(() -> newMember(oAuthInfoResponse));
    }

    protected Long newMember(OAuthInfoResponse oAuthInfoResponse) {
        User user = User.builder()
                .email(oAuthInfoResponse.getEmail())
                .nickname(oAuthInfoResponse.getNickname())
                .gender(oAuthInfoResponse.getGender())
                .age_range(oAuthInfoResponse.getAge_range())
                .profile_image_url(oAuthInfoResponse.getProfile_image_url())
                .birthday(oAuthInfoResponse.getBirthday())
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .build();
        return userRepository.save(user).getId();
    }

    public String refreshKakaoAccessToken(Long userId) {
        String kakaoRefreshToken = getKakaoRefreshToken(userId);
        if (kakaoRefreshToken == null) {
            throw new IllegalStateException("카카오 리프레시 토큰이 없습니다.");
        }

        String url = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=refresh_token"
                + "&client_id=YOUR_KAKAO_CLIENT_ID"
                + "&refresh_token=" + kakaoRefreshToken;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String newAccessToken = (String) response.getBody().get("access_token");
            return newAccessToken;
        } else {
            throw new RuntimeException("카카오 액세스 토큰 갱신 실패");
        }
    }

    public boolean logout() {
        Long userId = userId();
        String kakaoAccessToken = getKakaoAccessToken(userId);

        boolean isLoggedOut = callKakaoLogoutAPI(kakaoAccessToken);

        if (!isLoggedOut) {
            String newAccessToken = refreshKakaoAccessToken(userId);
            if (newAccessToken != null) {
                isLoggedOut = callKakaoLogoutAPI(newAccessToken);
            }
        }

        if (isLoggedOut) {
            deleteServiceRefreshToken(userId);
            deleteKakaoAccessToken(userId);
            deleteKakaoRefreshToken(userId);
        }

        return isLoggedOut;
    }

    private boolean callKakaoLogoutAPI(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/logout";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>("", headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            log.info("카카오 로그아웃 응답: {}", response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            log.error("카카오 로그아웃 실패: {}", e.getMessage());
            return false;
        }
    }

    public boolean withdraw() {
        Long userId = userId();
        String kakaoAccessToken = getKakaoAccessToken(userId);

        boolean isWithdrawn = callKakaoWithdrawAPI(kakaoAccessToken);

        if (!isWithdrawn) {
            // 액세스 토큰이 만료된 경우 리프레시 토큰으로 갱신 후 다시 시도
            String newAccessToken = refreshKakaoAccessToken(userId);
            if (newAccessToken != null) {
                isWithdrawn = callKakaoWithdrawAPI(newAccessToken);
            }
        }

        if (isWithdrawn) {
            deleteServiceRefreshToken(userId);
            deleteKakaoAccessToken(userId);
            deleteKakaoRefreshToken(userId);
            deleteUserFromDatabase(userId);
        }

        return isWithdrawn;
    }

    private boolean callKakaoWithdrawAPI(String accessToken) {
        String url = "https://kapi.kakao.com/v1/user/unlink";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>("", headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            log.info("카카오 회원탈퇴 응답: {}", response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            log.error("카카오 회원탈퇴 실패: {}", e.getMessage());
            return false;
        }
    }

    private void deleteUserFromDatabase(Long userId) {
        userRepository.deleteById(userId);
        log.info("사용자 ID {}의 계정이 삭제되었습니다.", userId);
    }

    private void deleteKakaoAccessToken(Long userId) {
        redisTemplate.delete("user:" + userId + ":kakaoAccessToken");
    }

    private void deleteKakaoRefreshToken(Long userId) {
        redisTemplate.delete("user:" + userId + ":kakaoRefreshToken");
    }

    private void saveTokens(Long userId, String kakaoAccessToken, String kakaoRefreshToken, String serviceRefreshToken) {
        saveKakaoAccessToken(userId, kakaoAccessToken);
        saveKakaoRefreshToken(userId, kakaoRefreshToken);
        saveServiceRefreshToken(userId, serviceRefreshToken);
    }

    private void saveKakaoAccessToken(Long userId, String kakaoAccessToken) {
        redisTemplate.opsForValue().set("user:" + userId + ":kakaoAccessToken", kakaoAccessToken, KAKAO_ACCESS_TOKEN_EXPIRATION, TimeUnit.SECONDS);
    }

    private void saveKakaoRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set("user:" + userId + ":kakaoRefreshToken", refreshToken, KAKAO_REFRESH_TOKEN_EXPIRATION, TimeUnit.SECONDS);
    }

    private void saveServiceRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set("user:" + userId + ":serviceRefreshToken", refreshToken, SERVICE_REFRESH_TOKEN_EXPIRATION, TimeUnit.SECONDS);
    }

    private void deleteServiceRefreshToken(Long userId) {
        redisTemplate.delete("user:" + userId + ":serviceRefreshToken");
    }

    private String getKakaoAccessToken(Long userId) {
        return redisTemplate.opsForValue().get("user:" + userId + ":kakaoAccessToken");
    }

    private String getKakaoRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("user:" + userId + ":kakaoRefreshToken");
    }

    private String getServiceRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("user:" + userId + ":serviceRefreshToken");
    }

    private Long userId() {
        User user = getMember.getCurrentMember();
        return user.getId();
    }

    public AccessTokenRenewResponse renewAccessToken() {
        Long userId = userId();
        String refreshToken = getServiceRefreshToken(userId);
        if (refreshToken == null) {
            return new AccessTokenRenewResponse(null, true);
        }

        String newAccessToken = jwtTokenProvider.generate(userId.toString(), new Date(System.currentTimeMillis() + 3600000));
        return new AccessTokenRenewResponse(newAccessToken, false);
    }

    @Transactional(readOnly = true)
    public RoleResponse getRole() {
        User user = getMember.getCurrentMember();
        return new RoleResponse(
                determineUserType(user).toString(), user.getNickname()
        );
    }

    private UserType determineUserType(User user) {
        if(user.getGeneralUser() != null) {
            return UserType.GENERAL;
        }
        if (user.getAgentUser() != null) {
            AgentType agentType = user.getAgentUser().getAgentType();
            if (agentType == AgentType.BUSINESS_AGENT) {
                return UserType.BUSINESS_AGENT;
            } else if (agentType == AgentType.AFFILIATED_AGENT) {
                return UserType.AFFILIATED_AGENT;
            }
        }
        return UserType.NOT_REGISTERED;
    }
}
