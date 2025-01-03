package i4U.mukPic.user.controller;

import i4U.mukPic.global.auth.TokenKey; // TokenKey 클래스 임포트
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
        responseBody.put("refreshToken", tokens.get("refreshToken"));

        // 응답 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokens.get("accessToken"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

}
