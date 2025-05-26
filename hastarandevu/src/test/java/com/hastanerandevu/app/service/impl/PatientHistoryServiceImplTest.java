package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.PatientHistory;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PatientHistoryRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientHistoryServiceImplTest {

    @Mock private PatientHistoryRepository patientHistoryRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private PatientHistoryServiceImpl service;

    private User doctor;
    private User patient;
    private PatientHistory history;

    @BeforeEach
    void setUp() {
        doctor = User.builder().id(1L).role(User.Role.DOKTOR).build();
        patient = User.builder().id(2L).role(User.Role.HASTA).build();
        history = PatientHistory.builder().id(1L).patient(patient).doctor(doctor)
                .diagnosis("grip").treatment("ilac").build();
    }

    @Test
    void createHistory_shouldSave_whenUserIsDoctor() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(true);
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);
            when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(patientHistoryRepository.save(any())).thenReturn(history);

            PatientHistory result = service.createHistory(history);
            assertEquals("grip", result.getDiagnosis());
        }
    }

    @Test
    void createHistory_shouldThrow_whenUserIsNotDoctor() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(false);
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);
            assertThrows(RuntimeException.class, () -> service.createHistory(history));
        }
    }

    @Test
    void updateHistory_shouldUpdate_whenAuthorized() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(true);
            when(patientHistoryRepository.findById(1L)).thenReturn(Optional.of(history));
            when(patientHistoryRepository.save(any())).thenReturn(history);

            history.setDiagnosis("yenilendi");
            PatientHistory result = service.updateHistory(1L, history);
            assertEquals("yenilendi", result.getDiagnosis());
        }
    }

    @Test
    void deleteHistory_shouldDelete_whenAuthorized() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(true);
            service.deleteHistory(1L);
            verify(patientHistoryRepository).deleteById(1L);
        }
    }

    @Test
    void getAllHistories_shouldReturnList_whenAdmin() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(patientHistoryRepository.findAll()).thenReturn(List.of(history));
            List<PatientHistory> result = service.getAllHistories();
            assertFalse(result.isEmpty());
        }
    }

    @Test
    void getHistoriesByPatientId_shouldReturnList_whenAuthorized() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(patient);
            when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(patientHistoryRepository.findByPatient(patient)).thenReturn(List.of(history));
            List<PatientHistory> result = service.getHistoriesByPatientId(patient.getId());
            assertEquals(1, result.size());
        }
    }

    @Test
    void getHistoriesByDoctorId_shouldReturnList_whenDoctorOrAdmin() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);
            when(userRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
            when(patientHistoryRepository.findByDoctor(doctor)).thenReturn(List.of(history));
            List<PatientHistory> result = service.getHistoriesByDoctorId(doctor.getId());
            assertEquals(1, result.size());
        }
    }

    @Test
    void getHistoriesByPatientIdAndPeriod_shouldReturnFiltered() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(patient);
            when(userRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(patientHistoryRepository.findByPatientAndDateAfter(eq(patient), any()))
                    .thenReturn(List.of(history));
            List<PatientHistory> result = service.getHistoriesByPatientIdAndPeriod(patient.getId(), "week");
            assertFalse(result.isEmpty());
        }
    }

    @Test
    void getHistoriesByDoctorIdAndPeriod_shouldReturnFiltered() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);
            when(userRepository.findById(doctor.getId())).thenReturn(Optional.of(doctor));
            when(patientHistoryRepository.findByDoctorAndDateAfter(eq(doctor), any()))
                    .thenReturn(List.of(history));
            List<PatientHistory> result = service.getHistoriesByDoctorIdAndPeriod(doctor.getId(), "month");
            assertFalse(result.isEmpty());
        }
    }

    @Test
    void searchHistoriesByDiagnosis_shouldReturnMatching() {
        when(patientHistoryRepository.findByDiagnosisContainingIgnoreCase("grip"))
                .thenReturn(List.of(history));
        List<PatientHistory> result = service.searchHistoriesByDiagnosis("grip");
        assertEquals(1, result.size());
    }

    @Test
    void searchHistoriesByTreatment_shouldReturnMatching() {
        when(patientHistoryRepository.findByTreatmentContainingIgnoreCase("ilac"))
                .thenReturn(List.of(history));
        List<PatientHistory> result = service.searchHistoriesByTreatment("ilac");
        assertEquals(1, result.size());
    }
}
