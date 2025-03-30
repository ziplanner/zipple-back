package com.zipple.module.mypage.agent.portfolio;

import com.zipple.module.member.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Integer countByUserId(long userId);

    List<Portfolio> findTop6ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT p FROM Portfolio p JOIN FETCH p.portfolioImage pi WHERE p.id = :portfolioId")
    Optional<Portfolio> findByIdWithImages(@Param("portfolioId") Long portfolioId);

    @Query("SELECT p FROM Portfolio p " +
            "JOIN FETCH p.user u " +
            "LEFT JOIN FETCH u.agentUser au " +
            "LEFT JOIN FETCH p.portfolioImage pi " +
            "WHERE p.id = :portfolioId")
    Optional<Portfolio> findByIdWithUserAndImages(@Param("portfolioId") Long portfolioId);

    List<Portfolio> findByUser(User user);

    Optional<Portfolio> findByUserId(Long id);
}
