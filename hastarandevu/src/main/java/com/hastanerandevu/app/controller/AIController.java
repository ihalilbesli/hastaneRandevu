package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.dto.Analytics.ChartAnalysisRequest;
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

    // 1 Kullanıcının yazdığı şikayeti analiz edip yönlendirme ve tavsiye döner
    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeComplaint(@RequestBody ComplaintRequest request) {
        if (request.complaintText == null || request.complaintText.trim().isEmpty()) {
            throw new RuntimeException("Şikayet metni boş olamaz.");
        }
        String response = aiService.analyzeComplaint(request.complaintText);
        return ResponseEntity.ok(response);
    }

    // 2 Admin: Şikayetleri analiz et
    @GetMapping("/admin/analyze-complaints")
    public ResponseEntity<String> analyzeComplaintsForAdmin() {
        String result = aiService.analyzeComplaintsForAdmin();
        return ResponseEntity.ok(result);
    }

    // 3 Admin: Klinik yoğunluğu analizi
    @GetMapping("/admin/analyze-clinic-load")
    public ResponseEntity<String> analyzeClinicLoad() {
        String result = aiService.analyzeClinicLoad();
        return ResponseEntity.ok(result);
    }

    // 4 Admin: Kullanıcı davranış analizi
    @GetMapping("/admin/analyze-user-behavior")
    public ResponseEntity<String> analyzeUserBehavior() {
        String result = aiService.analyzeUserBehavior();
        return ResponseEntity.ok(result);
    }

    // 5 Admin: Riskli durumları tespit et
    @GetMapping("/admin/risk-alerts")
    public ResponseEntity<String> generateRiskAlerts() {
        String result = aiService.generateRiskAlerts();
        return ResponseEntity.ok(result);
    }

    // 6 Grafik verisi analiz et (bar chart, pie chart vb.)
    @PostMapping("/analyze-graph")
    public ResponseEntity<String> analyzeGraph(@RequestBody ChartAnalysisRequest req) {
        if (req.getChartTitle() == null || req.getLabels() == null || req.getValues() == null) {
            throw new RuntimeException("Grafik analizi için başlık, etiket ve değerler zorunludur.");
        }
        String response = aiService.analyzeChart(req.getChartTitle(), req.getLabels(), req.getValues());
        return ResponseEntity.ok(response);
    }

    //  JSON body'den complaintText almak için basit DTO
    public static class ComplaintRequest {
        public String complaintText;
    }
}
