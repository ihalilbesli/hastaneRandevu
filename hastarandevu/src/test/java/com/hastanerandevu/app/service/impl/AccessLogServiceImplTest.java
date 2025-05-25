package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.AccessLog;
import com.hastanerandevu.app.repository.AccessLogRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.util.SecurityUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class AccessLogServiceImplTest {

    @Mock
    private AccessLogRepository accessLogRepository;

    @Mock
    private UserRepository userRepository;

    private AccessLogServiceImpl accessLogService;

    private static MockedStatic<SecurityUtil> mockedSecurityUtil;

    @BeforeAll
    static void init() {
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterAll
    static void tearDown() {
        mockedSecurityUtil.close();
    }

    @BeforeEach
    void setUp() {
        accessLogService = new AccessLogServiceImpl(accessLogRepository, userRepository);
    }

    @Test
    void getAllLogs_AdminAccess_ShouldReturnLogs() {
        List<AccessLog> logs = List.of(new AccessLog(), new AccessLog());
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(accessLogRepository.findAll()).thenReturn(logs);

        List<AccessLog> result = accessLogService.getAllLogs();

        assertEquals(2, result.size());
        verify(accessLogRepository).findAll();
    }

    @Test
    void getAllLogs_NonAdminAccess_ShouldThrowException() {
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> accessLogService.getAllLogs());
        verify(accessLogRepository, never()).findAll();
    }

    @Test
    void getLogsByEmail_AdminAccess_ShouldReturnLogs() {
        String email = "test@example.com";
        List<AccessLog> logs = List.of(new AccessLog());
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(accessLogRepository.findByUserEmail(email)).thenReturn(logs);

        List<AccessLog> result = accessLogService.getLogsByEmail(email);

        assertEquals(1, result.size());
        verify(accessLogRepository).findByUserEmail(email);
    }

    @Test
    void getLogsByEmail_NonAdminAccess_ShouldThrowException() {
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> accessLogService.getLogsByEmail("email"));
        verify(accessLogRepository, never()).findByUserEmail(anyString());
    }

    @Test
    void getLogsByRole_AdminAccess_ShouldReturnLogs() {
        String role = "ADMIN";
        List<AccessLog> logs = List.of(new AccessLog());
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(accessLogRepository.findByRole(role)).thenReturn(logs);

        List<AccessLog> result = accessLogService.getLogsByRole(role);

        assertEquals(1, result.size());
        verify(accessLogRepository).findByRole(role);
    }

    @Test
    void getLogsByRole_NonAdminAccess_ShouldThrowException() {
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> accessLogService.getLogsByRole("ADMIN"));
        verify(accessLogRepository, never()).findByRole(anyString());
    }

    @Test
    void getLogsByStatus_AdminAccess_ShouldReturnLogs() {
        String status = "ERROR";
        List<AccessLog> logs = List.of(new AccessLog());
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(accessLogRepository.findByStatus(status)).thenReturn(logs);

        List<AccessLog> result = accessLogService.getLogsByStatus(status);

        assertEquals(1, result.size());
        verify(accessLogRepository).findByStatus(status);
    }

    @Test
    void getLogsByStatus_NonAdminAccess_ShouldThrowException() {
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> accessLogService.getLogsByStatus("ERROR"));
        verify(accessLogRepository, never()).findByStatus(anyString());
    }

    @Test
    void saveLog_ShouldSaveLog() {
        AccessLog log = new AccessLog();

        accessLogService.saveLog(log);

        verify(accessLogRepository).save(log);
    }

    @Test
    void getLogsByPeriod_AdminAccess_ShouldReturnLogs() {
        String period = "week";
        List<AccessLog> logs = List.of(new AccessLog());
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
        when(accessLogRepository.findByTimestampBetween(any(), any())).thenReturn(logs);

        List<AccessLog> result = accessLogService.getLogsByPeriod(period);

        assertEquals(1, result.size());
        verify(accessLogRepository).findByTimestampBetween(any(), any());
    }

    @Test
    void getLogsByPeriod_InvalidPeriod_ShouldThrowException() {
        String period = "invalid";
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> accessLogService.getLogsByPeriod(period));
        verify(accessLogRepository, never()).findByTimestampBetween(any(), any());
    }

    @Test
    void getLogsByPeriod_NonAdminAccess_ShouldThrowException() {
        mockedSecurityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> accessLogService.getLogsByPeriod("week"));
        verify(accessLogRepository, never()).findByTimestampBetween(any(), any());
    }
}
