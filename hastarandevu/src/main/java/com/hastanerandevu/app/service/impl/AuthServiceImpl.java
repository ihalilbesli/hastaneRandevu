package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AuthService;
import com.hastanerandevu.app.util.JwtUtil;
import com.hastanerandevu.app.util.SecurityUtil;
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

        // Klinik kontrolü: sadece doktorlar için zorunlu
        if (user.getRole() == User.Role.DOKTOR && user.getClinic() == null) {
            throw new RuntimeException("Doktor oluşturulurken klinik seçilmelidir.");
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
    @Override
    public void resetPassword(String email, String name, String surname, String birthDate, String newPassword) {
        String trimmedDate = birthDate.length() > 10 ? birthDate.substring(0, 10) : birthDate;
        System.out.println("✂️ Doğum Tarihi (trimmed): " + trimmedDate);

        // Sorguda artık String kullanıyoruz
        Optional<User> optionalUser = userRepository.findByEmailAndNameAndSurnameAndBirthDate(email, name, surname, trimmedDate);

        if (optionalUser.isEmpty()) {
            System.out.println("❌ Kullanıcı bulunamadı veya bilgiler eşleşmiyor.");
            System.out.println("❗ Aranan kriterler: email=" + email + ", ad=" + name + ", soyad=" + surname + ", doğumTarihi=" + trimmedDate);
            throw new RuntimeException("Bilgiler uyuşmuyor veya kullanıcı bulunamadı.");
        }

        User user = optionalUser.get();
        System.out.println("✅ Kullanıcı bulundu: ID=" + user.getId() + ", Email=" + user.getEmail());

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        System.out.println("🔒 Şifre başarıyla güncellendi ve veritabanına kaydedildi.");
    }


}
