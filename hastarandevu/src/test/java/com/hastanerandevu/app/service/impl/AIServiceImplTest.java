package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.*;
import com.hastanerandevu.app.repository.*;
import com.hastanerandevu.app.util.SecurityUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ClinicRepository clinicRepository;
    @Mock private ComplaintRepository complaintRepository;
    @Mock private AppointmentRepository appointmentRepository;

    @InjectMocks private AIServiceImpl aiService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        // OpenAI API key ve URL'yi sahte olarak ayarla
        ReflectionTestUtils.setField(aiService, "apiKey", "test-key");
        ReflectionTestUtils.setField(aiService, "apiUrl", "http://fake-api");

        // Static mock'lar için hazırlık
        mocks = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close(); // Static mock'u kapat
    }

    // -----------------------
    // analyzeComplaint
    // -----------------------
    @Test
    void analyzeComplaint_shouldReturnAIResponse_whenUserIsHasta() {
        User user = User.builder()
                .role(User.Role.HASTA)
                .chronicDiseases("Diabet")
                .build();

        when(SecurityUtil.getCurrentUser(userRepository)).thenReturn(user);
        when(clinicRepository.findAll()).thenReturn(List.of(
                Clinic.builder().name("Kardiyoloji").build()
        ));

        AIServiceImpl spyService = Mockito.spy(aiService);
        doReturn("AI cevabı").when(spyService).sendToOpenAI(any());

        String result = spyService.analyzeComplaint("Göğüs ağrısı");
        assertEquals("AI cevabı", result);
    }

    @Test
    void analyzeComplaint_shouldThrowException_whenUserIsNotHasta() {
        User user = User.builder().role(User.Role.ADMIN).build();
        when(SecurityUtil.getCurrentUser(userRepository)).thenReturn(user);

        assertThrows(RuntimeException.class, () ->
                aiService.analyzeComplaint("Baş ağrısı"));
    }

    // -----------------------
    // analyzeComplaintsForAdmin
    // -----------------------
    @Test
    void analyzeComplaintsForAdmin_shouldReturnAIResponse_whenAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(
                Complaint.builder()
                        .subject("Karmaşık sistem")
                        .content("İletişim zor")
                        .clinic(Clinic.builder().name("Dahiliye").build())
                        .build()
        ));

        AIServiceImpl spyService = Mockito.spy(aiService);
        doReturn("Admin AI yanıtı").when(spyService).sendToOpenAI(any());

        String result = spyService.analyzeComplaintsForAdmin();
        assertEquals("Admin AI yanıtı", result);
    }

    @Test
    void analyzeComplaintsForAdmin_shouldThrow_whenNotAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> aiService.analyzeComplaintsForAdmin());
    }

    // -----------------------
    // analyzeClinicLoad
    // -----------------------
    @Test
    void analyzeClinicLoad_shouldReturnAIResponse_whenAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        Clinic clinic = Clinic.builder().name("Göz").build();

        when(clinicRepository.findAll()).thenReturn(List.of(clinic));
        when(appointmentRepository.findByDateAfter(any())).thenReturn(List.of(
                Appointments.builder().clinic(clinic).build()
        ));
        when(complaintRepository.findByCreatedAtAfter(any())).thenReturn(List.of(
                Complaint.builder().clinic(clinic).build()
        ));

        AIServiceImpl spyService = Mockito.spy(aiService);
        doReturn("Clinic load response").when(spyService).sendToOpenAI(any());

        String result = spyService.analyzeClinicLoad();
        assertEquals("Clinic load response", result);
    }

    @Test
    void analyzeClinicLoad_shouldThrow_whenNotAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> aiService.analyzeClinicLoad());
    }

    // -----------------------
    // analyzeUserBehavior
    // -----------------------
    @Test
    void analyzeUserBehavior_shouldReturnAIResponse_whenAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        User user = User.builder()
                .name("Ayşe")
                .surname("Yılmaz")
                .birthDate("1990-05-20")
                .gender(User.Gender.KADIN)
                .bloodType(User.Bloodtype.ARH_POS)
                .chronicDiseases("Hipertansiyon")
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user));

        AIServiceImpl spyService = Mockito.spy(aiService);
        doReturn("Behavior response").when(spyService).sendToOpenAI(any());

        String result = spyService.analyzeUserBehavior();
        assertEquals("Behavior response", result);
    }

    @Test
    void analyzeUserBehavior_shouldThrow_whenNotAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> aiService.analyzeUserBehavior());
    }

    // -----------------------
    // generateRiskAlerts
    // -----------------------
    @Test
    void generateRiskAlerts_shouldReturnAIResponse_whenAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        Clinic clinic = Clinic.builder().name("Ortopedi").build();

        List<Appointments> appointments = List.of(
                Appointments.builder().clinic(clinic).date(LocalDate.now()).status(Appointments.Status.IPTAL_EDILDI).build(),
                Appointments.builder().clinic(clinic).date(LocalDate.now()).status(Appointments.Status.GEC_KALINDI).build()
        );

        when(appointmentRepository.findAll()).thenReturn(appointments);
        when(complaintRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(
                Complaint.builder().clinic(clinic).build(),
                Complaint.builder().clinic(clinic).build(),
                Complaint.builder().clinic(clinic).build()
        ));

        AIServiceImpl spyService = Mockito.spy(aiService);
        doReturn("Risk alert").when(spyService).sendToOpenAI(any());

        String result = spyService.generateRiskAlerts();
        assertEquals("Risk alert", result);
    }

    @Test
    void generateRiskAlerts_shouldThrow_whenNotAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> aiService.generateRiskAlerts());
    }

    // -----------------------
    // analyzeChart
    // -----------------------
    @Test
    void analyzeChart_shouldReturnAIResponse_whenAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        AIServiceImpl spyService = Mockito.spy(aiService);
        doReturn("Chart analyzed").when(spyService).sendToOpenAI(any());

        List<String> labels = List.of("A", "B", "C");
        List<Long> values = List.of(10L, 20L, 30L);

        String result = spyService.analyzeChart("Kan Grubu Dağılımı", labels, values);
        assertEquals("Chart analyzed", result);
    }

    @Test
    void analyzeChart_shouldThrow_whenNotAdmin() {
        when(SecurityUtil.hasRole("ADMIN")).thenReturn(false);
        assertThrows(RuntimeException.class, () ->
                aiService.analyzeChart("Başlık", List.of(), List.of()));
    }
}
