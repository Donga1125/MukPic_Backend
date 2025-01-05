package i4U.mukPic.openai.controller;

import i4U.mukPic.openai.service.ImageAnalysisService;
import i4U.mukPic.openai.service.OpenAIService;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.service.UserService;
import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageAnalysisController {

    private final ImageAnalysisService imageAnalysisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final OpenAIService openAIService;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeImage(
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request
    ) {
        // 1. 요청 데이터에서 이미지 URL 추출
        String imageUrl = requestBody.get("imageUrl");
        if (imageUrl == null || imageUrl.isBlank()) {
            return ResponseEntity.badRequest().body("이미지 URL이 유효하지 않습니다.");
        }

        // 2. FastAPI 호출하여 결과(result) 가져오기
        String result;
        try {
            result = imageAnalysisService.analyzeImage(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("FastAPI 호출 실패: " + e.getMessage());
        }

        // 3. JWT에서 userId 추출
        String userId = jwtTokenProvider.extractUserIdFromRequest(request)
                .orElseThrow(() -> new RuntimeException("JWT 토큰이 유효하지 않습니다."));

        // 4. userId로 사용자 정보 조회
        User user = userService.checkUserByUserId(userId);

        // 5. OpenAI API 호출 (유저 정보와 result 전달)
        String openAIResponse;
        try {
            openAIResponse = openAIService.generateFoodInfoWithUserDetails(result, user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("OpenAI API 호출 실패: " + e.getMessage());
        }

        // 6. 최종 결과 반환
        return ResponseEntity.ok(openAIResponse);
    }
}
