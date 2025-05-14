package com.hastanerandevu.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Hangi saat aralıklarında randevular daha yoğun
public class TimeSlotAppointmentCountDTO {
    private String timeSlot;  // "09:00", "14:00" gibi
    private long appointmentCount;
}
