package com.meetple.domain.member.repository;

import com.meetple.domain.member.entity.Genre;
import com.meetple.domain.member.entity.Preference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreferenceRepository extends JpaRepository<Preference, Long> {
    // 사용자 id를 이용한 모든 선호 장르 찾기
    @Query("Select p.preference From Preference p Where p.user.id = :userId")
    List<Genre> findAllPreferencesById(Long userId);

    // 특정 사용자의 모든 선호 장르 지우기
    @Modifying
    @Query("Delete Preference Where user.id = :userId")
    void deleteAllByuserId(Long userId);
}
