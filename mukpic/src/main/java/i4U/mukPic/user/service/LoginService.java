package i4U.mukPic.user.service;

import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.global.jwt.service.AuthenticationService;
import i4U.mukPic.user.dto.LoginRequestDTO;
import i4U.mukPic.user.entity.Role;
import i4U.mukPic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(LoginRequestDTO loginRequest) {
        // 사용자 인증
        User user = authenticationService.authenticate(loginRequest.getUserId(), loginRequest.getPassword());

        // Role을 GrantedAuthority로 변환
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getKey());

        // Spring Security의 Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserId(), null, Collections.singletonList(authority)
        );

        // Access Token 생성
        return jwtTokenProvider.generateAccessToken(authentication);
    }
}
