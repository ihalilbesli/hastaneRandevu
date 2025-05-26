package com.hastanerandevu.app.dto.Ezcane;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EczaneDto {
    private String name;
    private String dist;
    private String address;
    private String phone;
    private String loc;
}