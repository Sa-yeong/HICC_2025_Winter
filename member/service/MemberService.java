package com.meetple.domain.member.service;

import com.meetple.domain.member.dto.LoginRequestDto;
import com.meetple.domain.member.dto.LoginResponseDto;
import com.meetple.domain.member.dto.SignupRequestDto;
import com.meetple.domain.member.dto.SignupResponseDto;
import com.meetple.domain.member.entity.User;
import com.meetple.domain.member.repository.MemberRepository;
import com.meetple.global.security.CustomUserDetailsService;
import com.meetple.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    // 1. 로그인 아이디 중복 확인
    public boolean existsByLoginId(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    // 2. 닉네임 중복 확인
    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 3. 회원 등록
    public SignupResponseDto signup(SignupRequestDto signupRequest) {
        if (memberRepository.existsByLoginId(signupRequest.getLoginId())){
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByNickname(signupRequest.getNickname())){
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        // 나중에 여기서 비밀번호 암호화를 해야함 (Gemini 권장)
        User user = memberRepository.save(signupRequest.toEntity());
        return SignupResponseDto.from(user);
    }

    // 4. 로그인
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = memberRepository.findByLoginId(requestDto.getLoginId())
                .orElseThrow(() -> new UsernameNotFoundException("잘못된 아이디 입니다."));
        // 비밀번호 비교
        if (!user.getPassword().equals(requestDto.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호 입니다.");
        }
        // 토큰 생성
        String token = jwtProvider.createJwt(user.getLoginId(), 1000L * 60 * 60 * 24);

        return LoginResponseDto.of(user, token);
    }

    // 5. 회원 탈퇴 (Hard Delete)
    @Transactional
    public void deleteAccount(String token, String password) {
        // 토큰 검증
        jwtProvider.validateJwt(token);

        // 비밀번호 비교
        User user = memberRepository.findByLoginId(jwtProvider.getLoginId(token))
                .orElseThrow(() -> new UsernameNotFoundException("삭제할 사용자를 찾을 수 없습니다."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("잘못된 비밀번호 입니다.");
        }

        // DB 삭제
        memberRepository.delete(user);
    }


}
