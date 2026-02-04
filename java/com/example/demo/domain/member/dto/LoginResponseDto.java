package com.example.demo.domain.member.dto;

import com.example.demo.domain.member.entity.User;
import lombok.Builder;
import lombok.Getter;

import javax.swing.text.html.Option;
import java.util.Optional;

@Getter
@Builder
public class LoginResponseDto {
    private String accessToken;
    private Long userId;
    private String nickname;

    public static LoginResponseDto of(User user, String accessToken) {
        return LoginResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .build();
    }
}
