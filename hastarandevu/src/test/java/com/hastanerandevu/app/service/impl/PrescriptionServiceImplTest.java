package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Prescription;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.PrescriptionRepository;
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
class PrescriptionServiceImplTest {

    @Mock private PrescriptionRepository prescriptionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private PrescriptionServiceImpl prescriptionService;

    private Prescription prescription;
    private User doctor;
    private User patient;

    @BeforeEach
    void setUp() {
        doctor = User.builder()
                .id(1L)
                .role(User.Role.DOKTOR)
                .build();

        patient = User.builder()
                .id(2L)
                .role(User.Role.HASTA)
                .build();

        prescription = Prescription.builder()
                .id(100L)
                .description("Test aciklama")
                .medications("ilac1, ilac2")
                .patient(patient)
                .build();
    }

    @Test
    void createPrescription_shouldGenerateCodeAndSave() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(doctor);
            when(userRepository.findById(2L)).thenReturn(Optional.of(patient));
            when(prescriptionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Prescription result = prescriptionService.createPrescription(prescription);

            assertNotNull(result.getPrescriptionCode());
            assertEquals(doctor, result.getDoctor());
            assertEquals(patient, result.getPatient());
            assertEquals(LocalDate.now(), result.getDate());
        }
    }

    @Test
    void getAllPrescriptions_shouldReturnList_whenAdmin() {
        User admin = User.builder().id(9L).role(User.Role.ADMIN).build();
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(prescriptionRepository.findAll()).thenReturn(List.of(prescription));

            List<Prescription> result = prescriptionService.getAllPrescriptions();

            assertEquals(1, result.size());
        }
    }

    @Test
    void deletePrescription_shouldCallRepo_whenAuthorized() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(true);
            doNothing().when(prescriptionRepository).deleteById(100L);

            assertDoesNotThrow(() -> prescriptionService.deletePrescription(100L));
            verify(prescriptionRepository).deleteById(100L);
        }
    }

    @Test
    void updatePrescription_shouldSaveNewDescription() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.hasRole("DOKTOR")).thenReturn(true);
            Prescription updated = Prescription.builder().description("guncel desc").build();

            when(prescriptionRepository.findById(100L)).thenReturn(Optional.of(prescription));
            when(prescriptionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            Prescription result = prescriptionService.updatePrescription(100L, updated);

            assertEquals("guncel desc", result.getDescription());
        }
    }
}
