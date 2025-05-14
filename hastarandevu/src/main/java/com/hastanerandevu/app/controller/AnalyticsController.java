package com.hastanerandevu.app.controller;
import com.hastanerandevu.app.dto.*;
import com.hastanerandevu.app.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("hastarandevu/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    // 1. Klinik bazlı randevu sayısı
    @GetMapping("/appointments/clinic")
    public ResponseEntity<List<ClinicAppointmentCountDTO>> getAppointmentCountByClinic() {
        return ResponseEntity.ok(analyticsService.getAppointmentCountByClinic());
    }

    // 2. Tarihe göre randevu sayısı (günlük/haftalık/aylık gibi frontend tarafı yönetebilir)
    @GetMapping("/appointments/date")
    public ResponseEntity<List<DateAppointmentCountDTO>> getAppointmentCountByDate() {
        return ResponseEntity.ok(analyticsService.getAppointmentCountByDate());
    }

    // 3. Randevu durumuna göre dağılım
    @GetMapping("/appointments/status")
    public ResponseEntity<List<AppointmentStatusCountDTO>> getAppointmentCountByStatus() {
        return ResponseEntity.ok(analyticsService.getAppointmentCountByStatus());
    }

    // 4. Doktor bazlı randevu sayısı
    @GetMapping("/appointments/doctor")
    public ResponseEntity<List<DoctorAppointmentCountDTO>> getAppointmentCountByDoctor() {
        return ResponseEntity.ok(analyticsService.getAppointmentCountByDoctor());
    }

    // 5. Aylık yeni kullanıcı kaydı
    @GetMapping("/users/monthly")
    public ResponseEntity<List<MonthlyUserRegistrationDTO>> getMonthlyUserRegistration() {
        return ResponseEntity.ok(analyticsService.getMonthlyUserRegistration());
    }

    // 6. Şikayet durumlarına göre sayılar
    @GetMapping("/complaints/status")
    public ResponseEntity<List<ComplaintStatusCountDTO>> getComplaintCountByStatus() {
        return ResponseEntity.ok(analyticsService.getComplaintCountByStatus());
    }

    // 7. Kliniklere göre şikayet sayısı
    @GetMapping("/complaints/clinic")
    public ResponseEntity<List<ClinicComplaintCountDTO>> getComplaintCountByClinic() {
        return ResponseEntity.ok(analyticsService.getComplaintCountByClinic());
    }

    // 8. Randevu zaman aralığına göre yoğunluk (örneğin sabah/öğle/akşam)
    @GetMapping("/appointments/time-slot")
    public ResponseEntity<List<TimeSlotAppointmentCountDTO>> getAppointmentCountByTimeSlot() {
        return ResponseEntity.ok(analyticsService.getAppointmentCountByTimeSlot());
    }
}
