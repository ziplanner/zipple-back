package com.zipple.module.member;

import com.zipple.common.oauth.kakao.KakaoLoginParams;
import com.zipple.module.member.oauth.model.AccessTokenRenewResponse;
import com.zipple.module.member.oauth.model.AuthLoginResponse;
import com.zipple.module.member.oauth.model.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "소셜 로그인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class OAuthController {
    private final OAuthLoginService oAuthLoginService;

    @Operation(
            summary = "카카오 로그인"
            ,description = "authorizationCode: url에 code값 입력"
    )
    @PostMapping("/kakao")
    public ResponseEntity<AuthLoginResponse> loginKakao(@RequestBody KakaoLoginParams params) {
        log.info("kakao code: {}", params);
        return ResponseEntity.ok(oAuthLoginService.login(params));
    }

    @Operation(
            summary = "카카오 로그아웃"
            , description = "토큰 삭제"
    )
    @PatchMapping(value = "/logout")
    public ResponseEntity<String> logoutKakao() {
        try {
            boolean result = oAuthLoginService.logout();
            if (result) {
                return ResponseEntity.ok("로그아웃 성공");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그아웃 실패");
            }
        } catch (Exception e) {
            log.error("카카오 로그아웃 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 처리 중 오류 발생");
        }
    }

    @Operation(summary = "카카오 회원탈퇴")
    @DeleteMapping(value = "/withdraw")
    public ResponseEntity<String> withdraw() {
        oAuthLoginService.withdraw();
        return ResponseEntity.ok("회원탈퇴 성공");
    }

    @Operation(summary = "액세스 토큰 갱신")
    @PostMapping(value = "/renew")
    public ResponseEntity<AccessTokenRenewResponse> renewToken(
            @Parameter(name = "refreshToken", description = "액세스 토큰 갱신을 위해")
            @RequestParam("refreshToken") String refreshToken
    ) {
        AccessTokenRenewResponse accessTokenRenewResponse = oAuthLoginService.renewAccessToken(refreshToken);
        return ResponseEntity.ok(accessTokenRenewResponse);
    }

    @Operation(summary = "권한, 이름 조회")
    @GetMapping(value = "role")
    public ResponseEntity<RoleResponse> role() {
        RoleResponse roleResponse = oAuthLoginService.getRole();
        return ResponseEntity.ok(roleResponse);
    }
}
