package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.ClinicRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicServiceImplTest {

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClinicServiceImpl clinicService;

    private Clinic testClinic;
    private User testDoctor;

    @BeforeEach
    void setUp() {
        testClinic = Clinic.builder()
                .id(1L)
                .name("Test Clinic")
                .description("Test Description")
                .isActive(true)
                .build();

        testDoctor = User.builder()
                .id(1L)
                .name("Test Doctor")
                .clinic(testClinic)
                .role(User.Role.DOKTOR)
                .build();
    }

    @Test
    void createClinic_WhenAdmin_ShouldCreateClinic() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(clinicRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
            when(clinicRepository.save(any(Clinic.class))).thenReturn(testClinic);

            // Act
            Clinic result = clinicService.createClinic(testClinic);

            // Assert
            assertNotNull(result);
            assertEquals(testClinic.getName(), result.getName());
            verify(clinicRepository).save(any(Clinic.class));
        }
    }

    @Test
    void createClinic_WhenNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clinicService.createClinic(testClinic));
            verify(clinicRepository, never()).save(any(Clinic.class));
        }
    }

    @Test
    void createClinic_WhenClinicNameExists_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(clinicRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clinicService.createClinic(testClinic));
            verify(clinicRepository, never()).save(any(Clinic.class));
        }
    }

    @Test
    void getAllClinics_WhenAuthorized_ShouldReturnClinics() {
        // Arrange
        List<Clinic> clinics = Arrays.asList(testClinic);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(clinicRepository.findAll()).thenReturn(clinics);

            // Act
            List<Clinic> result = clinicService.getAllClinics();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testClinic.getName(), result.get(0).getName());
            verify(clinicRepository).findAll();
        }
    }

    @Test
    void getAllClinics_WhenUnauthorized_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);
            securityUtil.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(false);
            securityUtil.when(() -> SecurityUtil.hasRole("HASTA")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clinicService.getAllClinics());
            verify(clinicRepository, never()).findAll();
        }
    }

    @Test
    void getClinicById_WhenAdmin_ShouldReturnClinic() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(clinicRepository.findById(anyLong())).thenReturn(Optional.of(testClinic));

            // Act
            Optional<Clinic> result = clinicService.getClinicById(1L);

            // Assert
            assertTrue(result.isPresent());
            assertEquals(testClinic.getName(), result.get().getName());
            verify(clinicRepository).findById(1L);
        }
    }

    @Test
    void getClinicById_WhenNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clinicService.getClinicById(1L));
            verify(clinicRepository, never()).findById(anyLong());
        }
    }

    @Test
    void updateClinic_WhenAdmin_ShouldUpdateClinic() {
        // Arrange
        Clinic updatedClinic = Clinic.builder()
                .id(1L)
                .name("Updated Clinic")
                .description("Updated Description")
                .isActive(true)
                .build();

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(clinicRepository.findById(anyLong())).thenReturn(Optional.of(testClinic));
            when(clinicRepository.save(any(Clinic.class))).thenReturn(updatedClinic);

            // Act
            Clinic result = clinicService.updateClinic(1L, updatedClinic);

            // Assert
            assertNotNull(result);
            assertEquals(updatedClinic.getName(), result.getName());
            assertEquals(updatedClinic.getDescription(), result.getDescription());
            verify(clinicRepository).save(any(Clinic.class));
        }
    }

    @Test
    void updateClinic_WhenNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clinicService.updateClinic(1L, testClinic));
            verify(clinicRepository, never()).save(any(Clinic.class));
        }
    }

    @Test
    void deactivateClinic_WhenAdmin_ShouldDeactivateClinic() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(clinicRepository.findById(anyLong())).thenReturn(Optional.of(testClinic));
            when(clinicRepository.save(any(Clinic.class))).thenReturn(testClinic);

            // Act
            clinicService.deactivateClinic(1L);

            // Assert
            verify(clinicRepository).save(any(Clinic.class));
            assertFalse(testClinic.getIsActive());
        }
    }

    @Test
    void deactivateClinic_WhenNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clinicService.deactivateClinic(1L));
            verify(clinicRepository, never()).save(any(Clinic.class));
        }
    }

    @Test
    void activateClinic_WhenAdmin_ShouldActivateClinic() {
        // Arrange
        testClinic.setIsActive(false);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(clinicRepository.findById(anyLong())).thenReturn(Optional.of(testClinic));
            when(clinicRepository.save(any(Clinic.class))).thenReturn(testClinic);

            // Act
            clinicService.activateClinic(1L);

            // Assert
            verify(clinicRepository).save(any(Clinic.class));
            assertTrue(testClinic.getIsActive());
        }
    }

    @Test
    void activateClinic_WhenNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clinicService.activateClinic(1L));
            verify(clinicRepository, never()).save(any(Clinic.class));
        }
    }

    @Test
    void existsByName_ShouldReturnCorrectValue() {
        // Arrange
        when(clinicRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);

        // Act
        boolean result = clinicService.existsByName("Test Clinic");

        // Assert
        assertFalse(result);
        verify(clinicRepository).existsByNameIgnoreCase("Test Clinic");
    }

    @Test
    void getDoctorsByClinicId_WhenAuthorized_ShouldReturnDoctors() {
        // Arrange
        List<User> doctors = Arrays.asList(testDoctor);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(clinicRepository.findById(anyLong())).thenReturn(Optional.of(testClinic));
            when(userRepository.findByClinic(any(Clinic.class))).thenReturn(doctors);

            // Act
            List<User> result = clinicService.getDoctorsByClinicId(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(testDoctor.getName(), result.get(0).getName());
            verify(userRepository).findByClinic(testClinic);
        }
    }

    @Test
    void getDoctorsByClinicId_WhenUnauthorized_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);
            securityUtil.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(false);
            securityUtil.when(() -> SecurityUtil.hasRole("HASTA")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> clinicService.getDoctorsByClinicId(1L));
            verify(userRepository, never()).findByClinic(any(Clinic.class));
        }
    }
} 