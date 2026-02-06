package com.meetple.domian.post.dto;

import com.meetple.domian.member.entity.Genre; // 장르
import com.meetple.domian.member.entity.User; // 작성자의 정보
import com.meetple.domian.post.entity.Post; // post entity의 정보 가져오기
import lombok.AllArgsConstructor; // 생성자 가져오기
import lombok.Getter; // getter 자동 생성
import lombok.NoArgsConstructor; // 기본 생성자 자동 생성

//포스트를 위한 데이터 전송받기
@Getter
@NoArgsConstructor // JSON 데이터를 객체로 찍어낼 때 필요합니다. [cite: 2025-10-31]
@AllArgsConstructor // 테스트 코드 등에서 객체를 직접 생성할 때 유용합니다. [cite: 2026-02-04]
public class PostRequestDto {
//변수들 선언
    private String Title;

    private Genre Genre;

    private String Condition;

    private String Content;

    private int Max_people;
    /**
     * 전달받은 DTO 데이터를 바탕으로 실제 DB에 저장할 Post 객체를 만드는 함수입니다.
     * 작성자(User)는 보안 컨텍스트에서 따로 받아와야 하므로 매개변수로 넘겨받습니다. [cite: 2026-02-04]
     */
    public Post toEntity(User writer) { //Post 객체를 생성하는 함수 toEntity
        return Post.builder() // post의 빌더패턴
                .title(this.Title) // DTO의 Title 값을 엔티티의 title 필드에 넣습니다.
                .genre(this.Genre)
                .condition(this.Condition)
                .content(this.Content)
                .maxPeople(this.Max_people)
                .writer(writer) // 매개변수로 받은 로그인한 유저 정보를 작성자로 설정합니다.
                .build(); // 최종적으로 Post 객체를 완성하여 반환합니다.
    }
}