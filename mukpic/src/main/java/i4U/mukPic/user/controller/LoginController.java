package i4U.mukPic.user.controller;

import i4U.mukPic.user.dto.LoginRequestDTO;
import i4U.mukPic.user.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        // 로그인 서비스 호출
        Map<String, String> tokens = loginService.login(loginRequest);

        // JSON 응답 본문 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("message", "Login successful");

        // 응답 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokens.get("accessToken"));
        headers.add("refreshToken", tokens.get("refreshToken"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

    @PostMapping("/email-login")
    public ResponseEntity<Map<String, Object>> loginWithEmail(@RequestBody Map<String, String> requestBody) {
        // 요청에서 이메일 추출
        String email = requestBody.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }

        // 로그인 서비스 호출
        Map<String, String> tokens = loginService.loginWithEmail(email);

        // JSON 응답 본문 생성
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("message", "Login successful");

        // 응답 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokens.get("accessToken"));
        headers.add("refreshToken", tokens.get("refreshToken"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authorization) {
        // Authorization 헤더에서 Bearer 제거
        String accessToken = authorization.replace("Bearer ", "");

        // 로그아웃 서비스 호출
        loginService.logout(accessToken);

        // 응답 본문 생성
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("message", "Logout successful");

        return ResponseEntity.ok(responseBody);
    }

}
