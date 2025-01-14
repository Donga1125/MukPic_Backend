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

        private List<String> allergyTypes;
        private List<String> chronicDiseaseTypes;
        private List<String> dietaryPreferences;

        public void setUserId(String userId) {
            if (userId == null || userId.length() < 4) {
                throw new IllegalArgumentException("아이디는 4자 이상이어야 합니다.");
            }
            this.userId = userId;
        }

        public void setEmail(String email) {
            if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
            }
            this.email = email;
        }

        public void setPassword(String password) {
            if (password == null || password.length() < 8) {
                throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
            }
            this.password = password;
        }

        public void setUserName(String userName) {
            if (userName == null || userName.isBlank()) {
                throw new IllegalArgumentException("유저 이름은 필수입니다.");
            }
            this.userName = userName;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public void setReligion(Religion religion) {
            this.religion = religion != null ? religion : Religion.NONE;
        }

        public void setAgree(Boolean agree) {
            if (agree == null) {
                throw new IllegalArgumentException("약관 동의 여부를 반드시 입력해야 합니다.");
            }
            this.agree = agree;
        }

        public void setAllergyTypes(List<String> allergyTypes) {
            this.allergyTypes = allergyTypes;
        }

        public void setChronicDiseaseTypes(List<String> chronicDiseaseTypes) {
            this.chronicDiseaseTypes = chronicDiseaseTypes;
        }

        public void setDietaryPreferences(List<String> dietaryPreferences) {
            this.dietaryPreferences = dietaryPreferences;
        }
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
