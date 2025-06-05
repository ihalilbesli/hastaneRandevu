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

    @PostMapping
    public ResponseEntity<PatientHistory> cretaeHistory(@RequestBody PatientHistory history){
        return ResponseEntity.ok(patientHistoryService.createHistory(history));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientHistory>> getByPatient(@PathVariable Long patientId) {
        List<PatientHistory> list = patientHistoryService.getHistoriesByPatientId(patientId);
        if (list.isEmpty()) {
            throw new RuntimeException("Hasta ID'si " + patientId + " için geçmiş kaydı bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PatientHistory>> getByDoctor(@PathVariable Long doctorId) {
        List<PatientHistory> list = patientHistoryService.getHistoriesByDoctorId(doctorId);
        if (list.isEmpty()) {
            throw new RuntimeException("Doktor ID'si " + doctorId + " için geçmiş kaydı bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientHistory> updateHistory(@PathVariable Long id, @RequestBody PatientHistory updatedHistory) {
        PatientHistory updated = patientHistoryService.updateHistory(id, updatedHistory);
        if (updated == null) {
            throw new RuntimeException("ID: " + id + " olan geçmiş bilgisi güncellenemedi.");
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        patientHistoryService.deleteHistory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}/filter")
    public ResponseEntity<List<PatientHistory>> filterByPeriod(@PathVariable Long patientId, @RequestParam String period) {
        List<PatientHistory> list = patientHistoryService.getHistoriesByPatientIdAndPeriod(patientId, period);
        if (list.isEmpty()) {
            throw new RuntimeException("Hasta ID'si " + patientId + " için '" + period + "' süresinde geçmiş kaydı bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search/diagnosis")
    public ResponseEntity<List<PatientHistory>> searchByDiagnosis(@RequestParam String keyword) {
        List<PatientHistory> list = patientHistoryService.searchHistoriesByDiagnosis(keyword);
        if (list.isEmpty()) {
            throw new RuntimeException("Tani içinde '" + keyword + "' içeren kayıt bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search/treatment")
    public ResponseEntity<List<PatientHistory>> searchByTreatment(@RequestParam String keyword) {
        List<PatientHistory> list = patientHistoryService.searchHistoriesByTreatment(keyword);
        if (list.isEmpty()) {
            throw new RuntimeException("Tedavi içinde '" + keyword + "' içeren kayıt bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<PatientHistory>> getAllHistories() {
        return ResponseEntity.ok(patientHistoryService.getAllHistories());
    }
}

