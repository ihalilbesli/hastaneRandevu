package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.User;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User adminUser;
    private Clinic testClinic;

    @BeforeEach
    void setUp() {
        testClinic = Clinic.builder()
                .id(1L)
                .name("Test Clinic")
                .isActive(true)
                .build();

        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .surname("Test Surname")
                .email("test@example.com")
                .password("password")
                .role(User.Role.HASTA)
                .phoneNumber("1234567890")
                .gender(User.Gender.ERKEK)
                .birthDate("1990-01-01")
                .bloodType(User.Bloodtype.ARH_POS)
                .build();

        adminUser = User.builder()
                .id(2L)
                .name("Admin")
                .surname("User")
                .email("admin@example.com")
                .role(User.Role.ADMIN)
                .build();
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        User result = userService.registerUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        verify(userRepository).save(testUser);
    }

    @Test
    void findByEmail_WhenAuthorized_ShouldReturnUser() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testUser);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userService.findByEmail(testUser.getEmail());

            // Assert
            assertTrue(result.isPresent());
            assertEquals(testUser.getEmail(), result.get().getEmail());
        }
    }

    @Test
    void findByEmail_WhenUnauthorized_ShouldThrowException() {
        // Arrange
        User differentUser = User.builder().email("other@example.com").role(User.Role.HASTA).build();
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(differentUser);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.findByEmail(testUser.getEmail()));
        }
    }

    @Test
    void findByEmailContaining_WhenAdmin_ShouldReturnUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(userRepository.findByEmailContainingIgnoreCase(anyString())).thenReturn(users);

            // Act
            List<User> result = userService.findByEmailContainingIgnoreCase("test");

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(testUser.getEmail(), result.get(0).getEmail());
        }
    }

    @Test
    void findUsersByRole_WhenAdmin_ShouldReturnUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(userRepository.findByRole(any(User.Role.class))).thenReturn(users);

            // Act
            List<User> result = userService.findUsersByRole(User.Role.HASTA);

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(User.Role.HASTA, result.get(0).getRole());
        }
    }

    @Test
    void getUserById_WhenAuthorized_ShouldReturnUser() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testUser);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userService.getUserById(testUser.getId());

            // Assert
            assertTrue(result.isPresent());
            assertEquals(testUser.getId(), result.get().getId());
        }
    }

    @Test
    void findByPhoneNumber_WhenAuthorized_ShouldReturnUser() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.getCurrentUser(userRepository)).thenReturn(testUser);
            when(userRepository.findByPhoneNumber(anyString())).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userService.findByPhoneNumber(testUser.getPhoneNumber());

            // Assert
            assertTrue(result.isPresent());
            assertEquals(testUser.getPhoneNumber(), result.get().getPhoneNumber());
        }
    }

    @Test
    void findByNameContaining_WhenAdmin_ShouldReturnUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(userRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(users);

            // Act
            List<User> result = userService.findByNameContainingIgnoreCase("Test");

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(testUser.getName(), result.get(0).getName());
        }
    }

    @Test
    void findByGender_WhenAdmin_ShouldReturnUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(userRepository.findByGender(any(User.Gender.class))).thenReturn(users);

            // Act
            List<User> result = userService.findByGender(User.Gender.ERKEK);

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(User.Gender.ERKEK, result.get(0).getGender());
        }
    }

    @Test
    void findByBloodType_WhenAdmin_ShouldReturnUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(userRepository.findByBloodType(any(User.Bloodtype.class))).thenReturn(users);

            // Act
            List<User> result = userService.findByBloodType(User.Bloodtype.ARH_POS);

            // Assert
            assertFalse(result.isEmpty());
            assertEquals(User.Bloodtype.ARH_POS, result.get(0).getBloodType());
        }
    }

    @Test
    void findBySpecialization_ShouldReturnDoctors() {
        // Arrange
        User doctorUser = User.builder()
                .role(User.Role.DOKTOR)
                .specialization("Cardiology")
                .build();
        List<User> doctors = Arrays.asList(doctorUser);
        when(userRepository.findBySpecializationContainingIgnoreCase(anyString())).thenReturn(doctors);

        // Act
        List<User> result = userService.findBySpecializationContainingIgnoreCase("Card");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals("Cardiology", result.get(0).getSpecialization());
    }

    @Test
    void deleteUserById_WhenAdmin_ShouldDeleteUser() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);

            // Act
            userService.deleteUserById(1L);

            // Assert
            verify(userRepository).deleteById(1L);
        }
    }

    @Test
    void deleteUserById_WhenNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.deleteUserById(1L));
            verify(userRepository, never()).deleteById(anyLong());
        }
    }

    @Test
    void updateUser_WhenAdmin_ShouldUpdateUser() {
        // Arrange
        User updatedUser = User.builder()
                .id(1L)
                .name("Updated Name")
                .build();

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);

            // Act
            User result = userService.updateUser(1L, updatedUser);

            // Assert
            assertNotNull(result);
            assertEquals("Updated Name", result.getName());
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void getAllUsers_WhenAdmin_ShouldReturnAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(testUser, adminUser);
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(true);
            when(userRepository.findAll()).thenReturn(users);

            // Act
            List<User> result = userService.getAllUsers();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(userRepository).findAll();
        }
    }

    @Test
    void getAllUsers_WhenNotAdmin_ShouldThrowException() {
        // Arrange
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(() -> SecurityUtil.hasRole("ADMIN")).thenReturn(false);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.getAllUsers());
            verify(userRepository, never()).findAll();
        }
    }
} 