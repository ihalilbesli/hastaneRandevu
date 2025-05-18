package com.hastanerandevu.app.dto.Analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Belirli bir zaman periyoduna göre (günlük, haftalık, aylık) randevu sayısı
public class DateAppointmentCountDTO {
    private LocalDate date;
    private long appointmentCount;
}
