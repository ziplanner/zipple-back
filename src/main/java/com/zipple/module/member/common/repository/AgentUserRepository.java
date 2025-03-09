package com.zipple.module.member.common.repository;

import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.category.AgentSpecialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentUserRepository extends JpaRepository<AgentUser, Long> {

    @Query("SELECT a FROM AgentUser a WHERE a.agentSpecialty = :agentSpecialty ORDER BY a.id DESC")
    Page<AgentUser> findByAgentSpecialty(@Param("agentSpecialty") AgentSpecialty agentSpecialty, Pageable pageable);
}
