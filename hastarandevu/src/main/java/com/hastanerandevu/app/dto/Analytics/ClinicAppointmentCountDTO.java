package com.hastanerandevu.app.dto.Analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Her kliniğe ait toplam randevu sayısı
public class ClinicAppointmentCountDTO {
    private String clinicName;
    private long appointmentCount;

}
