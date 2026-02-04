package com.example.demo.domain.post.dto;

import com.example.demo.domain.post.entity.Post;
import lombok.Getter;

@Getter
public class PostResponseDto {
    private Long postId;       // 게시물 id
    private String title;      // 제목
    private String genre;      // 장르 (String으로 변환해서 반환)
    private String condition;  // 모집 요건
    private String content;    // 내용
    private String writerId;   // 작성자 id
    private int maxPeople;     // 최대 인원

    public PostResponseDto(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.genre = post.getGenre().name(); // ENUM -> String
        this.condition = post.getCondition();
        this.content = post.getContent();
        this.maxPeople = post.getMaxPeople();
        // 작성자가 탈퇴했을 경우를 대비해 null 체크
        this.writerId = (post.getUser() != null) ? post.getUser().getLoginId() : "(알수없음)";
    }
}