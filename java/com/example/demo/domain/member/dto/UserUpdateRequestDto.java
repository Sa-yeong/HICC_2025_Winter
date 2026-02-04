package com.example.demo.domain.member.dto;

import com.example.demo.domain.member.entity.Gender;
import com.example.demo.domain.member.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List; // List 사용을 위해 추가

@Getter
@NoArgsConstructor//JSON을 객체로 반활할 때 필요
@AllArgsConstructor//생성자 자동 완성
public class UserUpdateRequestDto {
    private String nickname;
    private int age;
    private Gender gender;

    // ★ [핵심 수정] 수정 시에도 여러 장르를 한꺼번에 바꿀 수 있도록 List로 변경
    private List<Genre> preferences;
}