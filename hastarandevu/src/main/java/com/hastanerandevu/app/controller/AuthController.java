package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.dto.Auth.AuthResponse;
import com.hastanerandevu.app.dto.Auth.ResetPasswordRequest;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.service.AuthService;
import com.hastanerandevu.app.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hastarandevu/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    //  Kayıt işlemi
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new RuntimeException("Email alanı boş bırakılamaz.");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new RuntimeException("Şifre alanı boş bırakılamaz.");
        }
        User savedUser = authService.register(user);
        return ResponseEntity.ok(savedUser);
    }

    //  Giriş işlemi
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()) {
            throw new RuntimeException("Email boş olamaz.");
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            throw new RuntimeException("Şifre boş olamaz.");
        }

        User user = authService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());

        // Giriş başarılıysa JWT token üret
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    //  Şifre sıfırlama işlemi
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email boş olamaz.");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new RuntimeException("Yeni şifre boş olamaz.");
        }

        authService.resetPassword(
                request.getEmail(),
                request.getName(),
                request.getSurname(),
                request.getBirthDate(),
                request.getNewPassword()
        );
        return ResponseEntity.ok("Şifre başarıyla güncellendi.");
    }

    //  Giriş isteği için inner class
    public static class LoginRequest {
        private String email;
        private String password;

        // Getter ve Setter
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
