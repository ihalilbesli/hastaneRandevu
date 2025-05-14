package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.ClinicRepository;
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
import java.util.stream.Collectors;

@Service
public class AIServiceImpl implements AIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;

    public AIServiceImpl(UserRepository userRepository, ClinicRepository clinicRepository) {
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
    }

    @Override
    public String analyzeComplaint(String complaintText) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getRole() != User.Role.HASTA) {
            throw new RuntimeException("Yapay zeka sadece HASTA kullanıcılar tarafından kullanılabilir.");
        }

        // Klinikleri veritabanından çek
        List<String> clinicNames = clinicRepository.findAll()
                .stream()
                .map(Clinic::getName)
                .collect(Collectors.toList());

        // Klinik listesini metne çevir
        String clinicListText = String.join(", ", clinicNames);

        // OpenAI mesajı hazırla
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "user",
                "content", "Bir hastanın şikayeti: \"" + complaintText +
                        "\". Aşağıda verilen klinik listesine göre bu şikayete en uygun olanı öner. " +
                        "Sadece veritabanındaki kliniklere yönlendir, başka bir şey yazma. " +
                        "Ayrıca hastaya geleneksel tıbbi bir tavsiye ver. " +
                        "Klinikler:\n" + clinicListText +
                        "\nCevap formatı: Poliklinik: ...\nTavsiye: ... şeklinde olsun (Sadece yukarıdaki klinikler geçerli)."
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
