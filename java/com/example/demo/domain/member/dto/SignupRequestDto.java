package com.example.demo.domain.member.dto;

import com.example.demo.domain.member.entity.Gender;
import com.example.demo.domain.member.entity.Genre;
import com.example.demo.domain.member.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List; // List 사용을 위해 추가

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String loginId;
    private String password;
    private String nickname;
    private int age;
    private Gender gender;

    // ★ [핵심 수정] 단일 Genre에서 List<Genre>로 변경하여 복수 선택 지원
    private List<Genre> preferences;

    // DTO -> Entity 변환 메서드
    public User toEntity(String encodedPassword) {
        return User.builder()
                .loginId(this.loginId)
                .password(encodedPassword)
                .nickname(this.nickname)
                .age(this.age)
                .gender(this.gender)
                // ★ User 엔티티에서 단일 preferences 필드를 삭제할 예정이므로
                // 여기서 preferences는 세팅하지 않습니다. (Service에서 처리)
                .build();
    }
}