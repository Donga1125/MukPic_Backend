package i4U.mukPic.openai.controller;

import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.global.jwt.security.JwtTokenProvider;
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

    private final OpenAIService openAIService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 유저가 직접 키워드를 입력하여 음식 정보를 검색하는 API
     */
    @PostMapping("/info")
    public ResponseEntity<String> getFoodInfoByKeyword(
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request
    ) {
        // 1. 유저가 입력한 키워드 추출
        String keyword = requestBody.get("keyword");
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_REQUEST_BODY);
        }

        // 2. JWT에서 userId 추출
        String userId = jwtTokenProvider.extractUserIdFromRequest(request)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_TOKEN_ERROR));

        // 3. userId로 사용자 정보 조회
        User user = userService.checkUserByUserId(userId);

        // 4. OpenAI API 호출 (유저 정보와 키워드 전달)
        String response;
        try {
            response = openAIService.generateFoodInfoWithUserDetails(keyword, user);
        } catch (Exception e) {
            throw new BusinessLogicException(ExceptionCode.OPENAI_API_ERROR);
        }

        // 5. 최종 결과 반환
        return ResponseEntity.ok(response);
    }
}
