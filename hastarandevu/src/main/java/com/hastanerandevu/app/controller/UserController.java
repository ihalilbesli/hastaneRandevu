package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.dto.PasswordChange.PasswordChangeRequest;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.service.UserService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hastarandevu/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //  Email ile kullanıcı bulma
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(
                userService.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Email ile kullanıcı bulunamadı: " + email))
        );
    }

    //  Rol bilgisine göre listeleme
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUserByRole(@PathVariable User.Role role) {
        return ResponseEntity.ok(userService.findUsersByRole(role));
    }

    //  ID ile kullanıcı getirme
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                userService.getUserById(id)
                        .orElseThrow(() -> new RuntimeException("ID ile kullanıcı bulunamadı: " + id))
        );
    }

    //  Telefon numarası ile kullanıcı getirme
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(
                userService.findByPhoneNumber(phoneNumber)
                        .orElseThrow(() -> new RuntimeException("Telefon numarası ile kullanıcı bulunamadı: " + phoneNumber))
        );
    }

    //  İsme göre kullanıcı arama
    @GetMapping("/name/{name}")
    public ResponseEntity<List<User>> getUsersByName(@PathVariable String name) {
        return ResponseEntity.ok(userService.findByNameContainingIgnoreCase(name));
    }

    //  Cinsiyete göre kullanıcı listeleme
    @GetMapping("/gender/{gender}")
    public ResponseEntity<List<User>> getUsersByGender(@PathVariable User.Gender gender) {
        return ResponseEntity.ok(userService.findByGender(gender));
    }

    //  Kan grubuna göre hasta listeleme
    @GetMapping("/blood-type/{bloodType}")
    public ResponseEntity<List<User>> getUsersByBloodType(@PathVariable User.Bloodtype bloodType) {
        return ResponseEntity.ok(userService.findByBloodType(bloodType));
    }

    //  Uzmanlık alanına göre doktorları listeleme
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<User>> getUsersBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(userService.findBySpecializationContainingIgnoreCase(specialization));
    }

    //  Kullanıcı silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    //  Kullanıcı güncelleme (Admin)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }

    //  Tüm kullanıcıları listele
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //  Mevcut giriş yapan kullanıcıyı getir
    @GetMapping("/profile/me")
    public ResponseEntity<User> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    //  Mevcut giriş yapan kullanıcı kendi profilini günceller
    @PutMapping("/profile/me")
    public ResponseEntity<User> updateCurrentUserProfile(@RequestBody User updatedUser) {
        User currentUser = userService.getCurrentUser();
        updatedUser.setId(currentUser.getId());
        return ResponseEntity.ok(userService.updateUser(currentUser.getId(), updatedUser));
    }

    //  Şifre değiştirme
    @PostMapping("/profile/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        userService.changePassword(request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(Collections.singletonMap("message", "Şifre başarıyla güncellendi."));
    }
}



