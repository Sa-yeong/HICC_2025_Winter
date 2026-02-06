package com.meetple.domian.member.dto;

import com.meetple.domian.member.entity.Gender;
import com.meetple.domian.member.entity.Genre;
import com.meetple.domian.member.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String loginId;
    private String password;
    private LocalDate birthDate;
    private Gender gender;
    private String nickname;
    private List<Genre> preferences;

    public User toEntity() {
        return User.builder().loginId(this.loginId)
                .password(this.password)
                .birthDate(this.birthDate)
                .gender(this.gender)
                .nickname(this.nickname)
                .build();
    }
}
