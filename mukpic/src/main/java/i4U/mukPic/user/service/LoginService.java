package i4U.mukPic.user.service;

import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.user.dto.LoginRequestDTO;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.entity.UserStatus;
import i4U.mukPic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User login(LoginRequestDTO loginRequest) {
        // userId로 사용자 조회
        User user = userRepository.findByUserId(loginRequest.getUserId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
        }

        // 사용자 상태 확인
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_ACTIVE);
        }

        return user;
    }
}
