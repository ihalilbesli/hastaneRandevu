package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Prescription;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PrescriptionRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.PrescriptionService;
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



    //Recete olusturma
    @Override
    public Prescription createPrescription(Prescription prescription) {
        User doctor=userRepository.findById(prescription.getDoctor().getId())
                .orElseThrow(()->new RuntimeException("Doktor Bulunamadi"));
        if (doctor.getRole()!=User.Role.DOKTOR){
            throw new RuntimeException("Recete sadece doktor tarafindan olusturabilinir");
        }
        User patient=userRepository.findById(prescription.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);
        prescription.setDate(LocalDate.now());

        // 10 haneli prescription code oluştur (örnek: RX3A7B9K2)
        String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8).toUpperCase();
        String generatedCode = "RX" + randomPart;
        prescription.setPrescriptionCode(generatedCode);

        return prescriptionRepository.save(prescription);
    }

    @Override
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    @Override
    public Prescription getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reçete bulunamadı"));
    }

    @Override
    public List<Prescription> getPrescriptionsByPatientId(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        return prescriptionRepository.findByPatient(patient);
    }

    @Override
    public List<Prescription> getPrescriptionsByDoctorId(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        return prescriptionRepository.findByDoctor(doctor);
    }

    @Override
    public List<Prescription> getPrescriptionsByPatientIdAndPeriod(Long patientId, String period) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        LocalDate startDate = calculateStartDate(period);
        return prescriptionRepository.findByPatientAndDateAfter(patient, startDate);
    }

    @Override
    public List<Prescription> getPrescriptionsByDoctorIdAndPeriod(Long doctorId, String period) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        LocalDate startDate = calculateStartDate(period);
        return prescriptionRepository.findByDoctorAndDateAfter(doctor, startDate);
    }

    @Override
    public List<Prescription> searchPrescriptionsByKeyword(String keyword) {
        return prescriptionRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    @Override
    public Prescription updatePrescription(Long id, Prescription updatedPrescription) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reçete bulunamadı"));

        existing.setDescription(updatedPrescription.getDescription());
        return prescriptionRepository.save(existing);
    }

    @Override
    public void deletePrescription(Long id) {
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
