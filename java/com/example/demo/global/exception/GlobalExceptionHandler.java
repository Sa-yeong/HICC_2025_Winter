package com.example.demo.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 모든 컨트롤러에서 발생하는 에러를 여기서 잡음
public class GlobalExceptionHandler {

    // 우리가 서비스에서 던지는 IllegalArgumentException을 잡아서 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();

        // 명세서 규격에 맞게 응답 포장
        response.put("code", "400"); // 잘못된 요청은 보통 400번
        response.put("message", e.getMessage()); // 서비스에서 적은 에러 메시지 그대로 전달

        return ResponseEntity.badRequest().body(response);
    }

    // 그 외 예상치 못한 모든 에러를 잡아서 처리 (최후의 보루)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllException(Exception e) {
        Map<String, String> response = new HashMap<>();

        response.put("code", "500");
        response.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());

        return ResponseEntity.internalServerError().body(response);
    }
}