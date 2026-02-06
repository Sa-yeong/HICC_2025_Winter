package com.meetple.domian.member.dto;

import com.meetple.domian.member.entity.User;
import lombok.Builder;
import lombok.Getter;

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
