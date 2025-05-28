package com.hastanerandevu.app.dto.PasswordChange;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}