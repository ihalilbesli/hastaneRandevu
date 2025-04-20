package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AuthService;
import com.hastanerandevu.app.util.JwtUtil;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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


    @Override
    public User register(User user) {
        try {
            User currentUser = SecurityUtil.getCurrentUser(userRepository);

            // Eğer giriş yapılmışsa ve kullanıcı ADMIN değilse hata fırlat
            if (currentUser.getRole() != User.Role.ADMIN) {
                throw new RuntimeException("Sadece admin başka rol ile kullanıcı oluşturabilir.");
            }
        } catch (Exception e) {
            // Giriş yapılmamışsa (anonymous user), rolü otomatik olarak HASTA yap
            user.setRole(User.Role.HASTA);
        }

        user.setPassword(encodePassword(user.getPassword()));
        return userRepository.save(user);
    }


    // Kullanıcı giriş işlemi
    @Override
    public User loginUser(String email, String password) {
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
        return user;

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
