package com.example.demo.domain.member.repository;

import com.example.demo.domain.member.entity.Preference;
import com.example.demo.domain.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {

    /**
     * 특정 유저와 연관된 모든 선호 장르 데이터를 삭제합니다.
     * 회원 정보 수정 시 기존 데이터를 밀어버리고 새로 저장할 때 사용합니다.
     */
    void deleteByUser(User user);
}