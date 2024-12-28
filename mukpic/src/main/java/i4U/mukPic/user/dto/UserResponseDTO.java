package i4U.mukPic.user.dto;

import i4U.mukPic.user.entity.Allergy;
import i4U.mukPic.user.entity.Religion;
import i4U.mukPic.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
public class UserResponseDTO {

    @Getter
    @NoArgsConstructor
    public static class DetailUserInfo{
        private Long userKey;
        private String userId;
        private String email;
        private String userName;
        private String password;
        private String image;
        private Boolean agree;
        private Enum role;
        private Enum loginType;
        private Religion religion;
        private String nationality;
        private List<String> allergies; // 선택된 알러지 목록
        private List<String> chronicDiseases; // 만성질환 목록

        public DetailUserInfo (User user){
            this.userKey = user.getUserKey();
            this.userId = user.getUserId();
            this.email = user.getEmail();
            this.userName = user.getUserName();
            this.password = user.getPassword();
            this.image = user.getImage();
            this.agree = user.getAgree();
            this.role = user.getRole();
            this.loginType = user.getLoginType();
            this.religion = user.getReligion();
            this.nationality = user.getNationality();
            // 알러지 정보 변환
            this.allergies = user.getAllergy().getAllergies().stream()
                    .map(Enum::name)
                    .toList();
            // 만성 질환 정보 변환
            this.chronicDiseases = user.getChronicDisease().getDiseases().stream()
                    .map(Enum::name)
                    .toList();
        }

    }

    @Getter
    @NoArgsConstructor
    public static class BriefUserInfo {
        private Long userKey;
        private String userName;
        private String image;

        public BriefUserInfo (User user){
            this.userKey = user.getUserKey();
            this.userName = user.getUserName();
            this.image = user.getImage();
        }
    }
}
