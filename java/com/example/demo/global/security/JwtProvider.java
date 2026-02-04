package com.example.demo.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret; // application.properties에서 가져옴

    private SecretKey key;

    // 1시간 (밀리초 단위: 1000 * 60 * 60)
    private final long EXPIRATION_TIME = 1000L * 60 * 60;

    // 객체 생성 후 키를 세팅하는 메서드
    @PostConstruct
    public void init() {
        // 비밀키가 너무 짧으면 에러나니 길게 설정했던 그 키를 사용
        // 만약 Base64 인코딩된 키라면 Decoders.BASE64.decode(secret) 사용
        // 그냥 문자열이면 getBytes() 사용. 여기선 편의상 getBytes()로 함
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ★ [1] 토큰 생성 (createToken) - 에러 나던 부분 해결!
    public String createToken(String loginId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(loginId) // 사용자 ID를 주제(Subject)로 저장
                .issuedAt(now)    // 발행 시간
                .expiration(expiryDate) // 만료 시간
                .signWith(key)    // 비밀키로 서명
                .compact();       // 문자열로 반환
    }

    // [2] 토큰에서 아이디(User loginId) 꺼내기
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // [3] 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // 만료되거나 위조된 토큰이면 false 반환
            return false;
        }
    }
}