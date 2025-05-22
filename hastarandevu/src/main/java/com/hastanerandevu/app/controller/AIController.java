package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    // 2️ Admin: Şikayetleri analiz et
    @GetMapping("/admin/analyze-complaints")
    public ResponseEntity<String> analyzeComplaintsForAdmin() {
        String result = aiService.analyzeComplaintsForAdmin();
        return ResponseEntity.ok(result);
    }

    // 3️ Admin: Klinik yoğunluğu analizi
    @GetMapping("/admin/analyze-clinic-load")
    public ResponseEntity<String> analyzeClinicLoad() {
        String result = aiService.analyzeClinicLoad();
        return ResponseEntity.ok(result);
    }

    // 4️ Admin: Açılması gereken yeni klinikler
    @GetMapping("/admin/suggest-clinics")
    public ResponseEntity<String> suggestClinicsToOpen() {
        String result = aiService.suggestClinicsToOpen();
        return ResponseEntity.ok(result);

    }

    // 5 Admin: Kullanıcı davranış analizi
    @GetMapping("/admin/analyze-user-behavior")
    public ResponseEntity<String> analyzeUserBehavior() {
        String result = aiService.analyzeUserBehavior();
        return ResponseEntity.ok(result);
    }

    // 6 Admin: Riskli durumları tespit et
    @GetMapping("/admin/risk-alerts")
    public ResponseEntity<String> generateRiskAlerts() {
        String result = aiService.generateRiskAlerts();
        return ResponseEntity.ok(result);
    }


    // JSON'dan veri alabilmek için iç sınıf
    public static class ComplaintRequest {
        public String complaintText;
    }

}
