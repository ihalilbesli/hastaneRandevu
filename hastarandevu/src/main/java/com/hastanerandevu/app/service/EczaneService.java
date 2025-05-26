package com.hastanerandevu.app.service;

import com.hastanerandevu.app.dto.Ezcane.EczaneDto;

import java.util.List;

public interface EczaneService {
    List<EczaneDto> getPharmacies(String city, String district);
}
