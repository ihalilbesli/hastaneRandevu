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

    @Override
    public PatientReports createReport(PatientReports report) {
        User currentDoctor = SecurityUtil.getCurrentUser(userRepository);

        if (currentDoctor.getRole() != User.Role.DOKTOR) {
            throw new RuntimeException("Sadece doktorlar rapor oluşturabilir.");
        }

        User patient = userRepository.findById(report.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        report.setDoctor(currentDoctor);
        report.setPatient(patient);
        report.setReportDate(LocalDate.now());

        return reportRepository.save(report);
    }

    @Override
    public List<PatientReports> getAllReports() {
        if (SecurityUtil.getCurrentUser(userRepository).getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece admin tüm raporları görebilir.");
        }
        return reportRepository.findAll();
    }

    @Override
    public List<PatientReports> getReportsByPatientId(Long patientId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getId()!=(patientId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi raporlarınızı görüntüleyebilirsiniz.");
        }

        return reportRepository.findByPatientId(patientId);
    }

    @Override
    public List<PatientReports> getReportsByDoctorId(Long doctorId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getId()!=(doctorId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi yazdığınız raporları görüntüleyebilirsiniz.");
        }

        return reportRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<PatientReports> getReportsByPatientIdAndPeriod(Long patientId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getId()!=(patientId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi rapor geçmişinizi görüntüleyebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        LocalDate startDate = calculateStartDate(period);
        return reportRepository.findByPatientAndReportDateAfter(patient, startDate);
    }

    @Override
    public List<PatientReports> getReportsByDoctorIdAndPeriod(Long doctorId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getId()!=(doctorId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi rapor geçmişinizi görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı."));

        LocalDate startDate = calculateStartDate(period);
        return reportRepository.findByDoctorAndReportDateAfter(doctor, startDate);
    }

    @Override
    public List<PatientReports> searchReportsByKeyword(String keyword) {
        return reportRepository.findByReportTypeContainingIgnoreCase(keyword);
    }

    @Override
    public PatientReports updateReport(Long id, PatientReports updatedReport) {
        if (!SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin rapor güncelleyebilir.");
        }

        PatientReports report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapor bulunamadı."));

        report.setReportType(updatedReport.getReportType());
        report.setFileUrl(updatedReport.getFileUrl());

        return reportRepository.save(report);
    }

    @Override
    public void deleteReport(Long id) {
        if (!SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin rapor silebilir.");
        }
        reportRepository.deleteById(id);
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
