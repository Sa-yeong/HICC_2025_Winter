package com.meetple.domain.post.entity;

import com.meetple.domain.member.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity //엔티티임
@Getter //getter들은 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap { //시스템용 id, 사용자 정보, 게시글 정보
    @Id //pi로 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) //값이 자동으로 증가, 시스템에서 사용하는 아이디 값으로 사용
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  //다대일 관계, 실제로 이 객체를 사용할 때 쿼리 수행
    @JoinColumn(name = "user_id")//칼럼, db에서 사용되는 아이디를 "user_id"로 지정
    private User user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Post post;

    @Builder
    public Scrap(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}