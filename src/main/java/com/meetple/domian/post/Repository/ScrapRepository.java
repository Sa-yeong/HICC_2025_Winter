package com.meetple.domian.post.Repository;

import com.meetple.domian.member.entity.User;
import com.meetple.domian.post.entity.Post;
import com.meetple.domian.post.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Optional<Scrap> findByUserAndPost(User user, Post post);


    List<Scrap> findAllByUser(User user);
    //유저의 스크랩 조회
}