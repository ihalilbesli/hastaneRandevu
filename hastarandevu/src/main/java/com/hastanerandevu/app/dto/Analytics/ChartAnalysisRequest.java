package com.hastanerandevu.app.dto.Analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartAnalysisRequest {
    private String chartTitle;
    private List<String> labels;
    private List<Long> values;
}
