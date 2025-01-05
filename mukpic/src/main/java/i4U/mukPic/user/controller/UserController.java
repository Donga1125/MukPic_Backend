package i4U.mukPic.user.controller;

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
            // 서비스 계층에 요청 위임
            userService.deactivateUser(request);
            return ResponseEntity.ok("회원 비활성화 성공");
        } catch (RuntimeException e) {
            log.error("Error during deactivation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
