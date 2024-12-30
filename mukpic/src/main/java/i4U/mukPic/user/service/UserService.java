package i4U.mukPic.user.service;

import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.user.dto.UserRequestDTO;
import i4U.mukPic.user.dto.UserResponseDTO;
import i4U.mukPic.user.entity.*;
import i4U.mukPic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDTO.DetailUserInfo createUser(UserRequestDTO.Register register) {
        User user = checkUserStatus(register);
        if (user == null) {
            user = postUser(register);
        } else {
            user.updateUserStatus(UserStatus.ACTIVE);
            user.updatePassword(passwordEncoder.encode(register.getPassword()));
            user.updateUserName(register.getUserName());
            user.updateAgree(register.getAgree());
        }

        Allergy allergy = createDefaultAllergy(register.getAllergyTypes());
        allergy.setUser(user);
        user.setAllergy(allergy);

        // 만성 질환 정보 생성 및 설정
        ChronicDisease chronicDisease = createDefaultChronicDisease(register.getChronicDiseaseTypes());
        chronicDisease.setUser(user);
        user.setChronicDisease(chronicDisease);

        userRepository.save(user);

        return new UserResponseDTO.DetailUserInfo(user);
    }

    private User postUser(UserRequestDTO.Register register) {
        checkUserName(register.getUserName());
        checkEmail(register.getEmail());

        String image = register.getImage() != null ? register.getImage() : "default-image.png";

        User user = User.builder()
                .userId(register.getUserId())
                .email(register.getEmail())
                .password(register.getPassword())
                .userName(register.getUserName())
                .image(image)
                .agree(register.getAgree())
                .role(Role.USER)
                .userStatus(UserStatus.ACTIVE)
                .loginType(LoginType.LOCAL)
                .religion(register.getReligion())
                .nationality(register.getNationality())
                .build();

        user.passwordEncode(passwordEncoder);
        return userRepository.save(user);
    }

    public UserResponseDTO.DetailUserInfo getUserInfo(Long userKey) {
        User user = checkUser(userKey);
        return new UserResponseDTO.DetailUserInfo(user);
    }

    private User checkUserStatus(UserRequestDTO.Register register) {
        return userRepository.findByEmail(register.getEmail())
                .filter(user -> user.getUserStatus() == UserStatus.INACTIVE)
                .orElse(null);
    }

    private void checkUserName(String userName) {
        if (userRepository.findByUserName(userName).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.DUPLICATE_USERNAME_ERROR);
        }
    }

    private void checkEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.DUPLICATE_EMAIL_ERROR);
        }
    }

    private User checkUser(Long userKey) {
        return userRepository.findByUserKey(userKey)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    private Allergy createDefaultAllergy(List<String> allergyTypes) {
        Allergy allergy = new Allergy();

        if (allergyTypes != null) {
            for (String type : allergyTypes) {
                try {
                    AllergyType allergyType = AllergyType.valueOf(type.toUpperCase());
                    allergy.addAllergy(allergyType);
                } catch (IllegalArgumentException e) {
                    throw new BusinessLogicException(ExceptionCode.INVALID_ALLERGY_TYPE);
                }
            }
        }
        return allergy;
    }

    private ChronicDisease createDefaultChronicDisease(List<String> chronicDiseaseTypes) {
        ChronicDisease chronicDisease = new ChronicDisease();

        if (chronicDiseaseTypes != null) {
            for (String type : chronicDiseaseTypes) {
                try {
                    ChronicDiseaseType diseaseType = ChronicDiseaseType.valueOf(type.toUpperCase());
                    chronicDisease.addDisease(diseaseType);
                } catch (IllegalArgumentException e) {
                    throw new BusinessLogicException(ExceptionCode.INVALID_CHRONIC_DISEASE_TYPE);
                }
            }
        }

        return chronicDisease;
    }

}
