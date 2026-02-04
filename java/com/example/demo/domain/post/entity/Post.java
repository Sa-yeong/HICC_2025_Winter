package com.example.demo.domain.post.entity;

import com.example.demo.domain.member.entity.Genre;
import com.example.demo.domain.member.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "posts") // 테이블 이름 변경
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id") // 명세서의 Post_id 대응
    private Long id;

    @Column(nullable = false)
    private String title;     // 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;   // 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;      // 장르 (Enum)

    // ★ [변경] matePreference -> condition (모집 요건)
    @Column(name = "recruitment_condition") // DB 예약어 피하기 위해 컬럼명은 살짝 변경
    private String condition;

    // ★ [추가] 최대 모집 인원수
    private int maxPeople;

    // 작성자 (User와 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id") // 명세서의 Writer_id 대응
    private User user;

    // 작성일 (명세서엔 없지만 필수라 넣음)
    private LocalDateTime createdAt;

    // 생성될 때 시간 자동 저장
    @PrePersist
    public void createDate() {
        this.createdAt = LocalDateTime.now();
    }

    // ★ [기능 추가] 게시글 수정용 메서드 (User가 정보를 바꾸면 여기 업데이트)
    public void update(String title, Genre genre, String condition, String content, int maxPeople) {
        this.title = title;
        this.genre = genre;
        this.condition = condition;
        this.content = content;
        this.maxPeople = maxPeople;
    }
}