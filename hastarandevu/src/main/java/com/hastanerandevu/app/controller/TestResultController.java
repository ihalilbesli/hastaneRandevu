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
    // Yeni test sonucu oluştur
    @PostMapping
    public ResponseEntity<TestResult> createTest(@RequestBody TestResult testResult) {
        return ResponseEntity.ok(testResultService.createTestResult(testResult));
    }

    //  Tüm test sonuçlarını getir
    @GetMapping
    public ResponseEntity<List<TestResult>> getAllResult(){
        return ResponseEntity.ok(testResultService.getAllTestResults());
    }

    //  Hastanın tüm test sonuçlarını getir
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<TestResult>> getByPatientId(@PathVariable long patientId){
        return ResponseEntity.ok(testResultService.getTestResultsByPatientId(patientId));
    }
    //  Doktorun eklediği test sonuçlarını getir
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<TestResult>> getByDoctorId(@PathVariable long doctorId){
        return ResponseEntity.ok(testResultService.getTestResultsByDoctorId(doctorId));
    }

    //  Hasta için zaman filtreli test sonuçları
    @GetMapping("/patient/{patientId}/filter")
    public ResponseEntity<List<TestResult>> filterByPatientAndPeriod(@PathVariable long patientId,
                                                                    @RequestParam String period){
        return ResponseEntity.ok(testResultService.getTestResultsByPatientIdAndPeriod(patientId,period));
    }
    //  Doktor için zaman filtreli test sonuçları
    @GetMapping("/doctor/{doctorId}/filter")
    public ResponseEntity<List<TestResult>> filterByDoctorAndPeriod(@PathVariable long doctorId,
                                                                     @RequestParam String period){
        return ResponseEntity.ok(testResultService.getTestResultsByDoctorIdAndPeriod(doctorId,period));
    }
    //  Test sonucu güncelle
    @PutMapping("/{id}")
    public ResponseEntity<TestResult> updateTest(@PathVariable Long id,
                                                 @RequestBody TestResult updatedResult) {
        return ResponseEntity.ok(testResultService.updateTestResult(id, updatedResult));
    }

    //  Test sonucu sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testResultService.deleteTestResult(id);
        return ResponseEntity.noContent().build();
    }

}
