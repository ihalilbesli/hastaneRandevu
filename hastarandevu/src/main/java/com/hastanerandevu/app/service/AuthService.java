package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.User;

public interface AuthService {
    //Kullanici kayit islemi
    User register(User user);

    // Kullanıcı giriş işlemi
    User loginUser(String email,String password);

    // Şifre hashleme işlemi
    String encodePassword(String password);

    boolean validateToken(String token);

    void resetPassword(String email, String name, String surname, String birthDate, String newPassword);

}
