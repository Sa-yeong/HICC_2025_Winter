package com.meetple.domian.member.dto;

import com.meetple.domian.member.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeInfo {
    private String password;
    private String nickname;
    private List<Genre> preferences;
}
