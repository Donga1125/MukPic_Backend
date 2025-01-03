package i4U.mukPic.user.service;

import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.global.jwt.service.AuthenticationService;
import i4U.mukPic.global.jwt.service.TokenService;
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
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    public Map<String, String> login(LoginRequestDTO loginRequest) {
        // 사용자 인증
        User user = authenticationService.authenticate(loginRequest.getUserId(), loginRequest.getPassword());

        // Role을 GrantedAuthority로 변환
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getKey());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserId(), null, Collections.singletonList(authority)
        );

        // Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);

        // Refresh Token 생성 및 저장
        jwtTokenProvider.generateRefreshToken(authentication, accessToken);

        // 저장된 Refresh Token 가져오기
        String refreshToken = tokenService.findByAccessTokenOrThrow(accessToken).getRefreshToken();

        // Access Token과 Refresh Token을 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

}
