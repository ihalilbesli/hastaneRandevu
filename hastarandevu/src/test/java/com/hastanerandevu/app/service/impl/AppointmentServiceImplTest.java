package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.AppointmentRepository;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private User testPatient;
    private User testDoctor;
    private Clinic testClinic;
    private Appointments testAppointment;

    @BeforeEach
    void setUp() {
        testClinic = Clinic.builder()
                .id(1L)
                .name("Test Clinic")
                .isActive(true)
                .build();

        testDoctor = User.builder()
                .id(1L)
                .name("Test Doctor")
                .role(User.Role.DOKTOR)
                .clinic(testClinic)
                .build();

        testPatient = User.builder()
                .id(2L)
                .name("Test Patient")
                .role(User.Role.HASTA)
                .build();

        testAppointment = Appointments.builder()
                .id(1L)
                .patient(testPatient)
                .doctor(testDoctor)
                .clinic(testClinic)
                .date(LocalDate.now())
                .time(LocalTime.of(10, 0))
                .status(Appointments.Status.AKTIF)
                .build();
    }

    @Test
    void createAppointment_WhenValidPatient_ShouldCreateAppointment() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testPatient);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(testPatient), Optional.of(testDoctor));
            when(appointmentRepository.save(any(Appointments.class))).thenReturn(testAppointment);

            // Act
            Appointments result = appointmentService.createAppointment(testAppointment);

            // Assert
            assertNotNull(result);
            assertEquals(testAppointment.getPatient().getId(), result.getPatient().getId());
            assertEquals(testAppointment.getDoctor().getId(), result.getDoctor().getId());
            assertEquals(Appointments.Status.AKTIF, result.getStatus());
            verify(appointmentRepository).save(any(Appointments.class));
        }
    }

    @Test
    void createAppointment_WhenNotPatient_ShouldThrowException() {
        // Arrange
        User nonPatientUser = User.builder().role(User.Role.DOKTOR).build();
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(nonPatientUser);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> appointmentService.createAppointment(testAppointment));
            verify(appointmentRepository, never()).save(any(Appointments.class));
        }
    }

    @Test
    void getAppointmentsByPatientId_WhenAuthorized_ShouldReturnAppointments() {
        // Arrange
        List<Appointments> appointments = Arrays.asList(testAppointment);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testPatient);
            when(appointmentRepository.findAll()).thenReturn(appointments);

            // Act
            List<Appointments> result = appointmentService.getAppointemnrsByPatientId(testPatient.getId());

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(testAppointment.getId(), result.get(0).getId());
        }
    }

    @Test
    void getAppointmentsByPatientId_WhenUnauthorized_ShouldThrowException() {
        // Arrange
        User differentUser = User.builder().id(3L).role(User.Role.HASTA).build();
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(differentUser);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> appointmentService.getAppointemnrsByPatientId(testPatient.getId()));
        }
    }

    @Test
    void getAppointmentsByDoctorId_WhenAuthorized_ShouldReturnAppointments() {
        // Arrange
        List<Appointments> appointments = Arrays.asList(testAppointment);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testDoctor);
            when(appointmentRepository.findAll()).thenReturn(appointments);

            // Act
            List<Appointments> result = appointmentService.getAppointmensByDoctorId(testDoctor.getId());

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(testAppointment.getId(), result.get(0).getId());
        }
    }

    @Test
    void isDoctorAvailable_ShouldReturnAvailability() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testDoctor));
        when(appointmentRepository.findByDoctorAndDateAndTime(any(), any(), any())).thenReturn(List.of());

        // Act
        boolean result = appointmentService.isDoctorAvailable(
                testDoctor.getId(),
                LocalDate.now(),
                LocalTime.of(10, 0)
        );

        // Assert
        assertTrue(result);
    }

    @Test
    void getAllAppointments_WhenAdmin_ShouldReturnAllAppointments() {
        // Arrange
        List<Appointments> appointments = Arrays.asList(testAppointment);
        User adminUser = User.builder().role(User.Role.ADMIN).build();
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(adminUser);
            when(appointmentRepository.findAll()).thenReturn(appointments);

            // Act
            List<Appointments> result = appointmentService.getAllAppointments();

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(testAppointment.getId(), result.get(0).getId());
        }
    }

    @Test
    void getAllAppointments_WhenNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testPatient);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> appointmentService.getAllAppointments());
            verify(appointmentRepository, never()).findAll();
        }
    }

    @Test
    void cancelAppointment_WhenActive_ShouldCancel() {
        // Arrange
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointments.class))).thenReturn(testAppointment);

        // Act
        appointmentService.cancelAppointment(testAppointment.getId());

        // Assert
        assertEquals(Appointments.Status.IPTAL_EDILDI, testAppointment.getStatus());
        verify(appointmentRepository).save(testAppointment);
    }

    @Test
    void cancelAppointment_WhenAlreadyCancelled_ShouldThrowException() {
        // Arrange
        testAppointment.setStatus(Appointments.Status.IPTAL_EDILDI);
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(testAppointment));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> appointmentService.cancelAppointment(testAppointment.getId()));
        verify(appointmentRepository, never()).save(any(Appointments.class));
    }

    @Test
    void updateStatus_WhenAuthorizedDoctor_ShouldUpdateStatus() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testDoctor);
            when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(testAppointment));
            when(appointmentRepository.save(any(Appointments.class))).thenReturn(testAppointment);

            // Act
            Appointments result = appointmentService.updateStatus(
                    testAppointment.getId(),
                    Appointments.Status.COMPLETED,
                    "Test note"
            );

            // Assert
            assertNotNull(result);
            assertEquals(Appointments.Status.COMPLETED, result.getStatus());
            verify(appointmentRepository).save(any(Appointments.class));
        }
    }

    @Test
    void updateStatus_WhenUnauthorized_ShouldThrowException() {
        // Arrange
        User unauthorizedUser = User.builder().role(User.Role.HASTA).build();
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(unauthorizedUser);
            when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(testAppointment));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> appointmentService.updateStatus(
                    testAppointment.getId(),
                    Appointments.Status.COMPLETED,
                    "Test note"
            ));
            verify(appointmentRepository, never()).save(any(Appointments.class));
        }
    }

    @Test
    void getAllAppointmentsByPeriod_WhenAdmin_ShouldReturnAppointments() {
        // Arrange
        List<Appointments> appointments = Arrays.asList(testAppointment);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(appointmentRepository.findByDateAfter(any())).thenReturn(appointments);

            // Act
            List<Appointments> result = appointmentService.getAllAppointmentsByPeriod("week");

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(testAppointment.getId(), result.get(0).getId());
        }
    }

    @Test
    void searchAppointmentsByKeyword_WhenAdmin_ShouldReturnMatchingAppointments() {
        // Arrange
        List<Appointments> appointments = Arrays.asList(testAppointment);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(appointmentRepository.findByDescriptionContainingIgnoreCase(anyString())).thenReturn(appointments);

            // Act
            List<Appointments> result = appointmentService.searchAppointmentsByKeyword("test");

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(testAppointment.getId(), result.get(0).getId());
        }
    }

    @Test
    void countAppointmentsByStatus_WhenAdmin_ShouldReturnCount() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(appointmentRepository.countByStatus(any())).thenReturn(5L);

            // Act
            long result = appointmentService.countAppointmentsByStatus(Appointments.Status.AKTIF);

            // Assert
            assertEquals(5L, result);
        }
    }

    @Test
    void getAppointmentsByDoctorIdAndDate_WhenAuthorized_ShouldReturnAppointments() {
        // Arrange
        List<Appointments> appointments = Arrays.asList(testAppointment);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testPatient);
            when(appointmentRepository.findByDoctorIdAndDate(anyLong(), any())).thenReturn(appointments);

            // Act
            List<Appointments> result = appointmentService.getAppointmentsByDoctorIdAndDate(
                    testDoctor.getId(),
                    LocalDate.now()
            );

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(testAppointment.getId(), result.get(0).getId());
        }
    }
} 