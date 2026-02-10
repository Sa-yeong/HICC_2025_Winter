package com.meetple.domain.post.Repository;

import com.meetple.domain.member.entity.Genre;
import com.meetple.domain.member.entity.User;
import com.meetple.domain.post.entity.Post;
import org.springframework.data.domain.Page; // Page 임포트
import org.springframework.data.domain.Pageable; // Pageable 임포트
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


    Page<Post> findAllByGenre(Genre genre, Pageable pageable);
    //장르별 조회, ㅣㅇ름에 어울리는 쿼리 자동 생성
    // [수정] 작성자별 조회 페이징
    Page<Post> findAllByWriter(User writer, Pageable pageable);
}