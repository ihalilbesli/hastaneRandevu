package com.hastanerandevu.app.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Her doktorun aldığı toplam randevu sayısını görmek
public class DoctorAppointmentCountDTO {
    private String doctorName;
    private long appointmentCount;
}
