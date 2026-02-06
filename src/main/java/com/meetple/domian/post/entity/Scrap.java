package com.meetple.domian.post.entity;

import com.meetple.domian.member.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [중요] 유저 저장 시 cascade 옵션을 절대 넣지 않습니다. [cite: 2026-02-06]
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 게시글 저장 시에도 cascade 옵션을 제거합니다.
// Scrap.java 파일의 post 필드 부분을 수정합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE) // ★ 이 줄을 추가하세요!
    private Post post;

    @Builder
    public Scrap(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}