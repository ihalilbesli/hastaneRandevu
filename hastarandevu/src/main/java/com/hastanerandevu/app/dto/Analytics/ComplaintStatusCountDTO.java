package com.hastanerandevu.app.dto.Analytics;

import com.hastanerandevu.app.model.Complaint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Beklemede / İncelemede / Çözüldü durumundaki şikayet sayılar
public class ComplaintStatusCountDTO {
    private Complaint.Status status;
    private long count;
}
