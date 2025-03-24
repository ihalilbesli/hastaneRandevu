package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.Complaint;

import java.util.List;

public interface ComplaintService {
    // Yeni şikayet oluştur
    Complaint createComplaint(Complaint complaint);

    // Kullanıcının tüm şikayetlerini getir
    List<Complaint> getComplaintByUserId(Long id);

    // Kullanıcının belirli zaman aralığındaki şikayetleri (week/month/year)
    List<Complaint>getComplaintByUserIdAndDate(Long userId,String period);

    // Şikayeti sil
    void deleteComplaint(Long id);
}
