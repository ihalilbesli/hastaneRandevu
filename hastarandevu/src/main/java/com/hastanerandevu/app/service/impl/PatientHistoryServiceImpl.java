package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.PatientHistory;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PatientHistoryRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.PatientHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientHistoryServiceImpl implements PatientHistoryService {
    private final PatientHistoryRepository patientHistoryRepository;
    private final UserRepository userRepository;

    public PatientHistoryServiceImpl(PatientHistoryRepository patientHistoryRepository, UserRepository userRepository) {
        this.patientHistoryRepository = patientHistoryRepository;
        this.userRepository = userRepository;
    }

    // Yeni gecmis bilgisi olusturur
    @Override
    public PatientHistory createHistory(PatientHistory history) {
        User doctor=userRepository.findById(history.getDoctor().getId())
                .orElseThrow(()->new RuntimeException("Doktor Bulunamadi"));
        if (doctor.getRole()!=User.Role.DOKTOR){
            throw new RuntimeException("Sadece Doktor ekleme yapabilir");
        }
        User patient = userRepository.findById(history.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadi"));
        history.setDoctor(doctor);
        history.setPatient(patient);
        history.setDate(LocalDate.now());

        return patientHistoryRepository.save(history);
    }

    // Gecmis bilgisini gunceller
    @Override
    public PatientHistory updateHistory(Long id, PatientHistory updatedHistory) {
        PatientHistory history = patientHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gecmis bilgisi bulunamadi"));

        history.setNotes(updatedHistory.getNotes());
        history.setDiagnosis(updatedHistory.getDiagnosis());
        history.setTreatment(updatedHistory.getTreatment());

        return patientHistoryRepository.save(history);
    }

    // Gecmis bilgisini siler (doktor veya admin)
    @Override
    public void deleteHistory(Long id) {
         patientHistoryRepository.deleteById(id);
    }

    // ID ile gecmis bilgisini getirir
    @Override
    public PatientHistory getHistoryById(Long id) {
        return patientHistoryRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Gecmis Bilgisi Bulunamadi"));
    }

    // Tum gecmis bilgilerini listeler
    @Override
    public List<PatientHistory> getAllHistories() {
        return patientHistoryRepository.findAll();
    }

    // Hastaya ait gecmis bilgilerini getirir
    @Override
    public List<PatientHistory> getHistoriesByPatientId(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadi"));
        return patientHistoryRepository.findByPatient(patient);
    }

    // Doktorun ekledigi gecmis bilgilerini getirir
    @Override
    public List<PatientHistory> getHistoriesByDoctorId(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadi"));
        return patientHistoryRepository.findByDoctor(doctor);
    }

    // Hastaya ait zaman filtresiyle listeleme yapar
    @Override
    public List<PatientHistory> getHistoriesByPatientIdAndPeriod(Long patientId, String period) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadi"));
        LocalDate startDate = calculateStartDate(period);
        return patientHistoryRepository.findByPatientAndDateAfter(patient, startDate);
    }

    // Doktora ait zaman filtresiyle listeleme yapar
    @Override
    public List<PatientHistory> getHistoriesByDoctorIdAndPeriod(Long doctorId, String period) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadi"));
        LocalDate startDate = calculateStartDate(period);
        return patientHistoryRepository.findByDoctorAndDateAfter(doctor, startDate);
    }

    // Tani icerigine gore arama yapar
    @Override
    public List<PatientHistory> searchHistoriesByDiagnosis(String keyword) {
        return patientHistoryRepository.findByDiagnosisContainingIgnoreCase(keyword);
    }

    // Tedavi icerigine gore arama yapar
    @Override
    public List<PatientHistory> searchHistoriesByTreatment(String keyword) {
         return patientHistoryRepository.findByTreatmentContainingIgnoreCase(keyword);
    }
    private LocalDate calculateStartDate(String period) {
        return switch (period.toLowerCase()) {
            case "day" -> LocalDate.now().minusDays(1);
            case "week" -> LocalDate.now().minusWeeks(1);
            case "month" -> LocalDate.now().minusMonths(1);
            case "year" -> LocalDate.now().minusYears(1);
            default -> throw new IllegalArgumentException("Gecersiz zaman araligi: " + period);
        };
    }
}
