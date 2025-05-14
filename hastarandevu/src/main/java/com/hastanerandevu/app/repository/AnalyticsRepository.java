package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.dto.*;

import java.util.List;


public interface AnalyticsRepository {
    // 1. Klinik Bazlı Randevu Yoğunluğu
    List<ClinicAppointmentCountDTO> getAppointmentCountByClinic();

    // 2. Günlük / Aylık Randevu Dağılımı
    List<DateAppointmentCountDTO> getAppointmentCountByDate();

    // 3. Duruma Göre Randevu Dağılımı
    List<AppointmentStatusCountDTO> getAppointmentCountByStatus();

    // 4. Doktor Bazlı Randevu Sayısı
    List<DoctorAppointmentCountDTO> getAppointmentCountByDoctor();

    // 5. Aylık Yeni Kayıt Sayısı
    List<MonthlyUserRegistrationDTO> getMonthlyUserRegistration();

    // 6. Şikayet Durumu Dağılımı
    List<ComplaintStatusCountDTO> getComplaintCountByStatus();

    // 7. Kliniklere Göre Şikayet Dağılımı
    List<ClinicComplaintCountDTO> getComplaintCountByClinic();

    // 8. Saat Dilimlerine Göre Randevu Dağılımı
    List<TimeSlotAppointmentCountDTO> getAppointmentCountByTimeSlot();
}
