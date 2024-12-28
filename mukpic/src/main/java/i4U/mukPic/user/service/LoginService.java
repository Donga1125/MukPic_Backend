package i4U.mukPic.user.service;

import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.global.jwt.service.AuthenticationService;
import i4U.mukPic.user.dto.LoginRequestDTO;
import i4U.mukPic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(LoginRequestDTO loginRequest) {
        User user = authenticationService.authenticate(loginRequest.getUserId(), loginRequest.getPassword());
        return jwtTokenProvider.generateToken(user.getUserId());
    }
}
