package i4U.mukPic.openai.service;

import i4U.mukPic.openai.entity.FoodInfo;
import i4U.mukPic.openai.repository.FoodInfoRepository;
import i4U.mukPic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageAnalysisService {

    private final WebClient webClient;
    private final OpenAIService openAIService;
    private final FoodInfoRepository foodInfoRepository;

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

    // 이미지 분석 및 데이터 조합 메서드
    public Map<String, Object> analyzeImageWithUser(String imageUrl, User user) {
        // 1. FastAPI 호출 및 음식 키워드 추출
        Map<String, Object> fastAPIResponse = callFastAPIServer(imageUrl);
        String foodName = extractResult(fastAPIResponse);

        // 2. FoodInfo 데이터베이스 조회
        FoodInfo foodInfo = foodInfoRepository.findByFoodName(foodName)
                .orElseThrow(() -> new RuntimeException("해당 Food 정보를 찾을 수 없습니다: " + foodName));

        // 3. OpenAI 호출 (Allergy Information 및 사용자 정보만 요청)
        String allergyInfo;
        try {
            allergyInfo = openAIService.generateFoodInfoWithUserDetails(foodName, user);
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        }

        // 4. 데이터베이스 값과 OpenAI 응답 조합
        Map<String, Object> response = new HashMap<>();
        response.put("foodName", foodInfo.getFoodName());
        response.put("engFoodName", foodInfo.getEngFoodName());
        response.put("foodDescription", foodInfo.getDescription());
        response.put("ingredients", foodInfo.getIngredients());
        response.put("recipe", foodInfo.getRecipe());
        response.put("allergyInformation", allergyInfo);

        return response;
    }

    public Map<String, Object> getFoodInfoWithKeyword(String keyword, User user) {
        // 1. FoodInfo 데이터베이스에서 음식 정보 조회
        FoodInfo foodInfo = foodInfoRepository.findByFoodName(keyword)
                .orElseThrow(() -> new RuntimeException("해당 Food 정보를 찾을 수 없습니다: " + keyword));

        // 2. OpenAI API 호출 (유저 정보와 키워드 전달)
        String allergyInfo;
        try {
            allergyInfo = openAIService.generateFoodInfoWithUserDetails(keyword, user);
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API 호출 실패: " + e.getMessage());
        }

        // 3. FoodInfo와 OpenAI 응답 데이터를 조합
        Map<String, Object> response = new HashMap<>();
        response.put("foodName", foodInfo.getFoodName());
        response.put("engFoodName", foodInfo.getEngFoodName());
        response.put("foodDescription", foodInfo.getDescription());
        response.put("ingredients", foodInfo.getIngredients());
        response.put("recipe", foodInfo.getRecipe());
        response.put("allergyInformation", allergyInfo);

        return response;
    }

}