package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.PatientHistory;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PatientHistoryRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.PatientHistoryService;
import com.hastanerandevu.app.util.SecurityUtil;
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

    /**
     * Yeni geçmiş bilgisi oluşturur.
     * Sadece DOKTOR rolündeki kullanıcılar ekleme yapabilir.
     */
    @Override
    public PatientHistory createHistory(PatientHistory history) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!SecurityUtil.hasRole("DOKTOR")) {
            throw new RuntimeException("Sadece doktorlar geçmiş bilgisi ekleyebilir.");
        }

        User patient = userRepository.findById(history.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        history.setDoctor(currentUser);
        history.setPatient(patient);
        history.setDate(LocalDate.now());

        return patientHistoryRepository.save(history);
    }

    /**
     * Geçmiş bilgisini günceller.
     * Sadece DOKTOR veya ADMIN yapabilir.
     */
    @Override
    public PatientHistory updateHistory(Long id, PatientHistory updatedHistory) {
        if (!SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin geçmiş bilgisi güncelleyebilir.");
        }

        PatientHistory history = patientHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Geçmiş bilgisi bulunamadı"));

        history.setNotes(updatedHistory.getNotes());
        history.setDiagnosis(updatedHistory.getDiagnosis());
        history.setTreatment(updatedHistory.getTreatment());

        return patientHistoryRepository.save(history);
    }

    /**
     * Geçmiş bilgisini siler.
     * Sadece DOKTOR veya ADMIN yapabilir.
     */
    @Override
    public void deleteHistory(Long id) {
        if (!SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin geçmiş bilgisi silebilir.");
        }

        patientHistoryRepository.deleteById(id);
    }

    /**
     * ID ile geçmiş bilgisi getirir.
     */
    @Override
    public PatientHistory getHistoryById(Long id) {
        return patientHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Geçmiş bilgisi bulunamadı"));
    }

    /**
     * Tüm geçmiş bilgilerini listeler.
     * Sadece ADMIN erişebilir.
     */
    @Override
    public List<PatientHistory> getAllHistories() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin tüm geçmiş bilgilerini görebilir.");
        }

        return patientHistoryRepository.findAll();
    }

    /**
     * Hastaya ait geçmiş bilgilerini getirir.
     * Hasta kendi geçmişini görebilir, doktor/admin tüm hastaları görebilir.
     */
    @Override
    public List<PatientHistory> getHistoriesByPatientId(Long patientId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getId() != patientId && !SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi geçmiş bilgilerinizi görebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));

        return patientHistoryRepository.findByPatient(patient);
    }

    /**
     * Doktorun eklediği geçmiş bilgilerini getirir.
     * Sadece kendisi veya admin görebilir.
     */
    @Override
    public List<PatientHistory> getHistoriesByDoctorId(Long doctorId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);


        if (currentUser.getId() != doctorId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi kayıtlarınızı görebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));

        return patientHistoryRepository.findByDoctor(doctor);
    }

    /**
     * Hastaya ait geçmiş bilgilerini zaman filtresiyle getirir.
     * Hasta kendi verisini görebilir, admin/doktor tüm hastaları görebilir.
     */
    @Override
    public List<PatientHistory> getHistoriesByPatientIdAndPeriod(Long patientId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getId() != patientId && !SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi geçmiş bilgilerinizi görüntüleyebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));

        LocalDate startDate = calculateStartDate(period);
        return patientHistoryRepository.findByPatientAndDateAfter(patient, startDate);
    }

    /**
     * Doktora ait geçmiş bilgilerini zaman filtresiyle getirir.
     * Sadece kendisi veya admin görebilir.
     */
    @Override
    public List<PatientHistory> getHistoriesByDoctorIdAndPeriod(Long doctorId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);


        if (currentUser.getId() != doctorId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi geçmiş bilgilerinizi görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));

        LocalDate startDate = calculateStartDate(period);
        return patientHistoryRepository.findByDoctorAndDateAfter(doctor, startDate);
    }

    /**
     * Tani (diagnosis) içeriğine göre geçmiş bilgilerini arar.
     */
    @Override
    public List<PatientHistory> searchHistoriesByDiagnosis(String keyword) {
        return patientHistoryRepository.findByDiagnosisContainingIgnoreCase(keyword);
    }

    /**
     * Tedavi (treatment) içeriğine göre geçmiş bilgilerini arar.
     */
    @Override
    public List<PatientHistory> searchHistoriesByTreatment(String keyword) {
        return patientHistoryRepository.findByTreatmentContainingIgnoreCase(keyword);
    }

    /**
     * Periyoda göre başlangıç tarihini hesaplar.
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
