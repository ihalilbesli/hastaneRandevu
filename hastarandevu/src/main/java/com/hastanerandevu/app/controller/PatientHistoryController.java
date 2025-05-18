package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.PatientHistory;
import com.hastanerandevu.app.service.PatientHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hastarandevu/patient-history")
public class PatientHistoryController {
    private final PatientHistoryService patientHistoryService;

    public PatientHistoryController(PatientHistoryService patientHistoryService) {
        this.patientHistoryService = patientHistoryService;
    }

    // Yeni gecmis bilgisi olustur
    @PostMapping
    public ResponseEntity<PatientHistory> cretaeHistory(@RequestBody PatientHistory history){
        return ResponseEntity.ok(patientHistoryService.createHistory(history));
    }
    // Belirli hastanin tum gecmis bilgilerini getir
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientHistory>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientHistoryService.getHistoriesByPatientId(patientId));
    }

    // Belirli doktorun ekledigi kayitlari getir
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PatientHistory>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(patientHistoryService.getHistoriesByDoctorId(doctorId));
    }

    // Gecmis kaydi guncelle (Sadece doktor)
    @PutMapping("/{id}")
    public ResponseEntity<PatientHistory> updateHistory(@PathVariable Long id, @RequestBody PatientHistory updatedHistory) {
        return ResponseEntity.ok(patientHistoryService.updateHistory(id, updatedHistory));
    }

    // Gecmis kaydini sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        patientHistoryService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }

    // Tarihe gore filtrele (son 1 hafta, ay, yil gibi)
    @GetMapping("/patient/{patientId}/filter")
    public ResponseEntity<List<PatientHistory>> filterByPeriod(@PathVariable Long patientId, @RequestParam String period) {
        return ResponseEntity.ok(patientHistoryService.getHistoriesByPatientIdAndPeriod(patientId, period));
    }

    // Tani icinde anahtar kelimeye gore arama yap
    @GetMapping("/search/diagnosis")
    public ResponseEntity<List<PatientHistory>> searchByDiagnosis(@RequestParam String keyword) {
        return ResponseEntity.ok(patientHistoryService.searchHistoriesByDiagnosis(keyword));
    }
    // Tedaviye (treatment) gore arama yapar
    @GetMapping("/search/treatment")
    public ResponseEntity<List<PatientHistory>> searchByTreatment(@RequestParam String keyword) {
        return ResponseEntity.ok(patientHistoryService.searchHistoriesByTreatment(keyword));
    }
    @GetMapping
    public ResponseEntity<List<PatientHistory>> getAllHistories() {
        return ResponseEntity.ok(patientHistoryService.getAllHistories());
    }
}
