package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.TestResult;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.TestResultRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.TestResultService;
import com.hastanerandevu.app.util.SecurityUtil;
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

    /**
     * Yeni test sonucu oluşturur.
     * Sadece giriş yapan doktor kendi adına sonuç ekleyebilir.
     */
    @Override
    public TestResult createTestResult(TestResult testResult) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentDoctor = userRepository.findByEmail(email).orElseThrow();

        if (!SecurityUtil.hasRole("DOCTOR")) {
            throw new RuntimeException("Test sonucu sadece doktor tarafından eklenebilir.");
        }

        User patient = userRepository.findById(testResult.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        testResult.setDoctor(currentDoctor);
        testResult.setPatient(patient);

        return testResultRepository.save(testResult);
    }

    /**
     * Hastaya ait test sonuçlarını getirir.
     * Sadece hasta kendisi veya admin erişebilir.
     */
    @Override
    public List<TestResult> getTestResultsByPatientId(Long patientId) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != patientId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi test sonuçlarınızı görüntüleyebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));
        return testResultRepository.findByPatient(patient);
    }

    /**
     * Doktora ait test sonuçlarını getirir.
     * Sadece ilgili doktor veya admin görebilir.
     */
    @Override
    public List<TestResult> getTestResultsByDoctorId(Long doctorId) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != doctorId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi test sonuçlarınızı görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı."));
        return testResultRepository.findByDoctor(doctor);
    }

    /**
     * Tüm test sonuçlarını listeler.
     * Sadece admin erişebilir.
     */
    @Override
    public List<TestResult> getAllTestResults() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin tüm test sonuçlarını görüntüleyebilir.");
        }
        return testResultRepository.findAll();
    }

    /**
     * Test sonucunu günceller.
     * Sadece doktor veya admin yapabilir.
     */
    @Override
    public TestResult updateTestResult(Long id, TestResult updatedResult) {
        if (!SecurityUtil.hasRole("DOCTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin test sonucu güncelleyebilir.");
        }

        TestResult result = testResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test sonucu bulunamadı."));

        result.setTestName(updatedResult.getTestName());
        result.setTestType(updatedResult.getTestType());
        result.setResult(updatedResult.getResult());
        result.setDoctorComment(updatedResult.getDoctorComment());
        result.setTestDate(updatedResult.getTestDate());

        return testResultRepository.save(result);
    }

    /**
     * Test sonucunu siler.
     * Sadece doktor veya admin silebilir.
     */
    @Override
    public void deleteTestResult(Long id) {
        if (!SecurityUtil.hasRole("DOCTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin test sonucu silebilir.");
        }

        testResultRepository.deleteById(id);
    }

    /**
     * Hastaya ait belirli zaman aralığındaki test sonuçlarını getirir.
     */
    @Override
    public List<TestResult> getTestResultsByPatientIdAndPeriod(Long patientId, String period) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != patientId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi test geçmişinizi görüntüleyebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        LocalDate date = calculateStartDate(period);
        return testResultRepository.findByPatientAndTestDateAfter(patient, date);
    }

    /**
     * Doktora ait belirli zaman aralığındaki test sonuçlarını getirir.
     */
    @Override
    public List<TestResult> getTestResultsByDoctorIdAndPeriod(Long doctorId, String period) {
        String email = SecurityUtil.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId() != doctorId && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi test sonuçlarınızı görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı."));

        LocalDate date = calculateStartDate(period);
        return testResultRepository.findByDoctorAndTestDateAfter(doctor, date);
    }

    /**
     * Zaman aralığına göre başlangıç tarihini hesaplar.
     */
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
