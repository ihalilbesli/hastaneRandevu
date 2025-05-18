package com.hastanerandevu.app.dto.Analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Cliniklere gore sikayet sayisi
public class ClinicComplaintCountDTO {
    private String clinicName;
    private Long complaintCount;
}
