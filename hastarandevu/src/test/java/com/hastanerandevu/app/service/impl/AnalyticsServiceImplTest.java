package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.dto.Analytics.*;
import com.hastanerandevu.app.repository.AnalyticsRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.util.SecurityUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock private AnalyticsRepository analyticsRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private AnalyticsServiceImpl analyticsService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    // Yardımcı method: erişim izni test
    private void assertThrowsIfNotAdmin(Runnable methodCall) {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(false);
        assertThrows(RuntimeException.class, methodCall::run);
    }

    @Test void getAppointmentCountByClinic() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getAppointmentCountByClinic()).thenReturn(List.of(new ClinicAppointmentCountDTO()));
        assertEquals(1, analyticsService.getAppointmentCountByClinic().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getAppointmentCountByClinic());
    }

    @Test void getAppointmentCountByDate() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getAppointmentCountByDate()).thenReturn(List.of(new DateAppointmentCountDTO()));
        assertEquals(1, analyticsService.getAppointmentCountByDate().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getAppointmentCountByDate());
    }

    @Test void getAppointmentCountByStatus() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getAppointmentCountByStatus()).thenReturn(List.of(new AppointmentStatusCountDTO()));
        assertEquals(1, analyticsService.getAppointmentCountByStatus().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getAppointmentCountByStatus());
    }

    @Test void getAppointmentCountByDoctor() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getAppointmentCountByDoctor()).thenReturn(List.of(new DoctorAppointmentCountDTO()));
        assertEquals(1, analyticsService.getAppointmentCountByDoctor().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getAppointmentCountByDoctor());
    }

    @Test void getMonthlyUserRegistration() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getMonthlyUserRegistration()).thenReturn(List.of(new MonthlyUserRegistrationDTO()));
        assertEquals(1, analyticsService.getMonthlyUserRegistration().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getMonthlyUserRegistration());
    }

    @Test void getComplaintCountByStatus() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getComplaintCountByStatus()).thenReturn(List.of(new ComplaintStatusCountDTO()));
        assertEquals(1, analyticsService.getComplaintCountByStatus().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getComplaintCountByStatus());
    }

    @Test void getComplaintCountByClinic() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getComplaintCountByClinic()).thenReturn(List.of(new ClinicComplaintCountDTO()));
        assertEquals(1, analyticsService.getComplaintCountByClinic().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getComplaintCountByClinic());
    }

    @Test void getAppointmentCountByTimeSlot() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getAppointmentCountByTimeSlot()).thenReturn(List.of(new TimeSlotAppointmentCountDTO()));
        assertEquals(1, analyticsService.getAppointmentCountByTimeSlot().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getAppointmentCountByTimeSlot());
    }

    @Test void getUserCountByRole() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getUserCountByRole()).thenReturn(List.of(new UserRoleCountDTO(null, 0)));
        assertEquals(1, analyticsService.getUserCountByRole().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getUserCountByRole());
    }

    @Test void getUserCountByGender() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getUserCountByGender()).thenReturn(List.of(new UserGenderCountDTO(null, 0)));
        assertEquals(1, analyticsService.getUserCountByGender().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getUserCountByGender());
    }

    @Test void getUserCountByBloodType() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getUserCountByBloodType()).thenReturn(List.of(new UserBloodTypeCountDTO(null, 0)));
        assertEquals(1, analyticsService.getUserCountByBloodType().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getUserCountByBloodType());
    }

    @Test void getDoctorCountByClinic() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getDoctorCountByClinic()).thenReturn(List.of(new ClinicDoctorCountDTO()));
        assertEquals(1, analyticsService.getDoctorCountByClinic().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getDoctorCountByClinic());
    }

    @Test void getComplaintCountBySubject() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(analyticsRepository.getComplaintCountBySubject()).thenReturn(List.of(new ComplaintSubjectCountDTO()));
        assertEquals(1, analyticsService.getComplaintCountBySubject().size());

        assertThrowsIfNotAdmin(() -> analyticsService.getComplaintCountBySubject());
    }
}
