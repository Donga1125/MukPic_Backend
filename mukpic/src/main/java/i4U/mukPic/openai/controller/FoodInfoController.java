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
@RequestMapping("/food")
public class FoodInfoController {

    private final OpenAIService openAIService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/info")
    public ResponseEntity<String> getFoodInfo(
            @RequestBody Map<String, String> requestBody,
            HttpServletRequest request
    ) {
        String foodKeyword = requestBody.get("foodKeyword");

        // JWT에서 userId 추출
        String userId = jwtTokenProvider.extractUserIdFromRequest(request)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.INVALID_TOKEN_ERROR));

        // userId로 사용자 정보 조회
        User user = userService.checkUserByUserId(userId);

        // OpenAI 요청 생성 및 응답 반환
        String response = openAIService.generateFoodInfoWithUserDetails(foodKeyword, user);
        return ResponseEntity.ok(response);
    }
}
