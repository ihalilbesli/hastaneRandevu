package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.UserService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(currentUser.getEmail(), email) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Bu kullanıcı bilgilerine erişim yetkiniz yok.");
        }
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findByEmailContainingIgnoreCase(String emailPart) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin email araması yapabilir.");
        }
        return userRepository.findByEmailContainingIgnoreCase(emailPart);
    }

    @Override
    public List<User> findUsersByRole(User.Role role) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin rol filtrelemesi yapabilir.");
        }
        return userRepository.findByRole(role);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (!Objects.equals(currentUser.getId(), id) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi bilgilerinizi görüntüleyebilirsiniz.");
        }

        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isEmpty()) return user;

        if (!Objects.equals(user.get().getEmail(), currentUser.getEmail()) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Bu bilgiye erişim izniniz yok.");
        }
        return user;
    }

    @Override
    public List<User> findByNameContainingIgnoreCase(String name) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin isme göre arama yapabilir.");
        }
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<User> findByGender(User.Gender gender) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin cinsiyete göre filtreleme yapabilir.");
        }
        return userRepository.findByGender(gender);
    }

    @Override
    public List<User> findByBloodType(User.Bloodtype bloodType) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin kan grubuna göre filtreleme yapabilir.");
        }
        return userRepository.findByBloodType(bloodType);
    }

    @Override
    public List<User> findBySpecializationContainingIgnoreCase(String specialization) {
        return userRepository.findBySpecializationContainingIgnoreCase(specialization);
    }
}
