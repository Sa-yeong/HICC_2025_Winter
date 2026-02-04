package com.example.demo.domain.member.service;

import com.example.demo.domain.member.dto.SignupRequestDto;
import com.example.demo.domain.member.dto.UserUpdateRequestDto;
import com.example.demo.domain.member.entity.Genre;
import com.example.demo.domain.member.entity.Preference;
import com.example.demo.domain.member.entity.User;
import com.example.demo.domain.member.repository.PreferenceRepository;
import com.example.demo.domain.member.repository.UserRepository;
import com.example.demo.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PreferenceRepository preferenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 1. 회원가입
    @Transactional
    public String signup(SignupRequestDto requestDto) {
        if (userRepository.existsByLoginId(requestDto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = userRepository.save(requestDto.toEntity(encodedPassword));

        if (requestDto.getPreferences() != null && !requestDto.getPreferences().isEmpty()) {
            for (Genre genre : requestDto.getPreferences()) {
                preferenceRepository.save(Preference.builder().user(user).preference(genre).build());
            }
        }

        return user.getNickname();
    }

    // 2. 로그인
    public String login(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtProvider.createToken(user.getLoginId());
    }

    // 3. 정보 수정
    @Transactional
    public UserUpdateRequestDto updateUser(Long userId, UserUpdateRequestDto requestDto, String loginId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (!user.getLoginId().equals(loginId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        user.update(requestDto.getNickname(), requestDto.getAge(), requestDto.getGender());

        preferenceRepository.deleteByUser(user); // 기존 취향 삭제

        if (requestDto.getPreferences() != null && !requestDto.getPreferences().isEmpty()) {
            for (Genre genre : requestDto.getPreferences()) {
                preferenceRepository.save(Preference.builder().user(user).preference(genre).build());
            }
        }

        return requestDto;
    }

    // 4. 회원 탈퇴 (누락되었던 부분)
    @Transactional
    public void deleteUser(Long userId, String loginId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        if (!user.getLoginId().equals(loginId)) {
            throw new IllegalArgumentException("탈퇴 권한이 없습니다.");
        }

        userRepository.delete(user);
    }
}