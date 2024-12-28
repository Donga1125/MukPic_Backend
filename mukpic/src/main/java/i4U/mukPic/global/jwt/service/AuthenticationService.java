package i4U.mukPic.global.jwt.service;

import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.entity.UserStatus;
import i4U.mukPic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
        }

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_ACTIVE);
        }

        return user;
    }
}
