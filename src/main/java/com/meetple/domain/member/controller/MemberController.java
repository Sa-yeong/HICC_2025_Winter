package com.meetple.domain.member.controller;

import com.meetple.domain.member.dto.*;
import com.meetple.domain.member.service.MemberService;
import com.meetple.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

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
    public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, String> requestBody) {
        String token = authHeader.replace("Bearer ", "");
        String password = requestBody.get("password");

        // 토큰 인증
        jwtProvider.validateJwt(token);

        memberService.deleteAccount(jwtProvider.getLoginId(token), password);
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }

    // 사용자 정보 수정 화면 불러오기
    @GetMapping(value = "/users/{user_id}")
    public ChangeInfo openChangeInfo(@PathVariable("user_id") Long userId, @RequestHeader("Authorization") String authHeader) {
        // 토큰 인증
        String token = authHeader.replace("Bearer ", "");
        jwtProvider.validateJwt(token);

        return memberService.openChangeInfo(userId);
    }

    //사용자 정보 수정
    @PutMapping(value = "/users/{user_id}")
    public ChangeInfo changeUserInfo(@PathVariable("user_id") Long userId, @RequestHeader("Authorization") String authHeader, @RequestBody ChangeInfo changeInfo) {
        // 토큰 인증
        String token = authHeader.replace("Bearer ", "");
        jwtProvider.validateJwt(token);

        return memberService.changeInfo(jwtProvider.getLoginId(token), changeInfo);
    }
}
