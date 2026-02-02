package com.meetple.domain.member.controller;

import com.meetple.domain.member.dto.LoginRequestDto;
import com.meetple.domain.member.dto.LoginResponseDto;
import com.meetple.domain.member.dto.SignupRequestDto;
import com.meetple.domain.member.dto.SignupResponseDto;
import com.meetple.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 회원 가입
    @PostMapping(value = "/users")
    public SignupResponseDto signup(@RequestBody SignupRequestDto signupRequest) {
        return memberService.signup(signupRequest);
    }

    //로그인
    @PostMapping(value = "/users/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequest) {
        return memberService.login(loginRequest);
    }

    // 회원 탈퇴
    @DeleteMapping(value = "/users")
    public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization")String authHeader, @RequestBody Map<String, String> requestBody) {
        String token = authHeader.replace("Bearer ", "");
        String password = requestBody.get("password");

        memberService.deleteAccount(token, password);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
