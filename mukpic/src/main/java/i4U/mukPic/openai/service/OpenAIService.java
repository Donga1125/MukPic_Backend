package i4U.mukPic.openai.service;

import i4U.mukPic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateFoodInfoWithUserDetails(String foodKeyword, User user) {
        // 사용자 정보를 활용한 메시지 생성
        String userDetails = String.format(
                "User Name: %s\nAllergies: %s\nChronic Diseases: %s\nDietary Preferences: %s",
                user.getUserName(),
                user.getAllergy() != null ? user.getAllergy().getAllergies() : "None",
                user.getChronicDisease() != null ? user.getChronicDisease().getDiseases() : "None",
                user.getDietaryPreference() != null ? user.getDietaryPreference().getPreferences() : "None"
        );

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 요청 Body 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", String.format(
                        "Reply only in English. Provide detailed information about the food '%s'. Use the following format:\n"
                                + "Food Name: [value]\n"
                                + "Food Description: [value]\n"
                                + "Ingredients: [value]\n"
                                + "Recipe: [value]\n"
                                + "Allergy Information: [value]\n\n"
                                + "Additionally, consider the following user details:\n%s",
                        foodKeyword, userDetails
                ))
        );

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 1000);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // OpenAI API 호출
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // 응답 처리
            Map<String, Object> responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null) {
                        return (String) message.get("content");
                    }
                }
            }

            throw new RuntimeException("OpenAI API 응답이 비어있습니다.");
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}