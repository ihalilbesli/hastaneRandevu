package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.TestResult;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.AppointmentRepository;
import com.hastanerandevu.app.repository.TestResultRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestResultServiceImplTest {

    @Mock private TestResultRepository testResultRepository;
    @Mock private UserRepository userRepository;
    @Mock private AppointmentRepository appointmentRepository;

    @InjectMocks private TestResultServiceImpl testResultService;

    private User doctor;
    private User patient;
    private TestResult testResult;

    @BeforeEach
    void setup() {
        doctor = User.builder().id(1L).role(User.Role.DOKTOR).build();
        patient = User.builder().id(2L).role(User.Role.HASTA).build();
        testResult = TestResult.builder().id(10L).testName("Test").testDate(LocalDate.now()).patient(patient).doctor(doctor).build();
    }

    @Test
    void createTestResult_shouldSave_whenDoctor() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);
            when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(testResultRepository.save(any())).thenReturn(testResult);

            TestResult result = testResultService.createTestResult(testResult);

            assertEquals(testResult.getTestName(), result.getTestName());
        }
    }

    @Test
    void getTestResultsByPatientId_shouldReturn_whenAuthorized() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(patient);
            when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(testResultRepository.findByPatient(patient)).thenReturn(List.of(testResult));

            List<TestResult> results = testResultService.getTestResultsByPatientId(patient.getId());
            assertEquals(1, results.size());
        }
    }

    @Test
    void updateTestResult_shouldUpdateFields_whenAuthorized() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(true);
            when(testResultRepository.findById(10L)).thenReturn(Optional.of(testResult));
            when(testResultRepository.save(any())).thenReturn(testResult);

            TestResult update = TestResult.builder().testName("Updated").testDate(LocalDate.now()).build();
            TestResult result = testResultService.updateTestResult(10L, update);

            assertEquals("Updated", result.getTestName());
        }
    }

    @Test
    void deleteTestResult_shouldCallRepository_whenAuthorized() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(true);

            testResultService.deleteTestResult(10L);

            verify(testResultRepository).deleteById(10L);
        }
    }
}
