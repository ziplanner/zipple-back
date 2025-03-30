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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public static AgentLike createLike(User user, AgentUser agentUser) {
        return AgentLike.builder()
                .user(user)
                .agentUser(agentUser)
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
    }
}
