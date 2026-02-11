package com.meetple.domain.member.dto;

import com.meetple.domain.member.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupResponseDto {
    private String nickname;
    private String loginId;

    public static SignupResponseDto from(User user) {
        return SignupResponseDto.builder()
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .build();
    }
}
