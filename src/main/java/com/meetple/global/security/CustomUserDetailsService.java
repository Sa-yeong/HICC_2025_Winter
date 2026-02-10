package com.meetple.global.security;

import com.meetple.domain.member.entity.User;
import com.meetple.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Optional<User> optionalUser = memberRepository.findByLoginId(loginId);

        User user = optionalUser.orElseThrow(() -> {
            log.warn("해당하는 아이디가 존재하지 않습니다. : {}", loginId);
            return new UsernameNotFoundException("사용자를 찾을 수 없습니다. : " + loginId);
        });

        return new CustomUserDetails(user);
    }
}
