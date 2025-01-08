package i4U.mukPic.global.auth.handler;

import i4U.mukPic.global.auth.PrincipalDetails;
import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.user.entity.UserStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String BASE_URI = "http://localhost:8080";
    /*private static final String BASE_URI = "https://api.mukpic.site:8080";*/

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // accessToken, refreshToken 발급
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        jwtTokenProvider.generateRefreshToken(authentication, accessToken);

        // 사용자 정보 가져오기
        if (authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
            var user = principalDetails.user();

            // 조건에 따라 다른 redirect URL 설정
            String redirectUrl;
            if (user.getUpdatedAt() == null || user.getUserStatus() == UserStatus.INACTIVE) {
                // updatedAt이 null이거나 userStatus가 INACTIVE인 경우
                redirectUrl = UriComponentsBuilder.fromUriString(BASE_URI)
                        .queryParam("accessToken", accessToken)
                        .build().toUriString();
            } else {
                // 조건에 해당하지 않는 경우
                redirectUrl = UriComponentsBuilder.fromUriString(BASE_URI + "/welcome")
                        .queryParam("accessToken", accessToken)
                        .build().toUriString();
            }

            // 리다이렉트 처리
            response.sendRedirect(redirectUrl);
        } else {
            throw new IllegalArgumentException("Unexpected authentication principal type");
        }
    }
}
