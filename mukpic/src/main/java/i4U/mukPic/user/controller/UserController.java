package i4U.mukPic.user.controller;

import i4U.mukPic.email.service.EmailSendService;
import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailSendService emailSendService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO.DetailUserInfo> register(@Valid @RequestBody UserRequestDTO.Register register) {
        UserResponseDTO.DetailUserInfo detailUserInfo = userService.createUser(register);
        return ResponseEntity.ok(detailUserInfo);
    }

    @GetMapping("/myinfo")
    public ResponseEntity<UserResponseDTO.DetailUserInfo> getUserInfo(HttpServletRequest request) {
        User user = userService.getUserFromRequest(request);
        UserResponseDTO.DetailUserInfo detailUserInfo = new UserResponseDTO.DetailUserInfo(user);
        return ResponseEntity.ok(detailUserInfo);
    }

    @PatchMapping("/editUserInfo")
    public ResponseEntity<UserResponseDTO.DetailUserInfo> updateUserInfo(
            @RequestBody @Valid UserRequestDTO.Patch patch, HttpServletRequest request) {
        UserResponseDTO.DetailUserInfo updatedUser = userService.updateUserFromRequest(patch, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/editPassword")
    public ResponseEntity<String> updatePassword(
            @Valid @RequestBody UserRequestDTO.UpdatePassword updatePassword, HttpServletRequest request) {
        userService.updatePasswordFromRequest(updatePassword.getPassword(), request);
        return ResponseEntity.ok("비밀번호 변경이 완료되었습니다.");
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<String> deactivateUser(HttpServletRequest request) {
        try {
            userService.deactivateUser(request);
            return ResponseEntity.ok("회원 비활성화 성공");
        } catch (RuntimeException e) {
            log.error("Error during deactivation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/checkUserId")
    public ResponseEntity<Boolean> checkUserIdDuplicate(@RequestParam String userId) {
        boolean isDuplicate = userService.isUserIdDuplicate(userId);
        return ResponseEntity.ok(isDuplicate);
    }

    @GetMapping("/checkEmail")
    public ResponseEntity<Map<String, String>> checkEmailDuplicate(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            UserRequestDTO.Register register = new UserRequestDTO.Register();
            register.setEmail(email);
            User user = userService.checkUserStatus(register);

            String code = null;

            if (user == null) {
                code = emailSendService.joinEmail(email);
                response.put("message", "중복되지 않은 이메일입니다. 인증 메일을 발송했습니다.");
            } else {
                code = emailSendService.joinEmail(email);
                response.put("message", "재가입 유저입니다. 계정을 활성화하고 인증 메일을 발송했습니다.");
            }

            response.put("code", code);
            return ResponseEntity.ok(response);

        } catch (BusinessLogicException ex) {
            if (ExceptionCode.DUPLICATE_EMAIL_ERROR == ex.getExceptionCode()) {
                response.put("message", "중복된 이메일입니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            response.put("message", "알 수 없는 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/checkUserName")
    public ResponseEntity<Boolean> checkUserNameDuplicate(@RequestParam String userName) {
        boolean isDuplicate = userService.isUserNameDuplicate(userName);
        return ResponseEntity.ok(isDuplicate);
    }

}
