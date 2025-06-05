package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.repository.AppointmentRepository;
import com.hastanerandevu.app.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/hastarandevu/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentService appointmentService, AppointmentRepository appointmentRepository) {
        this.appointmentService = appointmentService;
        this.appointmentRepository = appointmentRepository;
    }

    //  Randevu oluştur
    @PostMapping
    public ResponseEntity<Appointments> createAppointment(@RequestBody Appointments appointments) {
        if (appointments.getPatient() == null || appointments.getDoctor() == null || appointments.getDate() == null || appointments.getTime() == null) {
            throw new RuntimeException("Hasta, doktor, tarih ve saat alanları boş olamaz.");
        }
        Appointments saved = appointmentService.createAppointment(appointments);
        return ResponseEntity.ok(saved);
    }

    //  Tüm randevuları getir
    @GetMapping
    public ResponseEntity<List<Appointments>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    //  ID ile randevu getir
    @GetMapping("/{id}")
    public ResponseEntity<Appointments> getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("ID ile randevu bulunamadı: " + id));
    }

    // Doktorun randevuları
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointments>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmensByDoctorId(doctorId));
    }

    // Belirli tarih ve saate göre doktor uygun mu
    @GetMapping("/available")
    public ResponseEntity<Boolean> checkDoctorAvailability(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam String time) {

        if (doctorId == null || date.isBlank() || time.isBlank()) {
            throw new RuntimeException("Doktor, tarih ve saat zorunludur.");
        }

        boolean available = appointmentService.isDoctorAvailable(
                doctorId, LocalDate.parse(date), LocalTime.parse(time)
        );
        return ResponseEntity.ok(available);
    }

    //  Randevu sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable long id) {
        appointmentService.deteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    //  Doktorun belirli tarihli randevuları
    @GetMapping("/doctor/{id}/date")
    public ResponseEntity<List<Appointments>> getAppointmentsByDoctorAndDate(
            @PathVariable Long id,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorIdAndDate(id, localDate));
    }

    //  Hastaya ait randevular
    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Appointments>> getAppointmentsByPatient(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointemnrsByPatientId(id));
    }

    //  Randevu iptal
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id) {
        Appointments appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı."));

        if (appointment.getStatus() == Appointments.Status.AKTIF) {
            appointment.setStatus(Appointments.Status.IPTAL_EDILDI);
            appointmentRepository.save(appointment);
            return ResponseEntity.ok(Map.of("message", "Randevu iptal edildi."));
        } else {
            throw new RuntimeException("Randevu zaten iptal edilmiş.");
        }
    }

    //  Durum güncelleme
    @PutMapping("/{id}/status")
    public ResponseEntity<Appointments> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam("status") Appointments.Status status,
            @RequestParam(value = "note", required = false) String note) {

        return ResponseEntity.ok(appointmentService.updateStatus(id, status, note));
    }

    //  Zaman aralığına göre filtreleme
    @GetMapping("/filter")
    public ResponseEntity<List<Appointments>> getAllAppointmentsByPeriod(@RequestParam String period) {
        return ResponseEntity.ok(appointmentService.getAllAppointmentsByPeriod(period));
    }

    //  Açıklamaya göre arama
    @GetMapping("/search")
    public ResponseEntity<List<Appointments>> searchAppointmentsByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(appointmentService.searchAppointmentsByKeyword(keyword));
    }

    //  Belirli durumdaki randevuların sayısı
    @GetMapping("/count")
    public ResponseEntity<Long> countAppointmentsByStatus(@RequestParam Appointments.Status status) {
        return ResponseEntity.ok(appointmentService.countAppointmentsByStatus(status));
    }
}
