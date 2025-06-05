package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.TestResult;
import com.hastanerandevu.app.service.TestResultService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hastarandevu/test-result")
public class TestResultController {

    private final TestResultService testResultService;

    public TestResultController(TestResultService testResultService) {
        this.testResultService = testResultService;
    }

    @PostMapping
    public ResponseEntity<TestResult> createTest(@RequestBody TestResult testResult) {
        return ResponseEntity.ok(testResultService.createTestResult(testResult));
    }

    @GetMapping
    public ResponseEntity<List<TestResult>> getAllResult() {
        return ResponseEntity.ok(testResultService.getAllTestResults());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TestResult>> getByPatientId(@PathVariable long patientId) {
        List<TestResult> results = testResultService.getTestResultsByPatientId(patientId);
        if (results.isEmpty()) {
            throw new RuntimeException("Belirtilen hasta ID'sine ait test sonucu bulunamadı: " + patientId);
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<TestResult>> getByDoctorId(@PathVariable long doctorId) {
        List<TestResult> results = testResultService.getTestResultsByDoctorId(doctorId);
        if (results.isEmpty()) {
            throw new RuntimeException("Belirtilen doktor ID'sine ait test sonucu bulunamadı: " + doctorId);
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/patient/{patientId}/filter")
    public ResponseEntity<List<TestResult>> filterByPatientAndPeriod(@PathVariable long patientId,
                                                                     @RequestParam String period) {
        List<TestResult> results = testResultService.getTestResultsByPatientIdAndPeriod(patientId, period);
        if (results.isEmpty()) {
            throw new RuntimeException("Hasta " + patientId + " için '" + period + "' süresine ait test sonucu bulunamadı.");
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/doctor/{doctorId}/filter")
    public ResponseEntity<List<TestResult>> filterByDoctorAndPeriod(@PathVariable long doctorId,
                                                                    @RequestParam String period) {
        List<TestResult> results = testResultService.getTestResultsByDoctorIdAndPeriod(doctorId, period);
        if (results.isEmpty()) {
            throw new RuntimeException("Doktor " + doctorId + " için '" + period + "' süresine ait test sonucu bulunamadı.");
        }
        return ResponseEntity.ok(results);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestResult> updateTest(@PathVariable Long id, @RequestBody TestResult updatedResult) {
        TestResult updated = testResultService.updateTestResult(id, updatedResult);
        if (updated == null) {
            throw new RuntimeException("Güncellenecek test sonucu bulunamadı. ID: " + id);
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testResultService.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }
}

