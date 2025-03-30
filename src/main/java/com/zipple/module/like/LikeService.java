package com.zipple.module.like;

import com.zipple.common.exception.custom.RequestTooFastException;
import com.zipple.common.utils.AgentIdBase64Util;
import com.zipple.common.utils.GetMember;
import com.zipple.module.like.entity.AgentLike;
import com.zipple.module.like.entity.AgentLikeRepository;
import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import com.zipple.module.member.common.repository.AgentUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final GetMember getMember;
    private final AgentLikeRepository agentLikeRepository;
    private final AgentUserRepository agentUserRepository;
    private final AgentIdBase64Util agentIdBase64Util;

    private final ConcurrentHashMap<String, Long> likeRequestCache = new ConcurrentHashMap<>();
    private final long REQUEST_INTERVAL_MS = 3000;

    private String makeKey(Long userId, Long agentId) {
        return userId + ":" + agentId;
    }

    @Transactional
    public void likeAgent(String agentUserId) {
        User user = getMember.getCurrentMember();
        Long agentId = agentIdBase64Util.decodeLong(agentUserId);

        String key = makeKey(user.getId(), agentId);
        Long lastTime = likeRequestCache.get(key);
        long now = System.currentTimeMillis();

        if (lastTime != null && now - lastTime < REQUEST_INTERVAL_MS) {
            throw new RequestTooFastException();
        }
        likeRequestCache.put(key, now);

        AgentUser agentUser = agentUserRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중개사입니다."));

        Optional<AgentLike> likeOpt = agentLikeRepository.findByUserAndAgentUser(user, agentUser);
        if (likeOpt.isPresent()) {
            AgentLike like = likeOpt.get();
            if (!like.isDeleted()) {
                throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
            } else {
                like.restore(); // 소프트 딜리트된 경우 복구
            }
        } else {
            AgentLike like = AgentLike.createLike(user, agentUser);
            agentLikeRepository.save(like);
        }
    }

    @Transactional
    public void unlikeAgent(String agentUserId) {
        User user = getMember.getCurrentMember();
        Long agentId = agentIdBase64Util.decodeLong(agentUserId);

        String key = makeKey(user.getId(), agentId);
        Long lastTime = likeRequestCache.get(key);
        long now = System.currentTimeMillis();

        if (lastTime != null && now - lastTime < REQUEST_INTERVAL_MS) {
            throw new RequestTooFastException();
        }
        likeRequestCache.put(key, now);

        AgentUser agentUser = agentUserRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중개사입니다."));

        AgentLike like = agentLikeRepository.findByUserAndAgentUser(user, agentUser)
                .orElseThrow(() -> new IllegalStateException("좋아요 상태가 아닙니다."));

        if (!like.isDeleted()) {
            like.softDelete();
        }
    }
//    @Transactional
//    public void likeAgent(String agentUserId) {
//        User user = getMember.getCurrentMember();
//        Long agentId = agentIdBase64Util.decodeLong(agentUserId);
//
//        String key = makeKey(user.getId(), agentId);
//        Long lastTime = likeRequestCache.get(key);
//        long now = System.currentTimeMillis();
//
//        if (lastTime != null && now - lastTime < REQUEST_INTERVAL_MS) {
//            throw new RequestTooFastException();
//        }
//        likeRequestCache.put(key, now);
//
//        AgentUser agentUser = agentUserRepository.findById(agentId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중개사입니다."));
//
//        if (agentLikeRepository.existsByUserAndAgentUser(user, agentUser)) {
//            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
//        }
//
//        AgentLike like = AgentLike.createLike(user, agentUser);
//        agentLikeRepository.save(like);
//    }
//
//    @Transactional
//    public void unlikeAgent(String agentUserId) {
//        User user = getMember.getCurrentMember();
//        Long agentId = agentIdBase64Util.decodeLong(agentUserId);
//
//        String key = makeKey(user.getId(), agentId);
//        Long lastTime = likeRequestCache.get(key);
//        long now = System.currentTimeMillis();
//
//        if (lastTime != null && now - lastTime < REQUEST_INTERVAL_MS) {
//            throw new RequestTooFastException();
//        }
//
//        likeRequestCache.put(key, now);
//
//        AgentUser agentUser = agentUserRepository.findById(agentId)
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중개사입니다."));
//
//        agentLikeRepository.deleteByUserAndAgentUser(user, agentUser);
//    }

    @Transactional(readOnly = true)
    public long getAgentLikeCount(String agentUserId) {
        Long userId = agentIdBase64Util.decodeLong(agentUserId);
        AgentUser agentUser = agentUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        return agentLikeRepository.countByAgentUser(agentUser);
    }
}
