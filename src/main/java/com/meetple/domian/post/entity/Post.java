package com.meetple.domian.post.entity;

import com.meetple.domian.member.entity.Genre;
import com.meetple.domian.member.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    // MySQL 예약어 'condition'과의 충돌을 피하기 위한 컬럼명 설정 [cite: 2026-02-06]
    @Column(name = "post_condition", nullable = false)
    private String condition;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int maxPeople;

    // [중요] cascade 옵션이 있다면 반드시 제거해야 합니다. [cite: 2025-10-31]
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @Builder
    public Post(String title, Genre genre, String condition, String content, int maxPeople, User writer) {
        this.title = title;
        this.genre = genre;
        this.condition = condition;
        this.content = content;
        this.maxPeople = maxPeople;
        this.writer = writer;
    }

    // 게시물 수정을 위한 비즈니스 메서드
    public void update(String title, Genre genre, String condition, String content, int maxPeople) {
        this.title = title;
        this.genre = genre;
        this.condition = condition;
        this.content = content;
        this.maxPeople = maxPeople;
    }
}