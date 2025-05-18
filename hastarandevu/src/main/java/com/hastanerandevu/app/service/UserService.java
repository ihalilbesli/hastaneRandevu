package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    // Yeni kullanıcı kaydı
    User registerUser(User user);

    // Email ile kullanıcı bulma
    Optional<User> findByEmail(String email);

    // Email içinde belirli bir kelime geçen kullanıcıları getir
    List<User> findByEmailContainingIgnoreCase(String emailPart);

    // Belirli bir rolü olan kullanıcıları getir (Hasta, Doktor, Admin)
    List<User> findUsersByRole(User.Role role);

    // ID ile kullanıcıyı getir
    Optional<User> getUserById(Long id);

    // Telefon numarasına göre kullanıcı bul
    Optional<User> findByPhoneNumber(String phoneNumber);

    // İsme göre kullanıcı arama
    List<User> findByNameContainingIgnoreCase(String name);

    // Cinsiyete göre kullanıcı listeleme
    List<User> findByGender(User.Gender gender);

    // Kan grubuna göre hasta listeleme
    List<User> findByBloodType(User.Bloodtype bloodType);

    // Uzmanlık alanına göre doktorları listeleme
    List<User> findBySpecializationContainingIgnoreCase(String specialization);

    void deleteUserById(Long id);
    User updateUser(Long id, User updatedUser);

    List<User> getAllUsers();

}
