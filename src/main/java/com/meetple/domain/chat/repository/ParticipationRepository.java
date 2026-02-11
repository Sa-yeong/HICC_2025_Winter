package com.meetple.domain.chat.repository;

import com.meetple.domain.chat.entity.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    @Query("Select p From Participation p Where p.chat.id = :chatId")
    List<Participation> findAllByChatId(Long chatId);

    // 채팅과 멤버 아이디를 이용한 조회
    @Query("Select p From Participation p Where p.chat.id = :chatId and p.user.id = :userId")
    Participation findByChatAndUser(Long chatId, Long userId);

    Optional<Participation> findByChatIdAndUserLoginId(Long chatId, String loginId);

    Integer countByChatId(Long chatId);
}
