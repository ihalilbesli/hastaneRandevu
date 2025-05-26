package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.util.JwtUtil;
import com.hastanerandevu.app.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User hastaUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        hastaUser = User.builder()
                .id(1L)
                .email("hasta@example.com")
                .password("12345678")
                .role(User.Role.HASTA)
                .build();

        adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .password("admin123")
                .role(User.Role.ADMIN)
                .build();
    }

    @Test
    void register_shouldSetRoleToHasta_whenAnonymousUser() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            mockedStatic.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenThrow(new RuntimeException("Anonim"));
            when(passwordEncoder.encode(anyString())).thenReturn("encoded123");
            when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

            User newUser = User.builder().password("12345678").build();
            User result = authService.register(newUser);

            assertEquals(User.Role.HASTA, result.getRole());
            assertEquals("encoded123", result.getPassword());
        }
    }

  /*  @Test
    void register_shouldThrow_whenLoggedUserIsNotAdmin() {
        try (MockedStatic<SecurityUtil> mockedStatic = mockStatic(SecurityUtil.class)) {
            User mockedUser = User.builder().role(User.Role.DOKTOR).build();
            mockedStatic.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(mockedUser);

            User newUser = User.builder().password("abc123").build();
            assertThrows(RuntimeException.class, () -> authService.register(newUser));
        }
    }*/

    @Test
    void loginUser_shouldReturnUser_whenCredentialsValid() {
        when(userRepository.findByEmail(hastaUser.getEmail())).thenReturn(Optional.of(hastaUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        User result = authService.loginUser("hasta@example.com", "12345678");
        assertNotNull(result);
        assertEquals(hastaUser.getEmail(), result.getEmail());
    }

    @Test
    void loginUser_shouldThrow_whenEmailInvalid() {
        when(userRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authService.loginUser("wrong@example.com", "pass"));
    }

    @Test
    void loginUser_shouldThrow_whenPasswordWrong() {
        when(userRepository.findByEmail(hastaUser.getEmail())).thenReturn(Optional.of(hastaUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.loginUser(hastaUser.getEmail(), "wrongpass"));
    }

    @Test
    void encodePassword_shouldReturnEncodedPassword() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        String result = authService.encodePassword("plainPassword");
        assertEquals("hashedPassword", result);
    }

    @Test
    void validateToken_shouldReturnTrue_whenValid() {
        when(jwtUtil.validateToken("token123")).thenReturn(true);
        assertTrue(authService.validateToken("token123"));
    }

    @Test
    void validateToken_shouldReturnFalse_whenInvalid() {
        when(jwtUtil.validateToken("invalidToken")).thenReturn(false);
        assertFalse(authService.validateToken("invalidToken"));
    }
}
