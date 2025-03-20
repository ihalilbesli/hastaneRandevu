package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AuthService;
import com.hastanerandevu.app.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }


    //Kullanici kayit islemi
    @Override
    public User register(User user) {
        user.setPassword(encodePassword(user.getPassword()));
        return userRepository.save(user);
    }

    // Kullanıcı giriş işlemi
    @Override
    public String login(String email, String password) {
        Optional<User> userOptional=userRepository.findByEmail(email);
        //email kontrolu
        if (userOptional.isEmpty()){
            throw new RuntimeException("Gecersiz email veya sifre");
        }
        User user=userOptional.get();
        // Şifreyi kontrolu
        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new RuntimeException("Gecersiz email veya sifre");
        }
        // JWT token oluştur ve döndür
        return jwtUtil.generateToken(user);

    }

    // Şifre hashleme
    @Override
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    // Token doğrulama işlemi
    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}
