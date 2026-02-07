package com.meetple.domain.chat.entity;

import com.meetple.domain.member.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "participate", uniqueConstraints = {@UniqueConstraint(columnNames = {"chat_id", "member_id"})})
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "last_read_time")
    private LocalDateTime lastReadTime;

    @Builder
    public Participation(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
    }

    public void updateReadTime() {
        this.lastReadTime = LocalDateTime.now();
    }
}

