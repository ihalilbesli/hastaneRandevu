package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AIService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final UserRepository userRepository;

    public AIServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Hastanın yazdığı şikayeti analiz eder, uygun poliklinik önerisi ve geleneksel tıbbi tavsiye verir.
     * Bu özellik sadece HASTA rolündeki kullanıcılar tarafından kullanılabilir.
     */
    @Override
    public String analyzeComplaint(String complaintText) {
        String email = SecurityUtil.getCurrentUserEmail();

        if (!SecurityUtil.hasRole("HASTA")) {
            throw new RuntimeException("Yapay zeka sadece HASTA kullanıcılar tarafından kullanılabilir.");
        }

        // OpenAI API isteğini hazırlıyoruz
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "user",
                "content", "Bir hastanın şikayeti: \"" + complaintText +
                        "\". Bu şikayete uygun polikliniği öner ve geleneksel tıbbi tavsiye ver. " +
                        "Cevap formatı: Poliklinik: ...\\nTavsiye: ... şeklinde olsun."
        ));

        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject responseBody = new JSONObject(response.getBody());
            return responseBody
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } else {
            return "Yapay zeka şu anda yanıt veremiyor. Lütfen daha sonra tekrar deneyin.";
        }
    }
}
