package com.meetple.domain.chat.repository;

import com.meetple.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    // 내 치팅방 목록 조회
    @Query("Select c From Chat c, Participation p Where p.user.id = :userId and c.id = p.chat.id")
    List<Chat> findAllChatByUserId(Long userId);

    // 중복 확인
    @Query("Select c From Chat c, Participation p Where p.user.id = :userId and c.post.id = :postId and c.id = p.chat.id")
    Optional<Chat> findByPostAndUser(Long postId, Long userId);
}
