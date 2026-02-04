package com.example.demo.domain.post.repository;

import com.example.demo.domain.member.entity.User;
import com.example.demo.domain.post.entity.Post;
import com.example.demo.domain.post.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    // 특정 유저가 특정 게시글을 스크랩했는지 확인
    Optional<Scrap> findByUserAndPost(User user, Post post);

    // 내가 스크랩한 모든 목록 가져오기
    List<Scrap> findByUser(User user);
}