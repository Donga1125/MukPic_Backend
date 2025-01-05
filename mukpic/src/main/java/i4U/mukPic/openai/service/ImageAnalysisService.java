package i4U.mukPic.openai.service;

import i4U.mukPic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageAnalysisService {

    private final WebClient webClient;
    private final OpenAIService openAIService;

    // FastAPI 호출 메서드
    public Map<String, Object> callFastAPIServer(String imageUrl) {
        try {
            // FastAPI로 이미지 URL 전송
            return webClient.post()
                    .uri("/predict/") // FastAPI 엔드포인트
                    .header("Content-Type", "application/json")
                    .bodyValue(Map.of("url", imageUrl))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 실패: " + e.getMessage());
        }
    }

    // 응답 데이터에서 필요한 값 추출
    public String extractResult(Map<String, Object> fastAPIResponse) {
        if (fastAPIResponse != null && fastAPIResponse.containsKey("result")) {
            return (String) fastAPIResponse.get("result");
        } else {
            throw new RuntimeException("FastAPI 응답이 비어있거나 잘못되었습니다.");
        }
    }

    // 이미지 분석 및 OpenAI 호출 전체 로직
    public String analyzeImageWithUser(String imageUrl, User user) {
        // 1. FastAPI 호출 및 결과 추출
        Map<String, Object> fastAPIResponse = callFastAPIServer(imageUrl);
        String result = extractResult(fastAPIResponse);

        // 2. OpenAI API 호출 (FastAPI 결과와 사용자 정보 사용)
        try {
            return openAIService.generateFoodInfoWithUserDetails(result, user);
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        }
    }
}
