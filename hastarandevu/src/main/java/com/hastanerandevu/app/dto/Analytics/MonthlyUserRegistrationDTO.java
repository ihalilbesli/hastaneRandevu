package com.hastanerandevu.app.dto.Analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Hasta ve doktor rollerinde yeni kullanıcı sayisi
public class MonthlyUserRegistrationDTO {
    private String month;      // "2025-05" gibi format
    private String role;       // HASTA, DOKTOR
    private long count;
}