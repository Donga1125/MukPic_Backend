package i4U.mukPic.user.controller;

import i4U.mukPic.global.auth.entity.Token;
import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.global.jwt.service.TokenService;
import i4U.mukPic.user.dto.UserRequestDTO;
import i4U.mukPic.user.dto.UserResponseDTO;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO.DetailUserInfo> register(@Valid @RequestBody UserRequestDTO.Register register) {
        UserResponseDTO.DetailUserInfo detailUserInfo = userService.createUser(register);
        return ResponseEntity.ok(detailUserInfo);
    }

    @GetMapping("/{userKey}")
    public ResponseEntity<UserResponseDTO.DetailUserInfo> getUserInfo(@PathVariable Long userKey) {
        User user = userService.getUserInfo(userKey);
        UserResponseDTO.DetailUserInfo detailUserInfo = new UserResponseDTO.DetailUserInfo(user);
        return ResponseEntity.ok(detailUserInfo);
    }

    @PatchMapping("/editUserInfo/{userKey}")
    public ResponseEntity<UserResponseDTO.DetailUserInfo> updateUser(
            @PathVariable Long userKey,
            @RequestBody @Valid UserRequestDTO.Patch patch) {

        UserResponseDTO.DetailUserInfo updatedUser = userService.updateUser(userKey, patch);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/editPassword")
    public ResponseEntity updatePasswordInfo(@Valid @RequestBody UserRequestDTO.UpdatePassword updatePassword) {
        userService.updatePassword(updatePassword.getEmail(), updatePassword.getPassword());

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<String> deactivateUser(HttpServletRequest request) {
        try {
            String accessToken = jwtTokenProvider.extractAccessToken(request)
                    .orElseThrow(() -> new RuntimeException("Access Token이 없습니다."));

            accessToken = refreshAccessTokenIfExpired(accessToken);

            // 새 Access Token으로 사용자 ID 추출
            String userId = jwtTokenProvider.extractSubject(accessToken)
                    .orElseThrow(() -> new RuntimeException("토큰에서 사용자 ID 정보를 가져올 수 없습니다."));

            // 회원 탈퇴 처리
            userService.deactivateMember(userId);

            return ResponseEntity.ok("회원 비활성화 성공");
        } catch (RuntimeException e) {
            log.error("Error during deactivation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
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
}
