package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.TestResult;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.AppointmentRepository;
import com.hastanerandevu.app.repository.TestResultRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.TestResultService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class TestResultServiceImpl implements TestResultService {

    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    public TestResultServiceImpl(TestResultRepository testResultRepository, UserRepository userRepository, AppointmentRepository appointmentRepository) {
        this.testResultRepository = testResultRepository;
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public TestResult createTestResult(TestResult testResult) {
        User currentDoctor = SecurityUtil.getCurrentUser(userRepository);

        if (currentDoctor.getRole() != User.Role.DOKTOR) {
            throw new RuntimeException("Test sonucu sadece doktor tarafından eklenebilir.");
        }

        User patient = userRepository.findById(testResult.getPatient().getId())
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        testResult.setDoctor(currentDoctor);
        testResult.setPatient(patient);

        return testResultRepository.save(testResult);
    }

    @Override
    public List<TestResult> getTestResultsByPatientId(Long patientId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getRole() == User.Role.HASTA) {
            // Hasta ise: sadece kendi verisine erişebilir
            if (!Objects.equals(currentUser.getId(), patientId)) {
                throw new RuntimeException("Sadece kendi test sonuçlarınızı görüntüleyebilirsiniz.");
            }
        } else if (currentUser.getRole() == User.Role.DOKTOR) {
            // Doktor ise: sadece kendi hastalarına erişebilir
            boolean hasRelation = appointmentRepository.existsByDoctorIdAndPatientId(currentUser.getId(), patientId);
            if (!hasRelation) {
                throw new RuntimeException("Bu hastaya ait verilere erişim yetkiniz yok.");
            }
        } else if (currentUser.getRole() != User.Role.ADMIN) {
            // Ne hasta, ne doktor, ne admin ➔ Hata!
            throw new RuntimeException("Bu işlemi yapmaya yetkiniz yok.");
        }

        // Hasta varsa test sonuçlarını getir
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        return testResultRepository.findByPatient(patient);
    }


    @Override
    public List<TestResult> getTestResultsByDoctorId(Long doctorId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(currentUser.getId(), doctorId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi test sonuçlarınızı görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı."));
        return testResultRepository.findByDoctor(doctor);
    }

    @Override
    public List<TestResult> getAllTestResults() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin tüm test sonuçlarını görüntüleyebilir.");
        }
        return testResultRepository.findAll();
    }

    @Override
    public TestResult updateTestResult(Long id, TestResult updatedResult) {
        if (!SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
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

    @Override
    public void deleteTestResult(Long id) {
        if (!SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece doktor veya admin test sonucu silebilir.");
        }

        testResultRepository.deleteById(id);
    }

    @Override
    public List<TestResult> getTestResultsByPatientIdAndPeriod(Long patientId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(currentUser.getId(), patientId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi test geçmişinizi görüntüleyebilirsiniz.");
        }

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı."));

        LocalDate startDate = calculateStartDate(period);
        return testResultRepository.findByPatientAndTestDateAfter(patient, startDate);
    }

    @Override
    public List<TestResult> getTestResultsByDoctorIdAndPeriod(Long doctorId, String period) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(currentUser.getId(), doctorId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi test sonuçlarınızı görüntüleyebilirsiniz.");
        }

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı."));

        LocalDate startDate = calculateStartDate(period);
        return testResultRepository.findByDoctorAndTestDateAfter(doctor, startDate);
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
