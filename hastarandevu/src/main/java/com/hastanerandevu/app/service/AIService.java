package com.hastanerandevu.app.service;

import java.util.List;

public interface AIService {

    String analyzeComplaint(String complaintText);


    // 1️⃣ Şikayetleri analiz et → Klinik ve çözüm öner
    String analyzeComplaintsForAdmin();

    // 2️⃣ Klinik yoğunluklarını analiz et
    String analyzeClinicLoad();


    // 4️⃣ Kullanıcı davranışlarını analiz et
    String analyzeUserBehavior();

    // 5️⃣ Riskli durumları ve erken uyarıları döndür
    String generateRiskAlerts();

    String analyzeChart(String chartTitle, List<String> labels, List<Long> values);

}
