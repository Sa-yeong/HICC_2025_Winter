package com.example.demo.domain.post.repository;

import com.example.demo.domain.member.entity.Genre;
import com.example.demo.domain.member.entity.User;
import com.example.demo.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 1. 전체 조회 (최신순 정렬)
    List<Post> findAllByOrderByCreatedAtDesc();

    // 2. 장르별 조회 (최신순) - 명세서 기능
    List<Post> findByGenreOrderByCreatedAtDesc(Genre genre);

    // 3. 내 게시물 조회 (최신순) - 명세서 기능
    List<Post> findByUserOrderByCreatedAtDesc(User user);
}