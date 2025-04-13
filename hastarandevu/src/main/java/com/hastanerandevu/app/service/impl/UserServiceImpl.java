package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.UserService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Yeni kullanıcı kaydeder. (Genel erişim)
     */
    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Email'e göre kullanıcıyı döner. (Sadece admin veya kendisi erişebilir)
     */
    @Override
    public Optional<User> findByEmail(String email) {
        String currentEmail = SecurityUtil.getCurrentUserId();
        if (!currentEmail.equals(email) && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu kullanıcı bilgilerine erişim yetkiniz yok.");
        }
        return userRepository.findByEmail(email);
    }

    /**
     * Email'e göre arama yapar (Admin)
     */
    @Override
    public List<User> findByEmailContainingIgnoreCase(String emailPart) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin email araması yapabilir.");
        }
        return userRepository.findByEmailContainingIgnoreCase(emailPart);
    }

    /**
     * Belirli role sahip tüm kullanıcıları listeler. (Admin)
     */
    @Override
    public List<User> findUsersByRole(User.Role role) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin rol filtrelemesi yapabilir.");
        }
        return userRepository.findByRole(role);
    }

    /**
     * ID'ye göre kullanıcı getirir. (Kendi bilgisi veya admin)
     */
    @Override
    public Optional<User> getUserById(Long id) {
        String email = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId()!=(id) && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi bilgilerinizi görüntüleyebilirsiniz.");
        }

        return userRepository.findById(id);
    }

    /**
     * Telefon numarasına göre kullanıcıyı bulur. (Admin veya kullanıcı kendisi)
     */
    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String email = SecurityUtil.getCurrentUserId();
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        if (user.isEmpty()) return user;

        if (!user.get().getEmail().equals(email) && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu bilgiye erişim izniniz yok.");
        }
        return user;
    }

    /**
     * İsme göre kullanıcıları arar. (Admin)
     */
    @Override
    public List<User> findByNameContainingIgnoreCase(String name) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin isme göre arama yapabilir.");
        }
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Cinsiyete göre kullanıcıları listeler. (Admin)
     */
    @Override
    public List<User> findByGender(User.Gender gender) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin cinsiyete göre filtreleme yapabilir.");
        }
        return userRepository.findByGender(gender);
    }

    /**
     * Kan grubuna göre kullanıcıları listeler. (Admin)
     */
    @Override
    public List<User> findByBloodType(User.Bloodtype bloodType) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin kan grubuna göre filtreleme yapabilir.");
        }
        return userRepository.findByBloodType(bloodType);
    }


     //Uzmanlık alanına göre doktorları arar. (Admin ve Doktor)

    @Override
    public List<User> findBySpecializationContainingIgnoreCase(String specialization) {

        return userRepository.findBySpecializationContainingIgnoreCase(specialization);
    }
}
