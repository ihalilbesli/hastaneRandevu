package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.Complaint;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.ClinicRepository;
import com.hastanerandevu.app.repository.ComplaintRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AIService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIServiceImpl implements AIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final ComplaintRepository complaintRepository;

    public AIServiceImpl(UserRepository userRepository, ClinicRepository clinicRepository, ComplaintRepository complaintRepository) {
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
        this.complaintRepository = complaintRepository;
    }

    @Override
    public String analyzeComplaint(String complaintText) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getRole() != User.Role.HASTA) {
            throw new RuntimeException("Yapay zeka sadece HASTA kullanıcılar tarafından kullanılabilir.");
        }

        List<String> clinicNames = clinicRepository.findAll()
                .stream()
                .map(Clinic::getName)
                .collect(Collectors.toList());

        String clinicListText = String.join(", ", clinicNames);

        String chronicInfo = currentUser.getChronicDiseases();
        boolean hasChronic = chronicInfo != null && !chronicInfo.trim().isEmpty();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Bir hastanın şikayeti: \"").append(complaintText).append("\".\n");

        if (hasChronic) {
            messageBuilder.append("Hastanın kronik rahatsızlığı: ").append(chronicInfo).append("\n");
        }

        messageBuilder.append("Aşağıda verilen klinik listesine göre bu şikayete en uygun olanı öner. ");
        messageBuilder.append("Sadece veritabanındaki kliniklere yönlendir. ");
        messageBuilder.append("Ayrıca hastaya geleneksel bir tıbbi tavsiye ver. ");
        messageBuilder.append("Klinikler:\n").append(clinicListText).append("\n");
        messageBuilder.append("Cevap formatı: Poliklinik: ...\nTavsiye: ... şeklinde olsun.");

        return sendToOpenAI(messageBuilder.toString());
    }

    @Override
    public String analyzeComplaintsForAdmin() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlem yalnızca admin tarafından yapılabilir.");
        }

        var complaints = complaintRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .limit(30)
                .toList();

        if (complaints.isEmpty()) return "Analiz yapılacak yeterli şikayet verisi yok.";

        StringBuilder prompt = new StringBuilder();
        prompt.append("Aşağıda hastaların şikayet içerikleri ve klinik bilgileri verilmiştir.\n");
        prompt.append("Lütfen bu verilere göre analiz yap. İçerikte bulunanlara göre aşağıdaki başlıkları değerlendir:\n");
        prompt.append("- Genel bulgular\n");
        prompt.append("- Klinik bazlı tespitler (eğer klinik bilgisi varsa)\n");
        prompt.append("- Sistemsel sorunlar (eğer şikayetlerde geçiyorsa)\n");
        prompt.append("- İletişim ve memnuniyet geri bildirimleri (varsa)\n");
        prompt.append("Her başlık varsa değerlendir, yoksa belirtme.\n");
        prompt.append("Sonuçları madde madde ve profesyonel bir dille sun.\n\n");

        int i = 1;
        for (Complaint c : complaints) {
            prompt.append(i++).append(". Şikayet: ").append(c.getSubject()).append(" — ").append(c.getContent());
            if (c.getClinic() != null) {
                prompt.append(" [Klinik: ").append(c.getClinic().getName()).append("]");
            }
            prompt.append("\n");
        }

        return sendToOpenAI(prompt.toString());
    }



    @Override
    public String analyzeClinicLoad() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlem yalnızca admin tarafından yapılabilir.");
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("Veritabanında bulunan kliniklerin isimleri aşağıda verilmiştir.\n");
        prompt.append("Bunların hastane içi iş yükü açısından nasıl bir dağılıma sahip olabileceğini analiz et.\n");
        prompt.append("Görsel veri yok. Yalnızca metin olarak genel bir yoğunluk analizi öner.\n\n");

        clinicRepository.findAll().forEach(c ->
                prompt.append("- ").append(c.getName()).append("\n")
        );

        return sendToOpenAI(prompt.toString());
    }

    @Override
    public String suggestClinicsToOpen() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlem yalnızca admin tarafından yapılabilir.");
        }

        List<Complaint> complaints = complaintRepository.findByCreatedAtAfter(LocalDate.now().minusMonths(3));

        StringBuilder prompt = new StringBuilder();
        prompt.append("Son 3 ayda gelen hasta şikayetleri aşağıdadır.\n");
        prompt.append("Bu verilere göre yeni klinik açılması gereken alanları öner. Mevcut kliniklerle kıyasla eksik olanları bul.\n\n");

        for (Complaint c : complaints) {
            prompt.append("- Konu: ").append(c.getSubject()).append(" — İçerik: ").append(c.getContent()).append("\n");
        }

        return sendToOpenAI(prompt.toString());
    }

    @Override
    public String analyzeUserBehavior() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlem yalnızca admin tarafından yapılabilir.");
        }

        List<User> users = userRepository.findAll();

        StringBuilder prompt = new StringBuilder();
        prompt.append("Aşağıda bazı kullanıcı bilgileri (yaş, cinsiyet, kan grubu, kronik hastalık) verilmiştir.\n");
        prompt.append("Bu verilere göre kullanıcı davranışları hakkında genel bir analiz yap.\n\n");

        for (User u : users) {
            prompt.append("- ").append(u.getName()).append(" ").append(u.getSurname());
            prompt.append(", Cinsiyet: ").append(u.getGender());
            prompt.append(", Kan Grubu: ").append(u.getBloodType());
            prompt.append(", Kronik: ").append(u.getChronicDiseases() != null ? u.getChronicDiseases() : "Yok");
            prompt.append("\n");
        }

        return sendToOpenAI(prompt.toString());
    }

    @Override
    public String generateRiskAlerts() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlem yalnızca admin tarafından yapılabilir.");
        }

        List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();

        StringBuilder prompt = new StringBuilder();
        prompt.append("Aşağıda gelen şikayetler verilmiştir.\n");
        prompt.append("Bunları analiz ederek:\n");
        prompt.append("- Belirli kliniklerde artan sorunları belirt,\n");
        prompt.append("- Riskli durumları erken uyarı olarak bildir.\n\n");

        for (Complaint c : complaints) {
            prompt.append("- Konu: ").append(c.getSubject()).append(" — İçerik: ").append(c.getContent());
            if (c.getClinic() != null) {
                prompt.append(" [Klinik: ").append(c.getClinic().getName()).append("]");
            }
            prompt.append("\n");
        }

        return sendToOpenAI(prompt.toString());
    }

    // Ortak OpenAI gönderimi
    private String sendToOpenAI(String prompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = List.of(Map.of(
                "role", "user",
                "content", prompt
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
