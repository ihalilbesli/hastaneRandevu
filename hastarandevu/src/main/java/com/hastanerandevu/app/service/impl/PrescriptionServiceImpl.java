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
import java.util.Objects;
import java.util.UUID;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final UserRepository userRepository;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository, UserRepository userRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Prescription createPrescription(Prescription prescription) {
        User currentDoctor = SecurityUtil.getCurrentUser(userRepository);

        if (currentDoctor.getRole() != User.Role.DOKTOR) {
            throw new RuntimeException("Reçete sadece doktor tarafından oluşturulabilir.");
        }

        User patient = userRepository.findById(prescription.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        prescription.setDoctor(currentDoctor);
        prescription.setPatient(patient);
        prescription.setDate(LocalDate.now());

        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        prescription.setPrescriptionCode("RX" + randomPart);

        return prescriptionRepository.save(prescription);
    }

    @Override
    public List<Prescription> getAllPrescriptions() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin tüm reçeteleri görüntüleyebilir.");
        }
        return prescriptionRepository.findAll();
    }

    @Override
    public Prescription getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reçete bulunamadı."));

        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(prescription.getDoctor().getId(), currentUser.getId())
                && !Objects.equals(prescription.getPatient().getId(), currentUser.getId())
                && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Bu reçeteye erişim izniniz yok.");
        }

        return prescription;
    }

    @Override
    public List<Prescription> getPrescriptionsByPatientId(Long patientId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (
                currentUser.getRole() != User.Role.DOKTOR &&
                        currentUser.getRole() != User.Role.ADMIN &&
                        !Objects.equals(currentUser.getId(), patientId)
        ) {
            throw new RuntimeException("Bu hastanın reçetelerine erişim yetkiniz yok.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        return prescriptionRepository.findByPatient(patient);
    }


    @Override
    public List<Prescription> getPrescriptionsByDoctorId(Long doctorId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(currentUser.getId(), doctorId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi yazdığınız reçeteleri görebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı."));

        return prescriptionRepository.findByDoctor(doctor);
    }

    @Override
    public List<Prescription> getPrescriptionsByPatientIdAndPeriod(Long patientId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(currentUser.getId(), patientId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi reçetelerinizi görüntüleyebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        LocalDate startDate = calculateStartDate(period);
        return prescriptionRepository.findByPatientAndDateAfter(patient, startDate);
    }

    @Override
    public List<Prescription> getPrescriptionsByDoctorIdAndPeriod(Long doctorId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(currentUser.getId(), doctorId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi yazdığınız reçeteleri görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı."));

        LocalDate startDate = calculateStartDate(period);
        return prescriptionRepository.findByDoctorAndDateAfter(doctor, startDate);
    }

    @Override
    public List<Prescription> searchPrescriptionsByKeyword(String keyword) {
        return prescriptionRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    @Override
    public Prescription updatePrescription(Long id, Prescription updatedPrescription) {
        if (!SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin reçete güncelleyebilir.");
        }

        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reçete bulunamadı."));

        existing.setDescription(updatedPrescription.getDescription());
        return prescriptionRepository.save(existing);
    }

    @Override
    public void deletePrescription(Long id) {
        if (!SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin reçete silebilir.");
        }

        prescriptionRepository.deleteById(id);
    }

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
