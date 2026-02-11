package com.meetple.domain.member.repository;

import com.meetple.domain.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<User, Long> {
    // loginId로 사용자 찾기
    Optional<User> findByLoginId(String loginId);

    // 중복 아이디 확인
    Boolean existsByLoginId(String loginId);

    // 중복 닉네임 확인
    Boolean existsByNickname(String nickname);

    // 회원 레코드 삭제
    void delete(User user);

    // 회원 레코드 업데이트
    @Modifying(clearAutomatically = true)
    @Query("Update User Set password = :password, nickname = :nickname Where id = :id")
    void updateUser(Long id, String password, String nickname);

}