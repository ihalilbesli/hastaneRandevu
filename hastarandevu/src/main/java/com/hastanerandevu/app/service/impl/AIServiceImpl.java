package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.Complaint;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.AppointmentRepository;
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
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AIServiceImpl implements AIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;
    private final ComplaintRepository complaintRepository;
    private  final AppointmentRepository appointmentRepository;

    public AIServiceImpl(UserRepository userRepository, ClinicRepository clinicRepository, ComplaintRepository complaintRepository, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
        this.complaintRepository = complaintRepository;
        this.appointmentRepository = appointmentRepository;
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

        LocalDate startDate = LocalDate.now().minusDays(30);
        List<Clinic> clinics = clinicRepository.findAll();
        List<Appointments> recentAppointments = appointmentRepository.findByDateAfter(startDate);
        List<Complaint> recentComplaints = complaintRepository.findByCreatedAtAfter(startDate);

        Map<Clinic, List<Appointments>> clinicAppointmentMap = recentAppointments.stream()
                .collect(Collectors.groupingBy(Appointments::getClinic));

        Map<Clinic, List<Complaint>> clinicComplaintMap = recentComplaints.stream()
                .filter(c -> c.getClinic() != null)
                .collect(Collectors.groupingBy(Complaint::getClinic));

        StringBuilder prompt = new StringBuilder();
        prompt.append("Aşağıda son 1 ayda kliniklerin randevu ve şikayet verileri listelenmiştir.\n");
        prompt.append("Her klinik için ayrı analiz yap:\n");
        prompt.append("- Randevu ve şikayet sayılarını değerlendir,\n");
        prompt.append("- Klinik çok yoğunsa detaylı analiz yap ve profesyonel önerilerde bulun,\n");
        prompt.append("- Gerekirse ikinci doktor öner veya sistemsel iyileştirme sun,\n");
        prompt.append("- Şikayet yoksa kısa ve yeterli bir değerlendirme yap.\n\n");

        for (Clinic clinic : clinics) {
            int randevuSayisi = clinicAppointmentMap.getOrDefault(clinic, List.of()).size();
            int sikayetSayisi = clinicComplaintMap.getOrDefault(clinic, List.of()).size();

            prompt.append("🔹 Klinik: ").append(clinic.getName()).append("\n");
            prompt.append("- Randevu sayısı: ").append(randevuSayisi).append("\n");
            prompt.append("- Şikayet sayısı: ").append(sikayetSayisi).append("\n");

            if (randevuSayisi > 5 || sikayetSayisi > 1) {
                prompt.append("➡️ Bu klinik için detaylı analiz yap. Yoğunluk veya memnuniyetsizlik varsa nedenlerini yorumla.\n");
                prompt.append("➡️ Gerekirse: ikinci doktor öner, ileriye dönük stratejik adımlar sun.\n");
            } else {
                prompt.append("➡️ Bu klinik için kısa değerlendirme sun. Şikayet yoksa olumlu şekilde belirt.\n");
            }

            prompt.append("\n");
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
        prompt.append("""
        Aşağıda hastane sistemine kayıtlı kullanıcıların (hastaların) yaş, cinsiyet, kan grubu ve kronik hastalık bilgileri yer almaktadır.

        Lütfen bu verileri yöneticiye (admin) yönelik profesyonel analiz raporu olarak değerlendir:

        1. Yaş grubu, cinsiyet, kan grubu ve kronik hastalık dağılımını yüzdeli ve sayısal olarak özetle.
        2. Eksik veri yüzdesini belirt (özellikle yaş, cinsiyet, kan grubu).
        3. Yaş grubuna göre hastalık riski analizi yap (örn. yaşlılarda hipertansiyon yaygın mı?).
        4. Yöneticiye özel aksiyon önerileri sun (eksik veri tamamlama, yaşlı/kronik hastalar için izlem, kadın sağlığı birimi gerekliliği gibi).
        
        Kullanıcı Verileri:
        """);

        // Yaş grubu istatistikleri
        Map<String, Integer> ageGroupStats = new HashMap<>();
        int totalUsers = users.size();
        int missingBirthDate = 0;

        for (User u : users) {
            int age = calculateAge(u.getBirthDate());
            String ageGroup = getAgeGroup(age);
            ageGroupStats.merge(ageGroup, 1, Integer::sum);

            prompt.append("- ").append(u.getName()).append(" ").append(u.getSurname());
            prompt.append(", Yaş: ").append(age > 0 ? age : "Bilinmiyor");
            prompt.append(", Yaş Grubu: ").append(ageGroup);
            prompt.append(", Cinsiyet: ").append(u.getGender());
            prompt.append(", Kan Grubu: ").append(u.getBloodType() != null ? u.getBloodType() : "Belirtilmemiş");
            prompt.append(", Kronik Hastalık: ").append(
                    (u.getChronicDiseases() != null && !u.getChronicDiseases().isBlank()) ? u.getChronicDiseases() : "Yok"
            );
            prompt.append("\n");

            if (age < 0) missingBirthDate++;
        }

        prompt.append("\nYaş Grubu Dağılımı:\n");
        for (Map.Entry<String, Integer> entry : ageGroupStats.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalUsers;
            prompt.append("- ").append(entry.getKey()).append(": ")
                    .append(entry.getValue()).append(" kullanıcı (")
                    .append(String.format("%.1f", percentage)).append("%)\n");
        }

        prompt.append("\nEksik Bilgi:\n");
        prompt.append("- Yaşı bilinmeyen kullanıcı sayısı: ").append(missingBirthDate).append("\n");

        return sendToOpenAI(prompt.toString());
    }


    @Override
    public String generateRiskAlerts() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlem yalnızca admin tarafından yapılabilir.");
        }

        List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();
        List<Appointments> allAppointments = appointmentRepository.findAll();

        LocalDate oneWeekAgo = LocalDate.now().minusDays(7);
        List<Appointments> recentAppointments = allAppointments.stream()
                .filter(a -> a.getDate().isAfter(oneWeekAgo))
                .toList();

        Map<String, Long> complaintCountByClinic = complaints.stream()
                .filter(c -> c.getClinic() != null)
                .collect(Collectors.groupingBy(c -> c.getClinic().getName(), Collectors.counting()));

        Map<String, List<Appointments>> appointmentsByClinic = recentAppointments.stream()
                .filter(a -> a.getClinic() != null)
                .collect(Collectors.groupingBy(a -> a.getClinic().getName()));

        StringBuilder prompt = new StringBuilder();
        prompt.append("""
        Aşağıda hastaneye ait son şikayet kayıtları ve son 1 haftalık randevu geçmişi verilmiştir.

        Lütfen bu veriler ışığında aşağıdaki kriterlere göre erken uyarı ve risk değerlendirmesi yap:
        - Aynı kliniğe gelen tekrar eden şikayetler
        - Artan randevu iptali ve geç gelme oranları
        - Kliniklerde memnuniyetsizlik trendi
        - Sistemsel eksiklikler

        Her durum için aşağıdaki şablonu kullan:
        🔹 Klinik/Sistem Adı
        - Uyarı Sebebi:
        - Erken Uyarı Notu:
        - Yöneticiye Stratejik Öneri:
        \n
        """);

        for (var entry : appointmentsByClinic.entrySet()) {
            String clinicName = entry.getKey();
            List<Appointments> clinicAppointments = entry.getValue();
            long total = clinicAppointments.size();
            long iptal = clinicAppointments.stream().filter(a -> a.getStatus() == Appointments.Status.IPTAL_EDILDI).count();
            long gecKalan = clinicAppointments.stream().filter(a -> a.getStatus() == Appointments.Status.GEC_KALINDI).count();

            double iptalOran = (double) iptal / total * 100;
            double gecOran = (double) gecKalan / total * 100;
            long sikayetSayisi = complaintCountByClinic.getOrDefault(clinicName, 0L);

            if (iptalOran >= 20 || gecOran >= 20 || sikayetSayisi >= 3) {
                prompt.append("🔹 Klinik: ").append(clinicName).append("\n");
                prompt.append("- Uyarı Sebebi: ");
                if (iptalOran >= 20) prompt.append("Yüksek randevu iptal oranı (").append(String.format("%.1f", iptalOran)).append("%). ");
                if (gecOran >= 20) prompt.append("Geç kalınan randevu oranı yüksek (").append(String.format("%.1f", gecOran)).append("%). ");
                if (sikayetSayisi >= 3) prompt.append("3+ tekrar eden şikayet. ");
                prompt.append("\n");

                prompt.append("- Erken Uyarı Notu: ");
                prompt.append("Bu klinikte son 1 haftada ").append(total).append(" randevu planlandı. ")
                        .append(iptal).append(" iptal (%").append(String.format("%.1f", iptalOran)).append("), ")
                        .append(gecKalan).append(" geç kalma (%").append(String.format("%.1f", gecOran)).append(") tespit edildi.\n");

                prompt.append("- Yöneticiye Stratejik Öneri: Klinik süreçleri gözden geçirilmeli. Randevu iptalleri için SMS/email hatırlatma artırılmalı, personel eğitimi verilmeli. ");
                if (sikayetSayisi >= 3) {
                    prompt.append("Ayrıca şikayet konuları analiz edilerek çözüm süreci başlatılmalı.");
                }
                prompt.append("\n\n");
            }
        }

        prompt.append("📌 Not: Yalnızca yüksek riskli klinikler listelenmiştir. Diğer kliniklerde ciddi bir risk bulunmamaktadır.\n");

        return sendToOpenAI(prompt.toString());
    }
    @Override
    public String analyzeChart(String chartTitle, List<String> labels, List<Long> values) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlem yalnızca admin tarafından yapılabilir.");
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("Grafik başlığı: ").append(chartTitle).append("\n");
        prompt.append("Aşağıda grafik verileri (etiket ve sayısal değer) listelenmiştir:\n");

        for (int i = 0; i < labels.size(); i++) {
            prompt.append("- ").append(labels.get(i)).append(": ").append(values.get(i)).append("\n");
        }

        prompt.append("""
Yukarıdaki grafik verilerine göre aşağıdaki kurallara uyarak profesyonel ve sezgisel bir yorum üret:

- Veriyi tekrar etme, grafik üzerinden anlam çıkar.
- Kategoriler arasındaki farkları değerlendir. Öne çıkan ya da geri kalan varsa belirt.
- Sayısal dengesizlik varsa neden sonuç kurarak yorum yap (örn: “X grubunun yüksekliği, Y hizmetine olan talebi artırabilir.”).
- Cevaplar yöneticinin strateji geliştirmesine yardımcı olacak yorumlar içermeli. Yani analiz odaklı olmalı.
- Teknik terimlerden kaçın, sade ve anlaşılır yaz.
- Kesin hükümler verme, “olabilir, gösterebilir, dikkat çekici” gibi nötr dil kullan.
-  **Not:** Eğer grafik “Kan Grubu Dağılımı” içeriyorsa, bu grafik hasta profilini gösterir, kan bağışı ile ilgili değildir.
""");

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


    private int calculateAge(String birthDateStr) {
        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (Exception e) {
            return -1; // bilinmiyor
        }
    }

    private String getAgeGroup(int age) {
        if (age < 0) return "Bilinmiyor";
        if (age < 18) return "Çocuk";
        else if (age < 35) return "Genç";
        else if (age < 60) return "Orta Yaş";
        else return "Yaşlı";
    }
}
