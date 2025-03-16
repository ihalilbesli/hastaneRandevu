package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    // Email ile eşleşen kullanıcıyı bul
    Optional<User> findByEmail(String email);

    // Email içinde geçen tüm kullanıcıları getir
    List<User> findByEmailContainingIgnoreCase(String emailPart);

    // Belirli bir rolü olan tüm kullanıcıları listele (Hasta, Doktor, Admin)
    List<User> findByRole(User.Role role);

    // Telefon numarasıyla kullanıcıyı bul
    Optional<User> findByPhoneNumber(String phoneNumber);

    // İsme göre kullanıcı arama (Admin paneli için)
    List<User> findByNameContainingIgnoreCase(String name);

    // İsme ve soyisme göre kullanıcı arama
    List<User> findByNameContainingIgnoreCaseAndSurnameContainingIgnoreCase(String name, String surname);

    // Cinsiyete göre kullanıcı listeleme
    List<User> findByGender(User.Gender gender);

    // Kan grubuna göre hasta listeleme
    List<User> findByBloodType(User.Bloodtype bloodType);

    // Uzmanlık alanına göre doktorları listeleme
    List<User> findBySpecializationContainingIgnoreCase(String specialization);
}
