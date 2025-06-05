package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.service.DoctorPatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hastarandevu/doctorPatients")
public class DoctorPatientController {

    private final DoctorPatientService doctorPatientService;

    public DoctorPatientController(DoctorPatientService doctorPatientService) {
        this.doctorPatientService = doctorPatientService;
    }

    // Doktorun geçmişte işlem yaptığı tüm hastaları getirir
    @GetMapping("/my-patients")
    public ResponseEntity<List<User>> getMyPatients() {
        List<User> patients = doctorPatientService.getMyPatients();
        if (patients == null || patients.isEmpty()) {
            throw new RuntimeException("İşlem yapılmış hasta bulunamadı.");
        }
        return ResponseEntity.ok(patients);
    }

    // Doktorun hastaları içinde isimle arama
    @GetMapping("/my-patients/search-by-name")
    public ResponseEntity<List<User>> searchMyPatientsByName(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("İsim bilgisi boş olamaz.");
        }

        List<User> results = doctorPatientService.searchMyPatientsByName(name);
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("Verilen isimle hasta bulunamadı: " + name);
        }

        return ResponseEntity.ok(results);
    }

    // Doktorun hastaları içinde email ile arama
    @GetMapping("/my-patients/search-by-email")
    public ResponseEntity<List<User>> searchMyPatientsByEmail(@RequestParam String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email bilgisi boş olamaz.");
        }

        List<User> results = doctorPatientService.searchMyPatientsByEmail(email);
        if (results == null || results.isEmpty()) {
            throw new RuntimeException("Verilen email ile hasta bulunamadı: " + email);
        }

        return ResponseEntity.ok(results);
    }

    // Bugünkü hastalar (sadece isim bilgisi)
    @GetMapping("/my-patients-today")
    public ResponseEntity<List<User>> getMyPatientsToday() {
        List<User> todayPatients = doctorPatientService.getMyPatientsToday();
        if (todayPatients == null || todayPatients.isEmpty()) {
            throw new RuntimeException("Bugün için atanmış hasta bulunamadı.");
        }
        return ResponseEntity.ok(todayPatients);
    }

    // Bugünkü randevuların detaylı bilgisi
    @GetMapping("/my-patients-today-full")
    public ResponseEntity<List<Appointments>> getTodayAppointmentsFull() {
        List<Appointments> appointments = doctorPatientService.getTodayAppointmentsWithPatientInfo();
        if (appointments == null || appointments.isEmpty()) {
            throw new RuntimeException("Bugünkü randevu bilgisi bulunamadı.");
        }
        return ResponseEntity.ok(appointments);
    }
}
