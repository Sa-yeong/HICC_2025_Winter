package com.meetple.domain.member.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id // Primary key라는 Annotation
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스가 자동으로 값 생성 (Auto_Increment)
    private Long id;

    @Column(name ="login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Builder
    public User(String loginId, String password, LocalDate birthDate, Gender gender, String nickname){
        this.loginId = loginId;
        this.password = password;
        this.birthDate = birthDate;
        this.gender = gender;
        this.nickname = nickname;
    }
}
