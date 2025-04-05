package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hastarandevu/ai")
public class AIController {
    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }
    // Kullanıcının yazdığı şikayeti analiz edip yönlendirme ve tavsiye döner
    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeComplaint(@RequestBody ComplaintRequest request) {
        String response = aiService.analyzeComplaint(request.complaintText);
        return ResponseEntity.ok(response);
    }
    // JSON'dan veri alabilmek için iç sınıf
    public static class ComplaintRequest {
        public String complaintText;
    }
}
