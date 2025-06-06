package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.service.ClinicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hastarandevu/clinics")
public class ClinicController {
    private final ClinicService clinicService;

    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    //  Klinik oluştur (Sadece ADMIN)
    @PostMapping
    public ResponseEntity<Clinic> createClinic(@RequestBody Clinic clinic) {
        if (clinic.getName() == null || clinic.getName().trim().isEmpty()) {
            throw new RuntimeException("Klinik adı boş olamaz.");
        }
        return ResponseEntity.ok(clinicService.createClinic(clinic));
    }

    //  Tüm klinikleri getir (ADMIN + DOKTOR)
    @GetMapping
    public ResponseEntity<List<Clinic>> getAllClinics() {
        return ResponseEntity.ok(clinicService.getAllClinics());
    }

    // Klinik ID ile getir (Sadece ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable Long id) {
        return clinicService.getClinicById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Klinik bulunamadı: ID = " + id));
    }

    //  Klinik güncelle (Sadece ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<Clinic> updateClinic(@PathVariable Long id, @RequestBody Clinic updatedClinic) {
        if (updatedClinic.getName() == null || updatedClinic.getName().trim().isEmpty()) {
            throw new RuntimeException("Güncellenecek klinik adı boş olamaz.");
        }
        return ResponseEntity.ok(clinicService.updateClinic(id, updatedClinic));
    }

    //  Klinik pasifleştir (Sadece ADMIN)
    @PutMapping("/{id}/passive")
    public ResponseEntity<Void> deactivateClinic(@PathVariable Long id) {
        clinicService.deactivateClinic(id);
        return ResponseEntity.noContent().build();
    }

    //  Klinik aktifleştir (Sadece ADMIN)
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateClinic(@PathVariable Long id) {
        clinicService.activateClinic(id);
        return ResponseEntity.noContent().build();
    }

    //  Belirli kliniğe ait doktorları getir
    @GetMapping("/{id}/doctors")
    public ResponseEntity<List<?>> getDoctorsByClinic(@PathVariable Long id) {
        List<?> doctors = clinicService.getDoctorsByClinicId(id);
        if (doctors == null || doctors.isEmpty()) {
            throw new RuntimeException("Bu kliniğe ait doktor bulunamadı. Klinik ID: " + id);
        }
        return ResponseEntity.ok(doctors);
    }
}
