package com.hastanerandevu.app.dto.Analytics;
import com.hastanerandevu.app.model.Appointments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Randevuların statülerine (AKTIF, IPTAL_EDILDI, GEC_KALINDI) göre sayıları.
public class AppointmentStatusCountDTO {
    private Appointments.Status status;
    private long count;
}
