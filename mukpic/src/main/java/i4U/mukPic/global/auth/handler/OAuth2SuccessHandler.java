package i4U.mukPic.global.auth.handler;

import i4U.mukPic.global.auth.PrincipalDetails;
import i4U.mukPic.user.entity.LoginType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${front.base-uri}")
    private String BASE_URI;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
            var user = principalDetails.user();

            // 기본 리디렉션 URL 설정
            String redirectUrl;
            if (user.getLoginType() == LoginType.GUEST) {
                redirectUrl = BASE_URI + "/signup/google";
            } else {
                redirectUrl = BASE_URI + "/login/withGoogle/success";
            }

            // 리디렉션 URL에 이메일 정보 추가
            redirectUrl += "?email=" + URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8);

            // 리디렉션 수행
            response.sendRedirect(redirectUrl);
        } else {
            throw new IllegalArgumentException("Unexpected authentication principal type");
        }
    }

}
