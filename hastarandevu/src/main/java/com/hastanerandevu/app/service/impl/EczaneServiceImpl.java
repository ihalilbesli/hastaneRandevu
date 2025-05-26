package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.dto.Ezcane.EczaneApiResponse;
import com.hastanerandevu.app.dto.Ezcane.EczaneDto;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.EczaneService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class EczaneServiceImpl implements EczaneService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${collectapi.key}")
    private String apiKey;

    public EczaneServiceImpl(RestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @Override
    public List<EczaneDto> getPharmacies(String city, String district) {
        // 🔒 Sadece HASTA rolü erişebilir
        if (!SecurityUtil.hasRole("HASTA")) {
            throw new RuntimeException("Bu veriye yalnızca hasta kullanıcılar erişebilir.");
        }

        try {
            String url = "https://api.collectapi.com/health/dutyPharmacy?il=" + city;
            if (district != null && !district.isBlank()) {
                url += "&ilce=" + district;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<EczaneApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    EczaneApiResponse.class
            );

            System.out.println("✅ Status: " + response.getStatusCode());
            System.out.println("📥 Body: " + response.getBody());

            return response.getBody().getResult();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Eczane verileri alınamadı!", e);
        }
    }
}
