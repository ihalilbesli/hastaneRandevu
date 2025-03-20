package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.User;

public interface AuthService {
    //Kullanici kayit islemi
    User register(User user);

    // Kullanıcı giriş işlemi
    String login(String email,String password);

    // Şifre hashleme işlemi
    String encodePassword(String password);

    boolean validateToken(String token);
}
