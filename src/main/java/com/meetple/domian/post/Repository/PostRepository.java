package com.meetple.domian.post.Repository;

import com.meetple.domian.member.entity.Genre; // 장르 필터링을 위해 장르 Enum을 불러옵니다.
import com.meetple.domian.member.entity.User; // 작성자별 조회를 위해 유저 엔티티를 불러옵니다.
import com.meetple.domian.post.entity.Post; // 게시물 엔티티를 불러옵니다.
import org.springframework.data.jpa.repository.JpaRepository; // 스프링 데이터 JPA의 기능을 사용하기 위해 상속받습니다. [cite: 2025-10-31]
import org.springframework.stereotype.Repository; // 이 인터페이스가 저장소임을 스프링에 알립니다.

import java.util.List; // 여러 개의 게시글을 리스트 형태로 받기 위해 사용합니다.
//쿼리를 대신 짜줌
@Repository
//spring Data Jpa의 기능으로 함수 이름으로 sql작성
//post를 받을거고 long일거야
public interface PostRepository extends JpaRepository<Post, Long>  {

    List<Post> findAllByGenre(Genre genre); //장르로 찾기, 게시물을 리스트의 형식으로 반환


    List<Post> findAllByWriter(User writer); //Writer의 아이디로 게시물 조회하는 함수
}
