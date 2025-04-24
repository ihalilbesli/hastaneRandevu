package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
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
    private static final List<String> CLINICS = List.of(
            "Kardiyoloji", "Aile Hekimliği", "Algoloji", "Amatem", "Anesteziyoloji ve Reanimasyon",
            "Beyin ve Sinir Cerrahisi", "Cerrahi Onkolojisi", "Çocuk Cerrahisi", "Çocuk Diş Hekimliği",
            "Çocuk Endokrinolojisi", "Çocuk Enfeksiyon Hastalıkları", "Çocuk Gastroenterolojisi",
            "Çocuk Genetik Hastalıkları", "Çocuk Göğüs Hastalıkları", "Çocuk Hematolojisi ve Onkolojisi",
            "Çocuk İmmünolojisi ve Alerji Hastalıkları", "Çocuk Kalp Damar Cerrahisi", "Çocuk Kardiyolojisi",
            "Çocuk Metabolizma Hastalıkları", "Çocuk Nefrolojisi", "Çocuk Nörolojisi", "Çocuk Romatolojisi",
            "Çocuk Sağlığı ve Hastalıkları", "Çocuk Ürolojisi", "Çocuk ve Ergen Madde ve Alkol Bağımlılığı",
            "Çocuk ve Ergen Ruh Sağlığı ve Hastalıkları", "Dahiliye", "Deri ve Zührevi Hastalıkları (Cildiye)",
            "Diş Hekimliği", "El Cerrahisi", "Endodonti", "Endokrinoloji ve Metabolizma Hastalıkları",
            "Enfeksiyon Hastalıkları ve Klinik Mikrobiyoloji", "Fiziksel Tıp ve Rehabilitasyon",
            "Gastroenteroloji", "Gastroenteroloji Cerrahisi", "Geleneksel Tamamlayıcı Tıp Ünitesi",
            "Gelişimsel Pediatri", "Genel Cerrahi", "Geriatri", "Göğüs Cerrahisi", "Göğüs Hastalıkları",
            "Göz Hastalıkları", "Glokom", "Retina", "Şaşılık", "Uvea", "Kornea", "Oküloplasti", "Hematoloji",
            "İmmünoloji ve Alerji Hastalıkları", "İş ve Meslek Hastalıkları", "Jinekolojik Onkoloji Cerrahisi",
            "Kadın Hastalıkları ve Doğum", "Kalp ve Damar Cerrahisi", "Klinik Nörofizyoloji",
            "Kulak Burun Boğaz Hastalıkları", "Nefroloji", "Neonatoloji", "Nöroloji", "Nükleer Tıp",
            "Ortodonti", "Ortopedi ve Travmatoloji", "Perinatoloji", "Periodontoloji",
            "Plastik, Rekonstrüktif ve Estetik Cerrahi", "Protetik Diş Tedavisi", "Radyasyon Onkolojisi",
            "Restoratif Diş Tedavisi", "Romatoloji", "Ruh Sağlığı ve Hastalıkları (Psikiyatri)",
            "Sağlık Kurulu Erişkin", "Sağlık Kurulu ÇÖZGER (Çocuk Özel Gereksinim Raporu)",
            "Sigarayı Bıraktırma Kliniği", "Spor Hekimliği", "Sualtı Hekimliği ve Hiperbarik Tıp",
            "Tıbbi Ekoloji ve Hidroklimatoloji", "Tıbbi Genetik", "Tıbbi Onkoloji", "Uyku Polikliniği",
            "Üroloji", "Ağız, Diş ve Çene Cerrahisi", "Ağız, Diş ve Çene Radyolojisi", "Radyoloji"
    );

    /**
     * Hastanın yazdığı şikayeti analiz eder, uygun poliklinik önerisi ve geleneksel tıbbi tavsiye verir.
     * Bu özellik sadece HASTA rolündeki kullanıcılar tarafından kullanılabilir.
     */
    @Override
    public String analyzeComplaint(String complaintText) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getRole() != User.Role.HASTA) {
            throw new RuntimeException("Yapay zeka sadece HASTA kullanıcılar tarafından kullanılabilir.");
        }

        // OpenAI API isteğini hazırlıyoruz
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "user",
                "content", "Bir hastanın şikayeti: \"" + complaintText +
                        "\". Aşağıda verilen klinik listesine göre bu şikayete en uygun olanı öner. " +
                        "Ayrıca hastaya geleneksel tıbbi bir tavsiye ver. " +
                        "Klinikler:\\n" + CLINICS +
                        "\\nCevap formatı: Poliklinik: ...\\nTavsiye: ... şeklinde olsun."
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
