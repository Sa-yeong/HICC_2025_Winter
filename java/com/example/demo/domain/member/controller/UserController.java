package com.example.demo.domain.member.controller;

import com.example.demo.domain.member.dto.SignupRequestDto;
import com.example.demo.domain.member.dto.UserUpdateRequestDto;
import com.example.demo.domain.member.service.UserService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러입니다.
 */
@RestController // JSON 형태로 응답을 보내는 컨트롤러임을 선언합니다.
@RequiredArgsConstructor // final이 붙은 필드(memberService)에 대한 생성자를 주입합니다.
@RequestMapping("/users") // 이 컨트롤러의 모든 API는 /users로 시작합니다.
public class UserController {

    private final UserService memberService; // 비즈니스 로직을 처리할 서비스 객체입니다.

    /**
     * 1. 회원가입 (POST /users)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequestDto requestDto) {
        // 서비스의 signup 메서드를 호출하여 회원가입을 진행하고 닉네임을 받아옵니다.
        String nickname = memberService.signup(requestDto);

        // 결과 응답을 담을 Map 객체를 생성합니다.
        Map<String, Object> response = new HashMap<>();
        response.put("code", "200"); // 성공 코드
        response.put("message", "회원가입 성공"); // 결과 메시지
        response.put("data", Map.of("nickname", nickname)); // 가입된 닉네임 정보

        return ResponseEntity.ok(response); // 200 OK 상태코드와 함께 응답을 보냅니다.
    }

    /**
     * 2. 로그인 (POST /users/login)
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginMap) {
        // 입력받은 아이디와 비밀번호로 로그인을 시도하고 JWT 토큰을 발급받습니다.
        String token = memberService.login(loginMap.get("loginId"), loginMap.get("password"));

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "로그인 성공");
        response.put("data", token); // 발급된 토큰을 클라이언트에 전달합니다.

        return ResponseEntity.ok(response);
    }

    /**
     * 3. 회원 탈퇴 (DELETE /users/{userId})
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long userId, // URL 경로에 있는 유저 ID를 가져옵니다.
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 현재 로그인한 유저 정보를 가져옵니다.

        // 서비스의 deleteUser를 호출하여 유저를 삭제합니다.
        memberService.deleteUser(userId, userDetails.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "회원탈퇴 성공");

        return ResponseEntity.ok(response);
    }

    /**
     * 4. 정보 수정 (PUT /users/{userId})
     * 에러가 났던 부분입니다. 서비스의 반환 타입인 UserUpdateRequestDto를 정확히 받아줘야 합니다.
     */
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long userId, // 수정할 유저의 ID
            @RequestBody UserUpdateRequestDto requestDto, // 수정할 데이터들 (리스트 형태의 장르 포함)
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 현재 로그인 유저 인증 정보

        // ★ [핵심] 서비스 호출 결과(수정된 데이터)를 DTO 타입에 맞게 저장합니다.
        UserUpdateRequestDto updatedData = memberService.updateUser(userId, requestDto, userDetails.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("code", "200");
        response.put("message", "정보 수정 성공");
        response.put("data", updatedData); // 수정된 정보들을 다시 보여줍니다.

        return ResponseEntity.ok(response);
    }
}