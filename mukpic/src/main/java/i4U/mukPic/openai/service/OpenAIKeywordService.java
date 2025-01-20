package i4U.mukPic.openai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import i4U.mukPic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIKeywordService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.api-url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getFoodDetailsByKeyword(String foodKeyword, User user) {
        // 유저 정보와 프롬프트 생성
        String userDetails = String.format(
                "User Name: %s\nAllergies: %s\nChronic Diseases: %s\nDietary Preferences: %s",
                user.getUserName(),
                user.getAllergy() != null ? user.getAllergy().getAllergies() : "None",
                user.getChronicDisease() != null ? user.getChronicDisease().getDiseases() : "None",
                user.getDietaryPreference() != null ? user.getDietaryPreference().getPreferences() : "None"
        );

        String prompt = String.format(
                "Reply with only the following JSON object when asked about the food '%s': " +
                        "{ " +
                        "\"foodName\": \"<음식 이름 (한국어)>\", " +
                        "\"engFoodName\": \"<English food name>\", " +
                        "\"foodDescription\": \"<description in English>\", " +
                        "\"ingredients\": [\"<ingredient1 in English>\", \"<ingredient2 in English>\", ...], " +
                        "\"recipe\": [\"<recipe1 in English>\", \"<recipe2 in English>\", ...]," +
                        "\"allergyInformation\": \"<customized allergy information based on user details>\", " +
                        "}. For the allergyInformation field, provide details based on the user's profile: %s. " +
                        "The 'foodName' must be in Korean, and all other fields should be in English. Do not include any other text or explanation.",
                foodKeyword, userDetails
        );

        // OpenAI API 요청 생성
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a helpful assistant."),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", 1000
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

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
                        String content = (String) message.get("content");
                        return parseFoodInfoResponse(content);
                    }
                }
            }

            throw new RuntimeException("OpenAI API 응답이 비어있습니다.");
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> parseFoodInfoResponse(String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 부분만 추출
            int jsonStart = content.indexOf('{');
            int jsonEnd = content.lastIndexOf('}');
            if (jsonStart != -1 && jsonEnd != -1) {
                String json = content.substring(jsonStart, jsonEnd + 1);
                return objectMapper.readValue(json, Map.class);
            } else {
                throw new RuntimeException("JSON 형식이 응답에 포함되지 않았습니다: " + content);
            }
        } catch (Exception e) {
            System.err.println("OpenAI 응답 원본: " + content);
            throw new RuntimeException("JSON 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}
