package com.zipple.module.like.entity;

import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgentLikeRepository extends JpaRepository<AgentLike, Long> {

    Optional<AgentLike> findByUserAndAgentUser(User user, AgentUser agentUser);
    boolean existsByUserAndAgentUser(User user, AgentUser agentUser);
    void deleteByUserAndAgentUser(User user, AgentUser agentUser);
    long countByAgentUser(AgentUser agentUser);
}
