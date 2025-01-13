package i4U.mukPic.global.auth.handler;

import i4U.mukPic.global.auth.PrincipalDetails;
import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.user.entity.LoginType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    /*private static final String BASE_URI = "http://localhost:8080";*/
    private static final String BASE_URI = "http://localhost:3000";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        jwtTokenProvider.generateRefreshToken(authentication, accessToken);

        if (authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
            var user = principalDetails.user();

            String redirectUrl;
            if (user.getLoginType() == LoginType.GUEST) {
                redirectUrl = BASE_URI + "/signup/step3";
            } else {
                redirectUrl = BASE_URI;
            }

            response.setHeader("Authorization", "Bearer " + accessToken);

            response.sendRedirect(redirectUrl);
        } else {
            throw new IllegalArgumentException("Unexpected authentication principal type");
        }
    }
}
