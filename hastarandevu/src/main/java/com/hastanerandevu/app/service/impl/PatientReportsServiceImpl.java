package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.PatientReports;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PatientReportRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.PatientReportsService;
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

    // Yeni rapor olusturur
    @Override
    public PatientReports createReport(PatientReports report) {
        User doctor = userRepository.findById(report.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));

        if (doctor.getRole() != User.Role.DOKTOR) {
            throw new RuntimeException("Sadece doktorlar rapor oluşturabilir.");
        }

        User patient = userRepository.findById(report.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        report.setDoctor(doctor);
        report.setPatient(patient);
        report.setReportDate(LocalDate.now());

        return reportRepository.save(report);

    }

    // Tum raporlari listeler (admin icin)
    @Override
    public List<PatientReports> getAllReports() {
        return reportRepository.findAll();
    }

    // Hastaya ait raporlari listeler
    @Override
    public List<PatientReports> getReportsByPatientId(Long patientId) {
        return reportRepository.findByPatientId(patientId);
    }

    // Doktora ait raporlari listeler
    @Override
    public List<PatientReports> getReportsByDoctorId(Long doctorId) {
        return reportRepository.findByDoctorId(doctorId);
    }

    // Hastaya ait belirli zaman araligindaki raporlari getirir
    @Override
    public List<PatientReports> getReportsByPatientIdAndPeriod(Long patientId, String period) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadi"));
        LocalDate startDate = calculateStartDate(period);
        return reportRepository.findByPatientAndReportDateAfter(patient, startDate);
    }

    // Doktora ait belirli zaman araligindaki raporlari getirir
    @Override
    public List<PatientReports> getReportsByDoctorIdAndPeriod(Long doctorId, String period) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadi"));
        LocalDate startDate = calculateStartDate(period);
        return reportRepository.findByDoctorAndReportDateAfter(doctor, startDate);
    }

    // Rapor turunde anahtar kelimeyle arama yapar
    @Override
    public List<PatientReports> searchReportsByKeyword(String keyword) {
        return reportRepository.findByReportTypeContainingIgnoreCase(keyword);
    }

    // Raporu gunceller
    @Override
    public PatientReports updateReport(Long id, PatientReports updatedReport) {
        PatientReports report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rapor bulunamadı."));

        report.setReportType(updatedReport.getReportType());
        report.setFileUrl(updatedReport.getFileUrl());
        return reportRepository.save(report);
    }

    // Raporu siler
    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
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
