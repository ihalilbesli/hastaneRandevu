package com.hastanerandevu.app.dto.Auth;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String name;
    private String surname;
    private String birthDate; // "yyyy-MM-dd" formatında gelecek
    private String newPassword;
}
