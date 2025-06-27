package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.*;
import com.hastanerandevu.app.repository.*;
import com.hastanerandevu.app.util.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ComplaintRepository complaintRepository;
    @Mock
    private TestResultRepository testResultRepository;
    @Mock
    private PrescriptionRepository prescriptionRepository;
    @Mock
    private PatientHistoryRepository patientHistoryRepository;
    @Mock
    private PatientReportRepository patientReportRepository;

    @InjectMocks
    private ExportServiceImpl exportService;

    @Test
    void exportUsers_shouldReturnCsvResponse() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

            User user = User.builder().id(1L).name("Ali").surname("Yılmaz").email("ali@example.com")
                    .phoneNumber("1234567890").role(User.Role.HASTA).gender(User.Gender.ERKEK)
                    .birthDate("2000-01-01").bloodType(User.Bloodtype.ARH_POS).build();

            when(userRepository.findAll()).thenReturn(List.of(user));

            ResponseEntity<Resource> response = exportService.exportUsers();
            assertEquals("text/csv", response.getHeaders().getContentType().toString());
        }
    }

    @Test
    void exportAppointments_shouldReturnCsvResponse() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

            Appointments appointment = Appointments.builder().id(1L)
                    .patient(User.builder().name("Ali").surname("Yılmaz").build())
                    .doctor(User.builder().name("Dr. Ahmet").surname("Demir").build())
                    .clinic(Clinic.builder().name("Dahiliye").build())
                    .date(LocalDate.of(2024, 1, 1)).time(java.time.LocalTime.of(10, 0))
                    .status(Appointments.Status.AKTIF).description("kontrol").build();

            when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

            ResponseEntity<Resource> response = exportService.exportAppointments();
            assertEquals("text/csv", response.getHeaders().getContentType().toString());
        }
    }

    @Test
    void exportComplaints_shouldReturnCsvResponse() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

            Complaint complaint = Complaint.builder().id(1L).subject("Geç randevu")
                    .content("Randevu çok geçti").status(Complaint.Status.BEKLEMEDE)
                    .createdAt(LocalDate.of(2024, 1, 1)).clinic(Clinic.builder().name("Dahiliye").build())
                    .user(User.builder().name("Ali").surname("Yılmaz").build())
                    .build();

            when(complaintRepository.findAll()).thenReturn(List.of(complaint));

            ResponseEntity<Resource> response = exportService.exportComplaints();
            assertEquals("text/csv", response.getHeaders().getContentType().toString());
        }
    }

    @Test
    void exportTestResults_shouldReturnCsvResponse() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

            TestResult result = TestResult.builder().id(1L).testName("Kan Tahlili")
                    .testType(TestResult.TestType.KAN_TAHLILI).result("Normal")
                    .patient(User.builder().name("Ali").surname("Yılmaz").build())
                    .doctor(User.builder().name("Dr. Ahmet").surname("Demir").build())
                    .testDate(LocalDate.of(2024, 1, 1)).build();

            when(testResultRepository.findAll()).thenReturn(List.of(result));

            ResponseEntity<Resource> response = exportService.exportTestResults();
            assertEquals("text/csv", response.getHeaders().getContentType().toString());
        }
    }

    @Test
    void exportPatientHistories_shouldReturnCsvResponse() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

            PatientHistory history = PatientHistory.builder().id(1L).diagnosis("Grip").treatment("İlaç")
                    .patient(User.builder().name("Ali").surname("Yılmaz").build())
                    .doctor(User.builder().name("Dr. Ahmet").surname("Demir").build())
                    .date(LocalDate.of(2024, 1, 1)).build();

            when(patientHistoryRepository.findAll()).thenReturn(List.of(history));

            ResponseEntity<Resource> response = exportService.exportPatientHistories();
            assertEquals("text/csv", response.getHeaders().getContentType().toString());
        }
    }

    @Test
    void exportPatientReports_shouldReturnCsvResponse() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

            PatientReports report = PatientReports.builder().id(1L).reportType("MR")
                    .reportDate(LocalDate.of(2024, 1, 1)).fileUrl("url")
                    .patient(User.builder().name("Ali").surname("Yılmaz").build())
                    .doctor(User.builder().name("Dr. Ahmet").surname("Demir").build())
                    .build();

            when(patientReportRepository.findAll()).thenReturn(List.of(report));

            ResponseEntity<Resource> response = exportService.exportPatientReports();
            assertEquals("text/csv", response.getHeaders().getContentType().toString());
        }
    }
}