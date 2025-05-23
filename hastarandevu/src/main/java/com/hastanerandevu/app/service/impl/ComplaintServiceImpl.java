package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.Complaint;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.ClinicRepository;
import com.hastanerandevu.app.repository.ComplaintRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.ComplaintService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final ClinicRepository clinicRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository, UserRepository userRepository, ClinicRepository clinicRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.clinicRepository = clinicRepository;
    }

    /**
     * Kullanıcı şikayet oluşturur.
     * Yalnızca giriş yapan kullanıcı kendi adına şikayet oluşturabilir.
     */
    @Override
    public Complaint createComplaint(Complaint complaint) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);


        if (currentUser.getId()!=(complaint.getUser().getId())) {
            throw new RuntimeException("Sadece kendi adınıza şikayet oluşturabilirsiniz.");
        }
        // Eğer klinik bilgisi varsa veritabanından doğrula
        if (complaint.getClinic() != null) {
            Clinic clinic = clinicRepository.findById(complaint.getClinic().getId())
                    .orElseThrow(() -> new RuntimeException("Klinik bulunamadı."));
            complaint.setClinic(clinic);
        } else {
            complaint.setClinic(null); // Klinik seçilmemişse açıkça null olarak ayarla
        }

        complaint.setUser(currentUser);

        return complaintRepository.save(complaint);
    }


    /**
     * Kullanıcı ID’sine göre şikayetleri getirir.
     * Kullanıcı kendi şikayetlerini görebilir, admin tüm kullanıcıları görebilir.
     */
    @Override
    public List<Complaint> getComplaintByUserId(Long id) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);


        if (currentUser.getId()!=(id) && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi şikayetlerinizi görüntüleyebilirsiniz.");
        }

        User user = userRepository.findById(id).orElseThrow();
        return complaintRepository.findByUser(user);
    }

    /**
     * Kullanıcının belirli zaman aralığında yaptığı şikayetleri getirir.
     * Zaman periyodu: week, month, year
     */
    @Override
    public List<Complaint> getComplaintByUserIdAndDate(Long userId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);


        if (currentUser.getId()!=(userId) && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi şikayet geçmişinizi görüntüleyebilirsiniz.");
        }

        LocalDate date;
        switch (period.toLowerCase()) {
            case "week" -> date = LocalDate.now().minusWeeks(1);
            case "month" -> date = LocalDate.now().minusMonths(1);
            case "year" -> date = LocalDate.now().minusYears(1);
            default -> throw new IllegalArgumentException("Geçersiz Zaman Seçimi: " + period);
        }

        User user = userRepository.findById(userId).orElseThrow();
        return complaintRepository.findByUserAndCreatedAtAfter(user, date);
    }

    /**
     * Tüm şikayetleri getirir. Sadece admin erişebilir.
     */
    @Override
    public List<Complaint> getAllComplaint() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin tüm şikayetleri görüntüleyebilir.");
        }
        return complaintRepository.findAll();
    }

    /**
     * Şikayet durumuna göre filtreleme yapılır.
     * Admin ve doktorlar erişebilir.
     */
    @Override
    public List<Complaint> getComplaintByStatus(Complaint.Status status) {
        if (!SecurityUtil.hasRole("ADMIN") && !SecurityUtil.hasRole("DOKTOR")) {
            throw new RuntimeException("Bu işlemi yalnızca yetkili kişiler görüntüleyebilir.");
        }
        return complaintRepository.findByStatus(status);
    }

    /**
     * Şikayet güncellenir. Sadece admin ve doktorlar şikayet durumunu güncelleyebilir.
     */
    @Override
    public Complaint updateComplaint(Long id, Complaint updatedComplaint) {
        if (!SecurityUtil.hasRole("ADMIN") && !SecurityUtil.hasRole("DOKTOR")) {
            throw new RuntimeException("Sadece admin veya doktorlar şikayet güncelleyebilir.");
        }

        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Şikayet Bulunamadı: " + id));

        complaint.setContent(updatedComplaint.getContent());
        complaint.setStatus(updatedComplaint.getStatus());
        complaint.setAdminNote(updatedComplaint.getAdminNote());

        return complaintRepository.save(complaint);
    }

    /**
     * Şikayet silinir (opsiyonel). Sadece admin silebilir.
     */
    @Override
    public void deleteComplaint(Long id) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin şikayet silebilir.");
        }
        complaintRepository.deleteById(id);
    }
}
