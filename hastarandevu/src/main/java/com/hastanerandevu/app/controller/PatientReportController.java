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
    //  Yeni rapor olustur
    @PostMapping
    public ResponseEntity<PatientReports> createReport(@RequestBody PatientReports report) {
        return ResponseEntity.ok(patientReportsService.createReport(report));
    }

    //  Tum raporlari getir
    @GetMapping
    public ResponseEntity<List<PatientReports>> getAllReports() {
        return ResponseEntity.ok(patientReportsService.getAllReports());
    }

    //  Belirli hastanin raporlari
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientReports>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientReportsService.getReportsByPatientId(patientId));
    }

    //  Belirli doktorun raporlari
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PatientReports>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(patientReportsService.getReportsByDoctorId(doctorId));
    }

    //  Hastaya ait belli zaman araligindaki raporlar
    @GetMapping("/patient/{patientId}/filter")
    public ResponseEntity<List<PatientReports>> getByPatientAndPeriod(
            @PathVariable Long patientId,
            @RequestParam String period) {
        return ResponseEntity.ok(patientReportsService.getReportsByPatientIdAndPeriod(patientId, period));
    }

    //  Doktora ait belli zaman araligindaki raporlar
    @GetMapping("/doctor/{doctorId}/filter")
    public ResponseEntity<List<PatientReports>> getByDoctorAndPeriod(
            @PathVariable Long doctorId,
            @RequestParam String period) {
        return ResponseEntity.ok(patientReportsService.getReportsByDoctorIdAndPeriod(doctorId, period));
    }

    //  Anahtar kelimeye gore arama (rapor turunde)
    @GetMapping("/search")
    public ResponseEntity<List<PatientReports>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(patientReportsService.searchReportsByKeyword(keyword));
    }

    //  Rapor guncelle (sadece ilgili doktor)
    @PutMapping("/{id}")
    public ResponseEntity<PatientReports> updateReport(
            @PathVariable Long id,
            @RequestBody PatientReports updatedReport) {
        return ResponseEntity.ok(patientReportsService.updateReport(id, updatedReport));
    }

    //  Rapor sil (admin veya ilgili doktor)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        patientReportsService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
