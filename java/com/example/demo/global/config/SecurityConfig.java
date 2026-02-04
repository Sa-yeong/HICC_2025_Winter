package com.example.demo.global.config;

import com.example.demo.global.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 해제 (Rest API에서는 안 씀)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 세션 미사용 (JWT 쓸 거니까)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. ★ 주소별 권한 설정 (여기가 제일 중요!)
                .authorizeHttpRequests(auth -> auth
                        // (1) 회원가입, 로그인은 누구나 접속 가능하게 열어둠
                        .requestMatchers("/users", "/users/login", "/error").permitAll()
                        // (2) 그 외 모든 요청(게시판 등)은 인증(로그인) 필요
                        .anyRequest().authenticated()
                )

                // 4. JWT 필터 끼워넣기
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}