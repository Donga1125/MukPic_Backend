package i4U.mukPic.user.dto;

import i4U.mukPic.user.entity.Religion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class UserRequestDTO {

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Register {

        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9@.]{4,}$",
                message = "아이디는 4자 이상, 영문과 숫자, @, . 만 포함할 수 있습니다.")
        private String userId;

        @NotBlank
        @Email
        private String email;

        private String image;

        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "비밀번호는 8자 이상, 영문과 숫자를 모두 포함해야 합니다.")
        private String password;

        @NotBlank
        private String userName;

        @Pattern(regexp = "^[A-Za-z ]+$",
                message = "국가는 영문으로만 작성되어야 합니다.")
        private String nationality;

        @NotNull(message = "종교가 있으신가요?")
        private Religion religion = Religion.NONE;

        @NotNull
        private Boolean agree;

        private List<String> allergyTypes; // 알러지 타입 리스트

        private List<String> chronicDiseaseTypes; // 만성 질환 타입 리스트

        private List<String> dietaryPreferences;

    }

    @Getter
    @NoArgsConstructor
    public static class AdditionalInformation {
        @NotNull
        private Boolean agree;
    }

    @Getter
    @NoArgsConstructor
    public static class Patch {
        private String userName;             // 사용자 이름
        private String image;                // 프로필 이미지
        private String nationality;          // 국적
        private Religion religion;           // 종교
        private List<String> allergyTypes;   // 알러지 정보
        private List<String> chronicDiseases; // 만성질환 정보
        private List<String> dietaryPreferences;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdatePassword {
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
                message = "비밀번호는 8자 이상, 영문과 숫자를 모두 포함해야 합니다.")
        private String password;
    }

}
