package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.PatientReports;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PatientReportRepository;
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
class PatientReportsServiceImplTest {

    @Mock private PatientReportRepository reportRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private PatientReportsServiceImpl service;

    private User doktor;
    private User hasta;
    private PatientReports rapor;

    @BeforeEach
    void setup() {
        doktor = User.builder().id(1L).role(User.Role.DOKTOR).name("Doktor").build();
        hasta = User.builder().id(2L).role(User.Role.HASTA).name("Hasta").build();
        rapor = PatientReports.builder()
                .id(10L)
                .patient(hasta)
                .doctor(doktor)
                .reportType("MR")
                .fileUrl("url")
                .reportDate(LocalDate.now())
                .build();
    }

    @Test
    void createReport_shouldCreate_whenRoleIsDoctor() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doktor);
            when(userRepository.findById(2L)).thenReturn(Optional.of(hasta));
            when(reportRepository.save(any())).thenReturn(rapor);

            PatientReports result = service.createReport(rapor);

            assertEquals("MR", result.getReportType());
            verify(reportRepository).save(any());
        }
    }

    @Test
    void getAllReports_shouldReturnList_whenAdmin() {
        User admin = User.builder().id(3L).role(User.Role.ADMIN).build();
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(admin);
            when(reportRepository.findAll()).thenReturn(List.of(rapor));

            List<PatientReports> result = service.getAllReports();
            assertEquals(1, result.size());
        }
    }

    @Test
    void getReportsByPatientId_shouldReturnOwnReports_whenHasta() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(hasta);
            when(reportRepository.findByPatientId(hasta.getId())).thenReturn(List.of(rapor));

            List<PatientReports> result = service.getReportsByPatientId(hasta.getId());
            assertEquals(1, result.size());
        }
    }

    @Test
    void getReportsByDoctorId_shouldReturnReports_whenAuthorized() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doktor);
            when(reportRepository.findByDoctorId(doktor.getId())).thenReturn(List.of(rapor));

            List<PatientReports> result = service.getReportsByDoctorId(doktor.getId());
            assertEquals(1, result.size());
        }
    }

    @Test
    void updateReport_shouldUpdate_whenAuthorized() {
        User admin = User.builder().id(4L).role(User.Role.ADMIN).build();
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(false);
            util.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

            PatientReports updated = PatientReports.builder().reportType("Yeni Tip").fileUrl("yeni-url").build();

            when(reportRepository.findById(10L)).thenReturn(Optional.of(rapor));
            when(reportRepository.save(any())).thenReturn(rapor);

            PatientReports result = service.updateReport(10L, updated);

            assertEquals("Yeni Tip", result.getReportType());
        }
    }

    @Test
    void deleteReport_shouldDelete_whenAuthorized() {
        try (MockedStatic<SecurityUtil> util = mockStatic(SecurityUtil.class)) {
            util.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(true);
            service.deleteReport(10L);
            verify(reportRepository).deleteById(10L);
        }
    }
}
