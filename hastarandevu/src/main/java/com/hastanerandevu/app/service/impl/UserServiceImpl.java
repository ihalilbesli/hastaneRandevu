package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.UserService;
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

    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findByEmailContainingIgnoreCase(String emailPart) {
        return userRepository.findByEmailContainingIgnoreCase(emailPart);
    }

    @Override
    public List<User> findUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public List<User> findByNameContainingIgnoreCase(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }


    @Override
    public List<User> findByGender(User.Gender gender) {
        return userRepository.findByGender(gender);
    }

    @Override
    public List<User> findByBloodType(User.Bloodtype bloodType) {
        return userRepository.findByBloodType(bloodType);
    }

    @Override
    public List<User> findBySpecializationContainingIgnoreCase(String specialization) {
        return userRepository.findBySpecializationContainingIgnoreCase(specialization);
    }
}
