package i4U.mukPic.user.controller;

import i4U.mukPic.user.dto.UserRequestDTO;
import i4U.mukPic.user.dto.UserResponseDTO;
import i4U.mukPic.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
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
        UserResponseDTO.DetailUserInfo detailUserInfo = userService.getUserInfo(userKey);
        return ResponseEntity.ok(detailUserInfo);
    }
}
