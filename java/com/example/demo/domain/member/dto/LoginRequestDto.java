package com.example.demo.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "아이디가 입력되지 않았습니다.")
    private String loginId;

    @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
    private String password;
}
