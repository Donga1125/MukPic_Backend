package i4U.mukPic.openai.controller;

import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.openai.service.ImageAnalysisService;
import i4U.mukPic.openai.service.OpenAIService;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class FoodInfoController {

    private final ImageAnalysisService imageAnalysisService;
    private final UserService userService;

    /**
     * 유저가 직접 키워드를 입력하여 음식 정보를 검색하는 API
     */
    @PostMapping("/info")
    public ResponseEntity<Map<String, Object>> getFoodInfoByKeyword(
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request
    ) {
        String keyword = requestBody.get("keyword");
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_REQUEST_BODY);
        }

        User user = userService.getUserFromRequest(request);

        // ImageAnalysisService 호출
        Map<String, Object> response = imageAnalysisService.getFoodInfoFromOpenAI(keyword, user);

        return ResponseEntity.ok(response);
    }
}