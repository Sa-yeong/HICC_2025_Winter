package com.meetple.global.security;

import com.meetple.domain.member.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Authorization 헤더에서 JWT 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");

        // 로그인 없이도 갈 수 있는 곳을 위해 -> 토큰이 없거나 내용이 이상한 것을 뒤로 넘김.
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        //진짜 토큰 값 추출
        String token = authorizationHeader.substring(7);

        try {
            //JWT 만료 여부
            if (!jwtProvider.validateJwt(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                return;
            }

            // 사용자 정보 추출
            String userId = jwtProvider.getLoginId(token);

            User user = User.builder()
                    .loginId(userId)
                    .password("N/A").build();

            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 오류 발생 : {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 처리 중 오류가 발생했습니다.");
        }
    }
}
