package i4U.mukPic.global.auth.service;

import i4U.mukPic.global.auth.PrincipalDetails;
import i4U.mukPic.global.auth.userinfo.OAuth2UserInfo;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;

    public PasswordEncoder getPasswordEncoder() {
        return applicationContext.getBean(PasswordEncoder.class);
    }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. resistrationId 가져오기 (third-party id)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        User user = getOrSave(oAuth2UserInfo);

        // 6. OAuth2User로 반환
        return new PrincipalDetails(user, oAuth2UserAttributes, userNameAttributeName);
    }

    @Transactional
    private User getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        return userRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(() -> {
                    User user = oAuth2UserInfo.toEntity();
                    user.passwordEncode(getPasswordEncoder()); // 비밀번호 암호화
                    return userRepository.save(user);
                });
    }
}
