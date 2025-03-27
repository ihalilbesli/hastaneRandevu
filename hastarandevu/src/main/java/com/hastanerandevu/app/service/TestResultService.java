package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.TestResult;

import java.util.List;

public interface TestResultService {
    TestResult createTestResult(TestResult testResult);

    List<TestResult> getTestResultsByPatientId(Long patientId);

    List<TestResult> getTestResultsByDoctorId(Long doctorId);

    List<TestResult> getAllTestResults();

    TestResult updateTestResult(Long id, TestResult updatedResult);

    void deleteTestResult(Long id);
    // 🔹 Hastanın "son X gün/hafta/ay/yıl" içindeki test sonuçlarını getir
    List<TestResult> getTestResultsByPatientIdAndPeriod(Long patientId, String period);

    // 🔹 Doktorun "son X gün/hafta/ay/yıl" içinde eklediği test sonuçlarını getir
    List<TestResult> getTestResultsByDoctorIdAndPeriod(Long doctorId, String period);
}
