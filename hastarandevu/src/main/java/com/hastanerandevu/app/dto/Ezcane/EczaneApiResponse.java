package com.hastanerandevu.app.dto.Ezcane;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EczaneApiResponse {
    private boolean success;
    private List<EczaneDto> result;
}