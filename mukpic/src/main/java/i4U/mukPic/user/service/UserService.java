package i4U.mukPic.user.service;

import i4U.mukPic.email.service.EmailSendService;
import i4U.mukPic.global.auth.entity.Token;
import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.global.jwt.service.TokenService;
import i4U.mukPic.image.service.ImageService;
import i4U.mukPic.user.dto.UserRequestDTO;
import i4U.mukPic.user.dto.UserResponseDTO;
import i4U.mukPic.user.entity.*;
import i4U.mukPic.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final ImageService imageService;
    private final EmailSendService emailSendService;

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
            user.updateReligion(register.getReligion());
            user.updateNationality(register.getNationality());
            user.updateEmail(register.getEmail());
            user.updateUserId(register.getUserId());
            if (register.getImage() != null && !register.getImage().isEmpty()) {
                user.updateImage(register.getImage());
            }
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

        String imageUrl;
        if (register.getImage() != null && !register.getImage().isEmpty()) {
            imageUrl = register.getImage();
        } else {
            imageUrl = imageService.getDefaultImageUrl();
        }

        User user = User.builder()
                .userId(register.getUserId())
                .email(register.getEmail())
                .password(register.getPassword())
                .userName(register.getUserName())
                .image(imageUrl)
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

    public User getUserInfo(Long userKey) {
        return checkUser(userKey);
    }

    public User checkUserStatus(UserRequestDTO.Register register) {
        Optional<User> optionalUser = userRepository.findByEmail(register.getEmail());
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        if (user.getUserStatus() == UserStatus.INACTIVE) {
            return user;
        } else if (user.getUserStatus() == UserStatus.ACTIVE) {
            throw new BusinessLogicException(ExceptionCode.DUPLICATE_EMAIL_ERROR);
        }
        throw new BusinessLogicException(ExceptionCode.UNKNOWN_USER_STATUS_ERROR);
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
    public UserResponseDTO.DetailUserInfo updateUserFromRequest(UserRequestDTO.Patch patch, HttpServletRequest request) {
        User user = getUserFromRequest(request);

        if (patch.getUserName() != null && !patch.getUserName().equals(user.getUserName())) {
            checkUserName(patch.getUserName());
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
        if (user.getLoginType() == LoginType.GUEST) {
            user.updateLoginType(LoginType.GOOGLE);
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

    @Transactional
    public void updatePasswordFromRequest(String newPassword, HttpServletRequest request) {
        User user = getUserFromRequest(request);

        if (newPassword == null || newPassword.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD_ERROR);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    @Transactional
    public boolean deactivateMember(String userIdOrEmail) {
        User user = userRepository.findByUserId(userIdOrEmail)
                .or(() -> userRepository.findByEmail(userIdOrEmail)) // 이메일로 추가 조회
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        if (user.getUserStatus() == UserStatus.INACTIVE) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_DEACTIVATED_USER);
        }

        if (user.getLoginType() == LoginType.GOOGLE) {
            user.updateLoginType(LoginType.GUEST);
        }

        user.updateUserStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        return true;
    }

    public User checkUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
    }

    @Transactional
    public void deactivateUser(HttpServletRequest request) {
        User user = getUserFromRequest(request);

        deactivateMember(user.getUserId());
    }

    @Transactional
    public User getUserFromRequest(HttpServletRequest request) {
        // Access Token 추출
        String accessToken = jwtTokenProvider.extractAccessToken(request)
                .orElseThrow(() -> new RuntimeException("Access Token이 없습니다."));

        // Access Token 갱신 처리
        accessToken = refreshAccessTokenIfExpired(accessToken);

        // 사용자 ID 추출
        String userId = jwtTokenProvider.extractSubject(accessToken)
                .orElseThrow(() -> new RuntimeException("토큰에서 사용자 ID 정보를 가져올 수 없습니다."));

        // userId로 사용자 정보 조회
        return checkUserByUserId(userId);
    }


    private String refreshAccessTokenIfExpired(String accessToken) {
        if (!jwtTokenProvider.validateToken(accessToken)) {
            Token token = tokenService.findByAccessTokenOrThrow(accessToken);
            log.info("Using Refresh Token for Access Token renewal");

            if (jwtTokenProvider.validateToken(token.getRefreshToken())) {
                String newAccessToken = jwtTokenProvider.generateAccessToken(
                        jwtTokenProvider.getAuthentication(token.getRefreshToken()));
                tokenService.updateToken(newAccessToken, token);
                return newAccessToken;
            } else {
                throw new RuntimeException("Refresh Token도 만료되었습니다.");
            }
        }
        return accessToken;
    }

    @Transactional(readOnly = true)
    public User isUserIdDuplicate(String userId) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        if (user.getUserStatus() == UserStatus.INACTIVE) {
            return null;
        }
        throw new BusinessLogicException(ExceptionCode.DUPLICATE_USERID_ERROR);
    }


    @Transactional(readOnly = true)
    public User isUserNameDuplicate(String userName) {
        Optional<User> optionalUser = userRepository.findByUserName(userName);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        if (user.getUserStatus() == UserStatus.INACTIVE) {
            return null;
        }
        throw new BusinessLogicException(ExceptionCode.DUPLICATE_USERNAME_ERROR);
    }

    public Map<String, Object> handleEmailDuplicationCheck(String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserRequestDTO.Register register = new UserRequestDTO.Register();
            register.setEmail(email);
            User user = checkUserStatus(register);
            if (user != null && user.getUserStatus() == UserStatus.INACTIVE) {
                String authCode = emailSendService.joinEmail(email);
                response.put("message", "재가입 유저입니다. 인증 메일을 발송했습니다.");
                response.put("authCode", authCode);
                response.put("status", HttpStatus.CONFLICT);
                return response;
            }
        } catch (BusinessLogicException e) {
            if (e.getExceptionCode() == ExceptionCode.DUPLICATE_EMAIL_ERROR) {
                response.put("message", "중복된 이메일입니다.");
                response.put("status", HttpStatus.CONFLICT);
                return response;
            }
            throw e;
        }
        String authCode = emailSendService.joinEmail(email);
        response.put("message", "중복되지 않은 이메일입니다. 인증 메일을 발송했습니다.");
        response.put("authCode", authCode);
        response.put("status", HttpStatus.OK);
        return response;
    }

}
