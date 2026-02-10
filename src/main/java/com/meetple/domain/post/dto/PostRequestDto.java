package com.meetple.domain.post.dto;

import com.meetple.domain.member.entity.Genre; // 장르
import com.meetple.domain.member.entity.User; // 작성자의 정보
import com.meetple.domain.post.entity.Post; // post entity의 정보 가져오기
import lombok.AllArgsConstructor; // 생성자 가져오기
import lombok.Getter; // getter 자동 생성
import lombok.NoArgsConstructor; // 기본 생성자 자동 생성

//포스트를 위한 데이터 전송받기
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
//변수들 선언
    private String Title;

    private Genre Genre;

    private String Condition;

    private String Content;

    private int Max_people;

    public Post toEntity(User writer) { //Post 객체를 생성하는 함수 toEntity
        return Post.builder() // post의 빌더패턴
                .title(this.Title) //
                .genre(this.Genre)
                .condition(this.Condition)
                .content(this.Content)
                .maxPeople(this.Max_people)
                .writer(writer)
                .build();
    }
}