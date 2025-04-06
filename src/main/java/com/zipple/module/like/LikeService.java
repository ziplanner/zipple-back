package com.zipple.module.like;

import com.zipple.common.utils.AgentIdBase64Util;
import com.zipple.common.utils.GetMember;
import com.zipple.module.like.entity.AgentLike;
import com.zipple.module.like.entity.AgentLikeRepository;
import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import com.zipple.module.member.common.repository.AgentUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final GetMember getMember;
    private final AgentLikeRepository agentLikeRepository;
    private final AgentUserRepository agentUserRepository;
    private final AgentIdBase64Util agentIdBase64Util;

    @Transactional
    public void likeAgent(String agentUserId) {
        User user = getMember.getCurrentMember();
        Long agentId = agentIdBase64Util.decodeLong(agentUserId);
        AgentUser agentUser = agentUserRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중개사입니다."));

        AgentLike checkAgentLike = agentLikeRepository.findByUserAndAgentUser(user, agentUser);
        if (checkAgentLike == null) {
            AgentLike agentLike = AgentLike.builder()
                    .agentUser(agentUser)
                    .user(user)
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            agentLikeRepository.save(agentLike);
        } else {
            checkAgentLike.setDeleted(false);
            agentLikeRepository.save(checkAgentLike);
        }
    }

    @Transactional
    public void unlikeAgent(String agentUserId) {
        User user = getMember.getCurrentMember();
        Long agentId = agentIdBase64Util.decodeLong(agentUserId);
        AgentUser agentUser = agentUserRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중개사입니다."));

        AgentLike agentLike = agentLikeRepository.findByUserAndAgentUser(user, agentUser);
        agentLike.setDeleted(true);
        agentLikeRepository.save(agentLike);
    }

    @Transactional
    public void onceLikeAgent(String agentUserId) {
        User user = getMember.getCurrentMember();
        Long agentId = agentIdBase64Util.decodeLong(agentUserId);
        AgentUser agentUser = agentUserRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 중개사입니다."));

        AgentLike checkAgentLike = agentLikeRepository.findByUserAndAgentUser(user, agentUser);
        if (checkAgentLike == null) {
            AgentLike agentLike = AgentLike.builder()
                    .agentUser(agentUser)
                    .user(user)
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            agentLikeRepository.save(agentLike);
        } else {
            if(checkAgentLike.isDeleted()) {
                checkAgentLike.setDeleted(false);
                agentLikeRepository.save(checkAgentLike);
            } else {
                checkAgentLike.setDeleted(true);
                agentLikeRepository.save(checkAgentLike);
            }
        }
    }
}
