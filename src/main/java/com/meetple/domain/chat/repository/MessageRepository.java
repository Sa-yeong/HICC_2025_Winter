package com.meetple.domain.chat.repository;

import com.meetple.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 특정 방의 전체 메세지 조회
    List<Message> getAllMessageByChatId(Long chatId);

    //특정 방의 마지막 메세지 조회
    @Query("Select m From Message m Where m.chat.id = :chatId and m.createTime = (Select Max(m1.createTime) From Message m1 Where m1.chat.id = :chatId)")
    Message getLastMessageByChatId(Long chatId);

    Optional<Message> findFirstByChatIdOrderByIdDesc(Long chatId);

    // 특정 방의 안읽은 메시지 개수 조회
    @Query("Select count(*) " +
            "From Message m Join Participation p on m.chat.id = p.chat.id " +
            "Where m.chat.id = :chatId and p.user.id = :userId " +
            "and(p.lastReadTime Is Null Or p.lastReadTime < m.createTime)")
    Integer countUnreadMessage(Long chatId, Long userId);
}
