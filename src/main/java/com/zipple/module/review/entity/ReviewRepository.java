package com.zipple.module.review.entity;

import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByAgentUser(AgentUser agentUser);

    Optional<Review> findByIdAndUserId(Long reviewId, Long userId);

    @Query("SELECT AVG(r.starCount) FROM Review r WHERE r.agentUser.id = :agentId")
    Double findAverageStarCountByAgent(@Param("agentId") Long agentId);

    Integer countByAgentUser(AgentUser agentUser);

    List<Review> findTop6ByAgentUserIdOrderByCreatedAtDesc(Long agentId);

    void deleteAllByUser(User user);

    @Query("SELECT r FROM Review r WHERE r.agentUser = :agentUser ORDER BY r.createdAt DESC")
    Page<Review> findPagedReviewsByAgentUser(@Param("agentUser") AgentUser agentUser, Pageable pageable);

    Integer countAllByAgentUser(AgentUser agentUser);

    List<Review> findByUser(User user);
}
