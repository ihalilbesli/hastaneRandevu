package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.PatientReports;

import java.util.List;

public interface PatientReportsService {

    // Yeni rapor olustur
    PatientReports createReport(PatientReports report);

    // Tum raporlar
    List<PatientReports> getAllReports();

    // Hasta ID'ye gore raporlar
    List<PatientReports> getReportsByPatientId(Long patientId);

    // Doktor ID'ye gore raporlar
    List<PatientReports> getReportsByDoctorId(Long doctorId);

    // Hasta ID ve periyoda gore (1 hafta / 1 ay / 1 yil)
    List<PatientReports> getReportsByPatientIdAndPeriod(Long patientId, String period);

    // Doktor ID ve periyoda gore (1 hafta / 1 ay / 1 yil)
    List<PatientReports> getReportsByDoctorIdAndPeriod(Long doctorId, String period);

    // ReportType'a gore filtreleme
    List<PatientReports> searchReportsByKeyword(String keyword);

    // Rapor guncelleme
    PatientReports updateReport(Long id, PatientReports updatedReport);

    // Rapor silme
    void deleteReport(Long id);
}
