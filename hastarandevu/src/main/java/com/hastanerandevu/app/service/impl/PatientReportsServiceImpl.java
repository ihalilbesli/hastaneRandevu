package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.PatientReports;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PatientReportRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.PatientReportsService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientReportsServiceImpl implements PatientReportsService {

    private final PatientReportRepository reportRepository;
    private final UserRepository userRepository;

    public PatientReportsServiceImpl(PatientReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    /**
     * Yeni hasta raporu oluşturur.
     * Sadece DOKTOR rolüne sahip kullanıcılar oluşturabilir.
     */
    @Override
    public PatientReports createReport(PatientReports report) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentDoctor = userRepository.findByEmail(email).orElseThrow();

        if (!SecurityUtil.hasRole("DOCTOR")) {
            throw new RuntimeException("Sadece doktorlar rapor oluşturabilir.");
        }

        User patient = userRepository.findById(report.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        report.setDoctor(currentDoctor);
        report.setPatient(patient);
        report.setReportDate(LocalDate.now());

        return reportRepository.save(report);
    }

    /**
     * Tüm hasta raporlarını döner.
     * Sadece ADMIN erişebilir.
     */
    @Override
    public List<PatientReports> getAllReports() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin tüm raporları görebilir.");
        }
        return reportRepository.findAll();
    }

    /**
     * Hastaya ait raporları döner.
     * Sadece hasta kendisi veya ADMIN görebilir.
     */
    @Override
    public List<PatientReports> getReportsByPatientId(Long patientId) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != patientId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi raporlarınızı görüntüleyebilirsiniz.");
        }

        return reportRepository.findByPatientId(patientId);
    }

    /**
     * Doktora ait raporları döner.
     * Sadece ilgili doktor veya ADMIN görebilir.
     */
    @Override
    public List<PatientReports> getReportsByDoctorId(Long doctorId) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != doctorId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi yazdığınız raporları görüntüleyebilirsiniz.");
        }

        return reportRepository.findByDoctorId(doctorId);
    }

    /**
     * Hastaya ait belirli zaman aralığındaki raporları döner.
     * Sadece ilgili hasta veya ADMIN erişebilir.
     */
    @Override
    public List<PatientReports> getReportsByPatientIdAndPeriod(Long patientId, String period) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != patientId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi rapor geçmişinizi görüntüleyebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));

        LocalDate startDate = calculateStartDate(period);
        return reportRepository.findByPatientAndReportDateAfter(patient, startDate);
    }

    /**
     * Doktora ait belirli zaman aralığındaki raporları döner.
     * Sadece ilgili doktor veya ADMIN erişebilir.
     */
    @Override
    public List<PatientReports> getReportsByDoctorIdAndPeriod(Long doctorId, String period) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != doctorId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi rapor geçmişinizi görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));

        LocalDate startDate = calculateStartDate(period);
        return reportRepository.findByDoctorAndReportDateAfter(doctor, startDate);
    }

    /**
     * Rapor türüne göre anahtar kelime ile arama yapar.
     * (Gerekirse sadece giriş yapmış kullanıcıya açılabilir)
     */
    @Override
    public List<PatientReports> searchReportsByKeyword(String keyword) {
        return reportRepository.findByReportTypeContainingIgnoreCase(keyword);
    }

    /**
     * Raporu günceller.
     * Sadece DOKTOR veya ADMIN yapabilir.
     */
    @Override
    public PatientReports updateReport(Long id, PatientReports updatedReport) {
        if (!SecurityUtil.hasRole("DOCTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin rapor güncelleyebilir.");
        }

        PatientReports report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapor bulunamadı."));

        report.setReportType(updatedReport.getReportType());
        report.setFileUrl(updatedReport.getFileUrl());

        return reportRepository.save(report);
    }

    /**
     * Raporu siler.
     * Sadece ADMIN veya DOKTOR yapabilir.
     */
    @Override
    public void deleteReport(Long id) {
        if (!SecurityUtil.hasRole("DOCTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin rapor silebilir.");
        }
        reportRepository.deleteById(id);
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
