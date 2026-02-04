package com.example.demo.domain.post.dto;

import com.example.demo.domain.member.entity.Genre;
import com.example.demo.domain.member.entity.User;
import com.example.demo.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private String title;      // 제목
    private Genre genre;       // 장르
    private String condition;  // 모집 요건 (성별, 나이대 등)
    private String content;    // 내용
    private int maxPeople;     // 최대 모집 인원수

    // DTO -> Entity 변환
    public Post toEntity(User user) {
        return Post.builder()
                .title(this.title)
                .genre(this.genre)
                .condition(this.condition)
                .content(this.content)
                .maxPeople(this.maxPeople)
                .user(user)
                .build();
    }
}