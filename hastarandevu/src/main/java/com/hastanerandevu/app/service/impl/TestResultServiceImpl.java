package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.TestResult;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.TestResultRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.TestResultService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;

    public TestResultServiceImpl(TestResultRepository testResultRepository, UserRepository userRepository) {
        this.testResultRepository = testResultRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TestResult createTestResult(TestResult testResult) {
        User doctor=userRepository.findById(testResult.getDoctor().getId())
                .orElseThrow(()->new RuntimeException("Doktor bulunamadi"));
        if (doctor.getRole()!=User.Role.DOKTOR){
            throw new RuntimeException("Test Sonucu sadece doktor tarafindan eklenebilinir");
        }

        // Hasta kontrolü
        User patient = userRepository.findById(testResult.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));

        testResult.setDoctor(doctor);
        testResult.setPatient(patient);

        return testResultRepository.save(testResult);
    }

    @Override
    public List<TestResult> getTestResultsByPatientId(Long patientId) {
        User patient=userRepository.findById(patientId)
                .orElseThrow(()->new RuntimeException("Hasta Bulunamadi"));
        return testResultRepository.findByPatient(patient);
    }

    @Override
    public List<TestResult> getTestResultsByDoctorId(Long doctorId) {
        User doktor=userRepository.findById(doctorId)
                .orElseThrow(()->new RuntimeException("Doktor Bulunamadi"));
        return testResultRepository.findByDoctor(doktor);
    }

    @Override
    public List<TestResult> getAllTestResults() {
        return testResultRepository.findAll();
    }

    @Override
    public TestResult updateTestResult(Long id, TestResult updatedResult) {
        TestResult result=testResultRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Test sonucu bulunamadi"));
        result.setTestName(updatedResult.getTestName());
        result.setTestType(updatedResult.getTestType());
        result.setResult(updatedResult.getResult());
        result.setDoctorComment(updatedResult.getDoctorComment());
        result.setTestDate(updatedResult.getTestDate());

        return testResultRepository.save(result);
    }

    @Override
    public void deleteTestResult(Long id) {
            testResultRepository.deleteById(id);
    }

    @Override
    public List<TestResult> getTestResultsByPatientIdAndPeriod(Long patientId, String period) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));

        LocalDate date = calculateStartDate(period);
        return testResultRepository.findByPatientAndTestDateAfter(patient, date);
    }

    @Override
    public List<TestResult> getTestResultsByDoctorIdAndPeriod(Long doctorId, String period) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));

        LocalDate date = calculateStartDate(period);
        return testResultRepository.findByDoctorAndTestDateAfter(doctor, date);
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
