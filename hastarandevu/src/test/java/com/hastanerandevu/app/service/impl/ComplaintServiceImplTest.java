package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.Complaint;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.ClinicRepository;
import com.hastanerandevu.app.repository.ComplaintRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceImplTest {

    @Mock private ComplaintRepository complaintRepository;
    @Mock private UserRepository userRepository;
    @Mock private ClinicRepository clinicRepository;
    @InjectMocks private ComplaintServiceImpl complaintService;

    private User user;
    private Clinic clinic;
    private Complaint complaint;

    @BeforeEach
    void setUp() {
        clinic = Clinic.builder().id(1L).name("KBB").isActive(true).build();
        user = User.builder().id(1L).name("Mehmet").role(User.Role.HASTA).clinic(clinic).build();
        complaint = Complaint.builder()
                .id(1L)
                .user(user)
                .subject("Gürültü")
                .content("Bekleme salonu çok gürültülüydü")
                .status(Complaint.Status.BEKLEMEDE)
                .createdAt(LocalDate.now())
                .clinic(clinic)
                .build();
    }

    @Test
    void createComplaint_shouldCreate_whenSameUser() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(user);
            when(clinicRepository.findById(1L)).thenReturn(Optional.of(clinic));
            when(complaintRepository.save(any())).thenReturn(complaint);

            Complaint result = complaintService.createComplaint(complaint);
            assertNotNull(result);
            assertEquals(complaint.getSubject(), result.getSubject());
        }
    }

    @Test
    void getComplaintByUserId_shouldReturnList_whenSameUserOrAdmin() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(user);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(complaintRepository.findByUser(user)).thenReturn(List.of(complaint));

            List<Complaint> list = complaintService.getComplaintByUserId(1L);
            assertEquals(1, list.size());
        }
    }

    @Test
    void getComplaintByUserIdAndDate_shouldFilterCorrectly() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(user);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(complaintRepository.findByUserAndCreatedAtAfter(eq(user), any())).thenReturn(List.of(complaint));

            List<Complaint> results = complaintService.getComplaintByUserIdAndDate(1L, "week");
            assertEquals(1, results.size());
        }
    }

    @Test
    void getAllComplaint_shouldReturnAll_whenAdmin() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(complaintRepository.findAll()).thenReturn(List.of(complaint));

            List<Complaint> result = complaintService.getAllComplaint();
            assertEquals(1, result.size());
        }
    }

    @Test
    void getComplaintByStatus_shouldReturnList_whenAdminOrDoctor() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(complaintRepository.findByStatus(Complaint.Status.BEKLEMEDE)).thenReturn(List.of(complaint));

            List<Complaint> result = complaintService.getComplaintByStatus(Complaint.Status.BEKLEMEDE);
            assertEquals(1, result.size());
        }
    }

    @Test
    void updateComplaint_shouldUpdate_whenAuthorized() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
            when(complaintRepository.save(any())).thenReturn(complaint);

            Complaint updated = Complaint.builder()
                    .content("Güncel içerik")
                    .adminNote("Çözüm notu")
                    .status(Complaint.Status.COZULDU)
                    .build();

            Complaint result = complaintService.updateComplaint(1L, updated);
            assertEquals("Güncel içerik", result.getContent());
            assertEquals("Çözüm notu", result.getAdminNote());
            assertEquals(Complaint.Status.COZULDU, result.getStatus());
        }
    }

    @Test
    void deleteComplaint_shouldWork_whenAdmin() {
        try (MockedStatic<SecurityUtil> mocked = mockStatic(SecurityUtil.class)) {
            mocked.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            doNothing().when(complaintRepository).deleteById(1L);

            complaintService.deleteComplaint(1L);
            verify(complaintRepository).deleteById(1L);
        }
    }
}
