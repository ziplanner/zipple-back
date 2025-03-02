package com.zipple.module.like.entity;

import com.zipple.module.member.common.entity.AgentUser;
import com.zipple.module.member.common.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "agent_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "agent_id"})}
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentUser agentUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static AgentLike createLike(User user, AgentUser agentUser) {
        return AgentLike.builder()
                .user(user)
                .agentUser(agentUser)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
