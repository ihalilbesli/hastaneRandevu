package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorPatientServiceImplTest {

    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private TestResultRepository testResultRepository;
    @Mock private PatientHistoryRepository patientHistoryRepository;
    @Mock private PatientReportRepository patientReportRepository;
    @Mock private UserRepository userRepository;
    @Mock private AppointmentRepository appointmentRepository;

    @InjectMocks private DoctorPatientServiceImpl doctorPatientService;

    private User doctor;
    private User patient;
    private Appointments completedAppointment;

    @BeforeEach
    void setUp() {
        doctor = User.builder().id(1L).name("Dr. Smith").role(User.Role.DOKTOR).build();
        patient = User.builder().id(2L).name("John Doe").email("john@example.com").build();

        completedAppointment = Appointments.builder()
                .id(10L)
                .doctor(doctor)
                .patient(patient)
                .date(LocalDate.now())
                .status(Appointments.Status.COMPLETED)
                .build();
    }

    @Test
    void getMyPatients_whenDoctor_shouldReturnUniquePatients() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);

            when(prescriptionRepository.findByDoctor(doctor)).thenReturn(List.of());
            when(testResultRepository.findByDoctor(doctor)).thenReturn(List.of());
            when(patientHistoryRepository.findByDoctor(doctor)).thenReturn(List.of());
            when(patientReportRepository.findByDoctor(doctor)).thenReturn(List.of());
            when(appointmentRepository.findByDoctor(doctor)).thenReturn(List.of(completedAppointment));

            List<User> result = doctorPatientService.getMyPatients();

            assertEquals(1, result.size());
            assertEquals("John Doe", result.get(0).getName());
        }
    }

    @Test
    void getMyPatients_whenNotDoctor_shouldThrow() {
        User admin = User.builder().id(99L).role(User.Role.ADMIN).build();
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(admin);
            assertThrows(RuntimeException.class, () -> doctorPatientService.getMyPatients());
        }
    }

    @Test
    void searchMyPatientsByName_shouldFilterCorrectly() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);

            when(prescriptionRepository.findByDoctor(doctor)).thenReturn(List.of());
            when(testResultRepository.findByDoctor(doctor)).thenReturn(List.of());
            when(patientHistoryRepository.findByDoctor(doctor)).thenReturn(List.of());
            when(patientReportRepository.findByDoctor(doctor)).thenReturn(List.of());
            when(appointmentRepository.findByDoctor(doctor)).thenReturn(List.of(completedAppointment));

            List<User> result = doctorPatientService.searchMyPatientsByName("john");

            assertEquals(1, result.size());
            assertTrue(result.get(0).getName().toLowerCase().contains("john"));
        }
    }

    @Test
    void getMyPatientsToday_whenAuthorized_shouldReturnTodayPatients() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);

            when(appointmentRepository.findByDoctorIdAndDate(eq(doctor.getId()), any(LocalDate.class)))
                    .thenReturn(List.of(completedAppointment));

            List<User> result = doctorPatientService.getMyPatientsToday();

            assertEquals(1, result.size());
            assertEquals(patient.getName(), result.get(0).getName());
        }
    }

    @Test
    void getMyPatientsToday_whenUnauthorized_shouldThrow() {
        User hasta = User.builder().id(3L).role(User.Role.HASTA).build();
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(hasta);

            assertThrows(RuntimeException.class, () -> doctorPatientService.getMyPatientsToday());
        }
    }
}