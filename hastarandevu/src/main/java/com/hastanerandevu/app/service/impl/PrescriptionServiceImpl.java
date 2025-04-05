package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Prescription;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PrescriptionRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.PrescriptionService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository, UserRepository userRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Yeni reçete oluşturur.
     * Sadece DOKTOR rolündeki kullanıcılar kendi adına reçete yazabilir.
     */
    @Override
    public Prescription createPrescription(Prescription prescription) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentDoctor = userRepository.findByEmail(email).orElseThrow();

        if (!SecurityUtil.hasRole("DOCTOR")) {
            throw new RuntimeException("Reçete sadece doktor tarafından oluşturulabilir.");
        }

        User patient = userRepository.findById(prescription.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        prescription.setDoctor(currentDoctor);
        prescription.setPatient(patient);
        prescription.setDate(LocalDate.now());

        // 10 haneli benzersiz prescription code oluştur
        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        String generatedCode = "RX" + randomPart;
        prescription.setPrescriptionCode(generatedCode);

        return prescriptionRepository.save(prescription);
    }

    /**
     * Tüm reçeteleri döner.
     * Sadece ADMIN erişebilir.
     */
    @Override
    public List<Prescription> getAllPrescriptions() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin tüm reçeteleri görüntüleyebilir.");
        }
        return prescriptionRepository.findAll();
    }

    /**
     * Reçeteyi ID ile getirir.
     * Reçete doktoru, hastası veya admin görebilir.
     */
    @Override
    public Prescription getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reçete bulunamadı."));

        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (prescription.getDoctor().getId()!=(currentUser.getId()) &&
                prescription.getPatient().getId()!=(currentUser.getId()) &&
                !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu reçeteye erişim izniniz yok.");
        }

        return prescription;
    }

    /**
     * Hastaya ait reçeteleri döner.
     * Sadece hasta kendisi veya admin görebilir.
     */
    @Override
    public List<Prescription> getPrescriptionsByPatientId(Long patientId) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != patientId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi reçetelerinizi görebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));

        return prescriptionRepository.findByPatient(patient);
    }

    /**
     * Doktora ait reçeteleri döner.
     * Sadece doktor kendisi veya admin görebilir.
     */
    @Override
    public List<Prescription> getPrescriptionsByDoctorId(Long doctorId) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != doctorId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi yazdığınız reçeteleri görebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));

        return prescriptionRepository.findByDoctor(doctor);
    }

    /**
     * Hastaya ait belirli zaman aralığındaki reçeteleri döner.
     */
    @Override
    public List<Prescription> getPrescriptionsByPatientIdAndPeriod(Long patientId, String period) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != patientId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi reçetelerinizi görüntüleyebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));

        LocalDate startDate = calculateStartDate(period);
        return prescriptionRepository.findByPatientAndDateAfter(patient, startDate);
    }

    /**
     * Doktora ait belirli zaman aralığındaki reçeteleri döner.
     */
    @Override
    public List<Prescription> getPrescriptionsByDoctorIdAndPeriod(Long doctorId, String period) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != doctorId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi yazdığınız reçeteleri görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));

        LocalDate startDate = calculateStartDate(period);
        return prescriptionRepository.findByDoctorAndDateAfter(doctor, startDate);
    }

    /**
     * Açıklama içeriğine göre reçetelerde arama yapar.
     */
    @Override
    public List<Prescription> searchPrescriptionsByKeyword(String keyword) {
        return prescriptionRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    /**
     * Reçeteyi günceller.
     * Sadece doktor veya admin yapabilir.
     */
    @Override
    public Prescription updatePrescription(Long id, Prescription updatedPrescription) {
        if (!SecurityUtil.hasRole("DOCTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin reçete güncelleyebilir.");
        }

        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reçete bulunamadı"));

        existing.setDescription(updatedPrescription.getDescription());
        return prescriptionRepository.save(existing);
    }

    /**
     * Reçeteyi siler.
     * Sadece admin veya doktor silebilir.
     */
    @Override
    public void deletePrescription(Long id) {
        if (!SecurityUtil.hasRole("DOCTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin reçete silebilir.");
        }

        prescriptionRepository.deleteById(id);
    }

    /**
     * Verilen zaman periyoduna göre başlangıç tarihi hesaplar.
     */
    private LocalDate calculateStartDate(String period) {
        return switch (period.toLowerCase()) {
            case "day" -> LocalDate.now().minusDays(1);
            case "week" -> LocalDate.now().minusWeeks(1);
            case "month" -> LocalDate.now().minusMonths(1);
            case "year" -> LocalDate.now().minusYears(1);
            default -> throw new IllegalArgumentException("Geçersiz zaman aralığı: " + period);
        };
    }
}
