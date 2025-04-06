package com.zipple.module.like.entity;

import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentLikeRepository extends JpaRepository<AgentLike, Long> {

    @Query("SELECT COUNT(l) FROM AgentLike l WHERE l.agentUser.id = :agentUserId")
    Integer countByAgentUserId(@Param("agentUserId") Long agentUserId);

    void deleteAllByUser(User user);

    void deleteAllByAgentUser(AgentUser agentUser);

    @Query("SELECT COUNT(al) > 0 FROM AgentLike al WHERE al.user.id = :userId AND al.agentUser.id = :agentId")
    boolean existsByUserIdAndAgentUserId(@Param("userId") Long userId, @Param("agentId") Long agentId);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
    AgentLike findByUserAndAgentUser(User user, AgentUser agentUser);

    @Query("SELECT COUNT(al) FROM AgentLike al WHERE al.agentUser.id = :agentUserId AND al.isDeleted = :isDeleted")
    Integer countByAgentUserId(@Param("agentUserId") Long agentUserId, @Param("isDeleted") boolean isDeleted);
}
