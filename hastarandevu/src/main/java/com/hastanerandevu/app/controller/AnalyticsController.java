package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.dto.Analytics.*;
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

    @GetMapping("/appointments/clinic")
    public ResponseEntity<List<ClinicAppointmentCountDTO>> getAppointmentCountByClinic() {
        List<ClinicAppointmentCountDTO> data = analyticsService.getAppointmentCountByClinic();
        if (data.isEmpty()) throw new RuntimeException("Klinik bazlı randevu verisi bulunamadı.");
        return ResponseEntity.ok(data);
    }

    @GetMapping("/appointments/date")
    public ResponseEntity<List<DateAppointmentCountDTO>> getAppointmentCountByDate() {
        List<DateAppointmentCountDTO> data = analyticsService.getAppointmentCountByDate();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/appointments/status")
    public ResponseEntity<List<AppointmentStatusCountDTO>> getAppointmentCountByStatus() {
        return ResponseEntity.ok(analyticsService.getAppointmentCountByStatus());
    }

    @GetMapping("/appointments/doctor")
    public ResponseEntity<List<DoctorAppointmentCountDTO>> getAppointmentCountByDoctor() {
        return ResponseEntity.ok(analyticsService.getAppointmentCountByDoctor());
    }

    @GetMapping("/users/monthly")
    public ResponseEntity<List<MonthlyUserRegistrationDTO>> getMonthlyUserRegistration() {
        return ResponseEntity.ok(analyticsService.getMonthlyUserRegistration());
    }

    @GetMapping("/complaints/status")
    public ResponseEntity<List<ComplaintStatusCountDTO>> getComplaintCountByStatus() {
        return ResponseEntity.ok(analyticsService.getComplaintCountByStatus());
    }

    @GetMapping("/complaints/clinic")
    public ResponseEntity<List<ClinicComplaintCountDTO>> getComplaintCountByClinic() {
        return ResponseEntity.ok(analyticsService.getComplaintCountByClinic());
    }

    @GetMapping("/appointments/time-slot")
    public ResponseEntity<List<TimeSlotAppointmentCountDTO>> getAppointmentCountByTimeSlot() {
        return ResponseEntity.ok(analyticsService.getAppointmentCountByTimeSlot());
    }

    @GetMapping("/users/roles")
    public ResponseEntity<List<UserRoleCountDTO>> getUserCountByRole() {
        return ResponseEntity.ok(analyticsService.getUserCountByRole());
    }

    @GetMapping("/users/genders")
    public ResponseEntity<List<UserGenderCountDTO>> getUserCountByGender() {
        return ResponseEntity.ok(analyticsService.getUserCountByGender());
    }

    @GetMapping("/users/blood-types")
    public ResponseEntity<List<UserBloodTypeCountDTO>> getUserCountByBloodType() {
        return ResponseEntity.ok(analyticsService.getUserCountByBloodType());
    }

    @GetMapping("/clinics/doctor-count")
    public ResponseEntity<List<ClinicDoctorCountDTO>> getDoctorCountByClinic() {
        return ResponseEntity.ok(analyticsService.getDoctorCountByClinic());
    }

    @GetMapping("/complaints/subject")
    public ResponseEntity<List<ComplaintSubjectCountDTO>> getComplaintCountBySubject() {
        return ResponseEntity.ok(analyticsService.getComplaintCountBySubject());
    }
}
