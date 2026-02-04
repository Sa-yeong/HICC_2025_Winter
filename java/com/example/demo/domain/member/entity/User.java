package com.example.demo.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users") // DB 예약어 충돌 방지를 위해 테이블명 명시
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // 명세서의 user_id와 매칭
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;  // 로그인 아이디

    @Column(nullable = false)
    private String password; // 비밀번호 (암호화되어 저장됨)

    @Column(nullable = false)
    private String nickname; // 닉네임

    // ★ [명세서 반영] 생년월일 삭제 -> 나이(int) 추가
    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;   // 성별 (MAN, WOMAN)

    // ★ [명세서 반영] 선호하는 장르 추가
    @Enumerated(EnumType.STRING)
    private Genre preferences;

    // ★ [기능 추가] 회원 정보 수정 메서드 (PUT /users/{user_id} 용도)
    // 비밀번호와 아이디는 변경하지 않고, 닉네임/나이/성별/선호장르만 변경
// com.example.demo.domain.member.entity.User.java

    /**
     * 회원 정보 수정 메서드
     * 이제 장르는 별도의 Preference 엔티티에서 관리하므로,
     * User 엔티티의 기본 정보(닉네임, 나이, 성별)만 업데이트하도록 변경합니다.
     */
    public void update(String nickname, int age, Gender gender) {
        this.nickname = nickname; // 전달받은 새로운 닉네임으로 교체
        this.age = age;           // 전달받은 새로운 나이로 교체
        this.gender = gender;     // 전달받은 새로운 성별로 교체

        // ★ 주의: 기존에 있던 this.preferences = preferences; 문장은 반드시 삭제해야 합니다!
    }
}