package i4U.mukPic.global.auth.userinfo;

import i4U.mukPic.user.entity.Allergy;
import i4U.mukPic.user.entity.ChronicDisease;
import i4U.mukPic.user.entity.DietaryPreference;
import i4U.mukPic.global.auth.exception.AuthException;
import i4U.mukPic.user.entity.LoginType;
import i4U.mukPic.user.entity.Role;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.entity.UserStatus;
import lombok.Builder;

import java.util.Map;
import java.util.UUID;

import static i4U.mukPic.global.exception.ErrorCode.ILLEGAL_REGISTRATION_ID;

@Builder
public record OAuth2UserInfo(
        String name,
        String email,
        String profile
) {

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            default -> throw new AuthException(ILLEGAL_REGISTRATION_ID);
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .build();
    }

    public User toEntity() {
        Allergy defaultAllergy = new Allergy();
        ChronicDisease defaultChronicDisease = new ChronicDisease();
        DietaryPreference defaultDietaryPreference = new DietaryPreference();

        String randomPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 10); // 10자리 비밀번호 생성
        return User.builder()
                .userId(email)
                .userName(email) //name 닉네임이 겹치지 않게 email로 수정함
                .email(email)
                .image(profile)
                .role(Role.USER)
                .loginType(LoginType.GUEST) //최초가입 guest
                .userStatus(UserStatus.ACTIVE)
                .agree(true)
                .password(randomPassword) // 임의의 비밀번호 설정
                .build();
    }
}
