package com.meetple.global.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey secretKey;

    //생성자
    public JwtProvider(@Value("${spring.jwt.secret}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    //Jwt 토큰 생성
    public String createJwt(String userId, Long expriedMs) {
        return Jwts.builder()
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expriedMs))
                .signWith(secretKey)
                .compact();
    }

    //Jwt 검증
    public Boolean validateJwt(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey) // 검증할 키 설정
                    .build()
                    .parseSignedClaims(token); // 서명된 토큰 해석
            return true;
        } catch (SignatureException e) {
           log.info("잘못된 JWT 서명입니다.: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다. : {}", e.getMessage());
        }
        return false;
    }

    //사용자 로그인 아이디 추출
    public String getLoginId(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", String.class);
    }
}