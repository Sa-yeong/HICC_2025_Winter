package com.meetple.domain.member.service;

import com.meetple.domain.member.dto.*;
import com.meetple.domain.member.entity.Genre;
import com.meetple.domain.member.entity.Preference;
import com.meetple.domain.member.entity.User;
import com.meetple.domain.member.repository.MemberRepository;
import com.meetple.domain.member.repository.PreferenceRepository;
import com.meetple.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PreferenceRepository preferenceRepository;
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
        // 사용자 데이터 저장
        User user = memberRepository.save(signupRequest.toEntity());

        //선호 장르 데이터 저장
        List<Genre> preferences = signupRequest.getPreferences();
        for (Genre genre : preferences) {
            preferenceRepository.save(Preference.builder().user(user).preference(genre).build());
        }

        return SignupResponseDto.from(user);
    }

    // 4. 로그인
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = getUserByLoginId(requestDto.getLoginId());
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
    public void deleteAccount(String loginId, String password) {
        //User 객체 불러오기
        User user = getUserByLoginId(loginId);

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("잘못된 비밀번호 입니다.");
        }

        // DB 삭제
        memberRepository.delete(user);
    }

    // 6. 회원 정보 수정 화면 들어가기
    public ChangeInfo openChangeInfo(Long userId) {
        // 객체 가져오기
        User user = getUserById(userId);

        return ChangeInfo.builder().password(user.getPassword()).nickname(user.getNickname())
                .preferences(preferenceRepository.findAllPreferencesById(userId)).build();
    }

    // 7. 회원 정보 수정
    @Transactional
    public ChangeInfo changeInfo(String loginId, ChangeInfo changeData) {
        // User 객체 불러오기
        User user = getUserByLoginId(loginId);
        //Preference 객체 불러오기
        List<Genre> preferences = preferenceRepository.findAllPreferencesById(user.getId());

        //User 객체 수정
        memberRepository.updateUser(user.getId(), changeData.getPassword(), changeData.getNickname());
        //Preference 객체 수정
        // 객체 전체 삭제
        preferenceRepository.deleteAllByuserId(user.getId());

        // 새로운 객체 생성
        for (Genre genre : changeData.getPreferences()) {
            preferenceRepository.save(Preference.builder().user(user).preference(genre).build());
        }

        return changeData;
    }

    // 8. 로그인 아이디로 User 찾기
    public User getUserByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
    }

    // 9. 아이디로 User 찾기
    public User getUserById(Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
    }

}
