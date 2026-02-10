package com.meetple.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 프로젝트 전역에서 발생하는 예외를 한곳에서 처리하는 클래스입니다.
 * @RestControllerAdvice는 모든 컨트롤러에서 발생하는 에러를 감시합니다. [cite: 2025-10-31]
 */
@RestControllerAdvice
public class exception {

    /**
     * 잘못된 인자가 들어왔을 때(IllegalArgumentException) 발생하는 에러를 처리합니다.
     * 예: 게시글을 찾을 수 없을 때
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> response = new HashMap<>();

        // 명세서의 규칙대로 Code와 Message를 담습니다.
        response.put("Code", "400"); // 클라이언트 잘못이므로 400번대 코드를 줍니다.
        response.put("Message", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 권한이 없는 작업을 시도할 때(IllegalStateException) 발생하는 에러를 처리합니다.
     * 예: 내가 쓰지 않은 글을 수정하려고 할 때
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException e) {
        Map<String, Object> response = new HashMap<>();

        response.put("Code", "403"); // 권한 없음(Forbidden)의 의미로 403을 줍니다.
        response.put("Message", e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * 그 외 예상치 못한 모든 서버 내부 에러를 처리합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllException(Exception e) {
        Map<String, Object> response = new HashMap<>();

        response.put("Code", "500"); // 서버 내부 오류는 500번을 줍니다.
        response.put("Message", "서버 내부 오류가 발생했습니다: " + e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}