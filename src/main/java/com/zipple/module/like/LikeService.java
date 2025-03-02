package com.zipple.module.like;

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

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    private final GetMember getMember;
    private final AgentLikeRepository agentLikeRepository;
    private final AgentUserRepository agentUserRepository;

    @Transactional
    public void likeAgent(Long agentUserId) {
        User user = getMember.getCurrentMember();

        AgentUser agentUser = agentUserRepository.findById(agentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        if (agentLikeRepository.existsByUserAndAgentUser(user, agentUser)) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }

        AgentLike like = AgentLike.createLike(user, agentUser);
        agentLikeRepository.save(like);
    }

    @Transactional
    public void unlikeAgent(Long agentUserId) {
        User user = getMember.getCurrentMember();

        AgentUser agentUser = agentUserRepository.findById(agentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        agentLikeRepository.deleteByUserAndAgentUser(user, agentUser);
    }

    @Transactional(readOnly = true)
    public long getAgentLikeCount(Long agentUserId) {
        AgentUser agentUser = agentUserRepository.findById(agentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공인중개사입니다."));

        return agentLikeRepository.countByAgentUser(agentUser);
    }
}
