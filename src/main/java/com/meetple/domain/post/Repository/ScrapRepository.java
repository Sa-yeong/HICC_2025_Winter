package com.meetple.domain.post.Repository;

import com.meetple.domain.member.entity.User;
import com.meetple.domain.post.entity.Post;
import com.meetple.domain.post.entity.Scrap;
import org.springframework.data.domain.Page; // Page 임포트 추가
import org.springframework.data.domain.Pageable; // Pageable 임포트 추가
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Optional<Scrap> findByUserAndPost(User user, Post post);


    Page<Scrap> findAllByUser(User user, Pageable pageable);
}