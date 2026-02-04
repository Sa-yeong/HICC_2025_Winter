package com.example.demo.domain.member.repository;

import com.example.demo.domain.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // loginId로 사용자 찾기
    Optional<User> findByLoginId(String loginId);

    // 중복 아이디 확인
    Boolean existsByLoginId(String loginId);

    // 중복 닉네임 확인
    Boolean existsByNickname(String nickname);

    // 회원 레코드 삭제
    void delete(User user);

}

