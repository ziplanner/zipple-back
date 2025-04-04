package com.zipple.module.like.entity;

import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AgentLikeRepository extends JpaRepository<AgentLike, Long> {

//    Optional<AgentLike> findByUserAndAgentUser(User user, AgentUser agentUser);
    boolean existsByUserAndAgentUser(User user, AgentUser agentUser);
    void deleteByUserAndAgentUser(User user, AgentUser agentUser);
    long countByAgentUser(AgentUser agentUser);

    @Query("SELECT COUNT(l) FROM AgentLike l WHERE l.agentUser.id = :agentUserId")
    Integer countByAgentUserId(@Param("agentUserId") Long agentUserId);

    void deleteAllByUser(User user);

    void deleteAllByAgentUser(AgentUser agentUser);

    @Query("SELECT COUNT(al) > 0 FROM AgentLike al WHERE al.user.id = :userId AND al.agentUser.id = :agentId")
    boolean existsByUserIdAndAgentUserId(@Param("userId") Long userId, @Param("agentId") Long agentId);

    boolean existsByUserAndAgentUserAndIsDeletedFalse(User user, AgentUser agentUser);

    Optional<AgentLike> findByUserAndAgentUser(User user, AgentUser agentUser);
}
