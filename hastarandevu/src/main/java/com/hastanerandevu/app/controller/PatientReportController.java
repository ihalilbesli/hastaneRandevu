package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.PatientReports;
import com.hastanerandevu.app.service.PatientReportsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hastarandevu/patient-report")
public class PatientReportController {

    private final PatientReportsService patientReportsService;

    public PatientReportController(PatientReportsService patientReportsService) {
        this.patientReportsService = patientReportsService;
    }

    @PostMapping
    public ResponseEntity<PatientReports> createReport(@RequestBody PatientReports report) {
        return ResponseEntity.ok(patientReportsService.createReport(report));
    }

    @GetMapping
    public ResponseEntity<List<PatientReports>> getAllReports() {
        return ResponseEntity.ok(patientReportsService.getAllReports());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientReports>> getByPatient(@PathVariable Long patientId) {
        List<PatientReports> reports = patientReportsService.getReportsByPatientId(patientId);
        if (reports.isEmpty()) {
            throw new RuntimeException("Hasta ID'si " + patientId + " için rapor bulunamadı.");
        }
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PatientReports>> getByDoctor(@PathVariable Long doctorId) {
        List<PatientReports> reports = patientReportsService.getReportsByDoctorId(doctorId);
        if (reports.isEmpty()) {
            throw new RuntimeException("Doktor ID'si " + doctorId + " için rapor bulunamadı.");
        }
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/patient/{patientId}/filter")
    public ResponseEntity<List<PatientReports>> getByPatientAndPeriod(
            @PathVariable Long patientId,
            @RequestParam String period) {
        List<PatientReports> reports = patientReportsService.getReportsByPatientIdAndPeriod(patientId, period);
        if (reports.isEmpty()) {
            throw new RuntimeException("Hasta ID'si " + patientId + " için '" + period + "' süresinde rapor bulunamadı.");
        }
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/doctor/{doctorId}/filter")
    public ResponseEntity<List<PatientReports>> getByDoctorAndPeriod(
            @PathVariable Long doctorId,
            @RequestParam String period) {
        List<PatientReports> reports = patientReportsService.getReportsByDoctorIdAndPeriod(doctorId, period);
        if (reports.isEmpty()) {
            throw new RuntimeException("Doktor ID'si " + doctorId + " için '" + period + "' süresinde rapor bulunamadı.");
        }
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PatientReports>> searchByKeyword(@RequestParam String keyword) {
        List<PatientReports> reports = patientReportsService.searchReportsByKeyword(keyword);
        if (reports.isEmpty()) {
            throw new RuntimeException("Anahtar kelime '" + keyword + "' ile eşleşen rapor bulunamadı.");
        }
        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientReports> updateReport(
            @PathVariable Long id,
            @RequestBody PatientReports updatedReport) {
        PatientReports updated = patientReportsService.updateReport(id, updatedReport);
        if (updated == null) {
            throw new RuntimeException("ID: " + id + " olan rapor güncellenemedi veya bulunamadı.");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        patientReportsService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
