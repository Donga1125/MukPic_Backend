package i4U.mukPic.user.service;

import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.global.exception.InvalidTokenException;
import i4U.mukPic.user.dto.UserRequestDTO;
import i4U.mukPic.user.dto.UserResponseDTO;
import i4U.mukPic.user.entity.*;
import i4U.mukPic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
            user.updateImage(register.getImage());
        }

        Allergy allergy = user.getAllergy();
        if (allergy == null) {
            allergy = createDefaultAllergy(register.getAllergyTypes());
            allergy.setUser(user);
            user.setAllergy(allergy);
        } else {
            allergy.getAllergies().clear();
            for (String type : register.getAllergyTypes()) {
                AllergyType allergyType = AllergyType.valueOf(type.toUpperCase());
                allergy.addAllergy(allergyType);
            }
        }

        ChronicDisease chronicDisease = user.getChronicDisease();
        if (chronicDisease == null) {
            chronicDisease = createDefaultChronicDisease(register.getChronicDiseaseTypes());
            chronicDisease.setUser(user);
            user.setChronicDisease(chronicDisease);
        } else {
            chronicDisease.getDiseases().clear();
            for (String type : register.getChronicDiseaseTypes()) {
                ChronicDiseaseType diseaseType = ChronicDiseaseType.valueOf(type.toUpperCase());
                chronicDisease.addDisease(diseaseType);
            }
        }

        DietaryPreference dietaryPreference = user.getDietaryPreference();
        if (dietaryPreference == null) {
            dietaryPreference = createDefaultDietaryPreference(register.getDietaryPreferences());
            dietaryPreference.setUser(user);
            user.setDietaryPreference(dietaryPreference);
        } else {
            dietaryPreference.getPreferences().clear();
            for (String type : register.getDietaryPreferences()) {
                DietaryPreferenceType preferenceType = DietaryPreferenceType.valueOf(type.toUpperCase());
                dietaryPreference.addPreference(preferenceType);
            }
        }

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

    private DietaryPreference createDefaultDietaryPreference(List<String> dietaryPreferences) {
        DietaryPreference dietaryPreference = new DietaryPreference();

        if (dietaryPreferences != null) {
            for (String type : dietaryPreferences) {
                try {
                    DietaryPreferenceType preferenceType = DietaryPreferenceType.valueOf(type.toUpperCase());
                    dietaryPreference.addPreference(preferenceType);
                } catch (IllegalArgumentException e) {
                    throw new BusinessLogicException(ExceptionCode.INVALID_DIETARY_PREFERENCE_TYPE);
                }
            }
        }
        return dietaryPreference;
    }

    @Transactional
    public UserResponseDTO.DetailUserInfo updateUser(Long userKey, UserRequestDTO.Patch patch) {
        User user = userRepository.findById(userKey)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        if (patch.getUserName() != null && !patch.getUserName().equals(user.getUserName())) {
            checkUserName(patch.getUserName()); // 중복 검사
            user.updateUserName(patch.getUserName());
        }

        if (patch.getImage() != null) {
            user.updateImage(patch.getImage());
        }

        if (patch.getNationality() != null) {
            user.updateNationality(patch.getNationality());
        }

        if (patch.getReligion() != null) {
            user.updateReligion(patch.getReligion());
        }

        if (patch.getAllergyTypes() != null) {
            Allergy updatedAllergy = createOrUpdateAllergy(user, patch.getAllergyTypes());
            user.updateAllergy(updatedAllergy);
        }

        if (patch.getChronicDiseases() != null) {
            ChronicDisease updatedChronicDisease = createOrUpdateChronicDisease(user, patch.getChronicDiseases());
            user.updateChronicDisease(updatedChronicDisease);
        }

        if (patch.getDietaryPreferences() != null) {
            DietaryPreference updatedPreference = createOrUpdateDietaryPreference(user, patch.getDietaryPreferences());
            user.updateDietaryPreference(updatedPreference);
        }

        userRepository.save(user);

        return new UserResponseDTO.DetailUserInfo(user);
    }

    // 알러지 생성/업데이트
    private Allergy createOrUpdateAllergy(User user, List<String> allergyTypes) {
        Allergy allergy = user.getAllergy() != null ? user.getAllergy() : new Allergy();
        allergy.getAllergies().clear(); // 기존 알러지 비우기

        for (String type : allergyTypes) {
            try {
                AllergyType allergyType = AllergyType.valueOf(type.toUpperCase());
                allergy.addAllergy(allergyType);
            } catch (IllegalArgumentException e) {
                throw new BusinessLogicException(ExceptionCode.INVALID_ALLERGY_TYPE);
            }
        }

        allergy.setUser(user);
        return allergy;
    }

    // 만성질환 생성/업데이트
    private ChronicDisease createOrUpdateChronicDisease(User user, List<String> chronicDiseases) {
        ChronicDisease chronicDisease = user.getChronicDisease() != null ? user.getChronicDisease() : new ChronicDisease();
        chronicDisease.getDiseases().clear(); // 기존 만성질환 비우기

        for (String disease : chronicDiseases) {
            try {
                ChronicDiseaseType diseaseType = ChronicDiseaseType.valueOf(disease.toUpperCase());
                chronicDisease.addDisease(diseaseType);
            } catch (IllegalArgumentException e) {
                throw new BusinessLogicException(ExceptionCode.INVALID_CHRONIC_DISEASE_TYPE);
            }
        }

        chronicDisease.setUser(user);
        return chronicDisease;
    }

    private DietaryPreference createOrUpdateDietaryPreference(User user, List<String> dietaryPreferences) {
        DietaryPreference dietaryPreference = user.getDietaryPreference() != null ? user.getDietaryPreference() : new DietaryPreference();
        dietaryPreference.getPreferences().clear();

        for (String type : dietaryPreferences) {
            try {
                DietaryPreferenceType preferenceType = DietaryPreferenceType.valueOf(type.toUpperCase());
                dietaryPreference.addPreference(preferenceType);
            } catch (IllegalArgumentException e) {
                throw new BusinessLogicException(ExceptionCode.INVALID_DIETARY_PREFERENCE_TYPE);
            }
        }

        dietaryPreference.setUser(user);
        return dietaryPreference;
    }

    //이메일로 회원 확인
    public User checkUserByEmail (String email){

        return userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    }

    @Transactional
    public void updatePassword(String email, String newPassword) {
        if (email == null || email.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_EMAIL_ERROR); // 새로운 예외 코드 정의
        }

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public boolean deactivateMember(String userIdOrEmail) {
        User user = userRepository.findByUserId(userIdOrEmail)
                .or(() -> userRepository.findByEmail(userIdOrEmail)) // 이메일로 추가 조회
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        if (user.getUserStatus() == UserStatus.INACTIVE) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_DEACTIVATED_USER);
        }

        user.updateUserStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        return true;
    }

}
