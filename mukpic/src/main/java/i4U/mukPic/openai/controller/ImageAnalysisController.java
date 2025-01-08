package i4U.mukPic.openai.controller;

import i4U.mukPic.openai.service.ImageAnalysisService;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.service.UserService;
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
    private final UserService userService;

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

        // 2. UserService를 통해 사용자 정보 가져오기
        User user = userService.getUserFromRequest(request);

        // 3. ImageAnalysisService를 통해 비즈니스 로직 처리
        String result;
        try {
            result = imageAnalysisService.analyzeImageWithUser(imageUrl, user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        // 4. 최종 결과 반환
        return ResponseEntity.ok(result);
    }
}