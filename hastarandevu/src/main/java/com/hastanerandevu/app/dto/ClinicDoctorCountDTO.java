package com.hastanerandevu.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicDoctorCountDTO {
    private String clinicName;
    private long count;
}
