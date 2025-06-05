package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.Prescription;
import com.hastanerandevu.app.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hastarandevu/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        return ResponseEntity.ok(prescriptionService.createPrescription(prescription));
    }

    @GetMapping
    public ResponseEntity<List<Prescription>> getAll() {
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getById(@PathVariable Long id) {
        Prescription result = prescriptionService.getPrescriptionById(id);
        if (result == null) {
            throw new RuntimeException("Belirtilen ID'ye ait reçete bulunamadı: " + id);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Prescription>> getByPatient(@PathVariable Long patientId) {
        List<Prescription> list = prescriptionService.getPrescriptionsByPatientId(patientId);
        if (list.isEmpty()) {
            throw new RuntimeException("Hasta ID'si " + patientId + " için reçete bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getByDoctor(@PathVariable Long doctorId) {
        List<Prescription> list = prescriptionService.getPrescriptionsByDoctorId(doctorId);
        if (list.isEmpty()) {
            throw new RuntimeException("Doktor ID'si " + doctorId + " için reçete bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/patient/{patientId}/filter")
    public ResponseEntity<List<Prescription>> getByPatientAndPeriod(@PathVariable Long patientId,
                                                                    @RequestParam String period) {
        List<Prescription> list = prescriptionService.getPrescriptionsByPatientIdAndPeriod(patientId, period);
        if (list.isEmpty()) {
            throw new RuntimeException("Hasta ID'si " + patientId + " için '" + period + "' süresinde reçete bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/doctor/{doctorId}/filter")
    public ResponseEntity<List<Prescription>> getByDoctorAndPeriod(@PathVariable Long doctorId,
                                                                   @RequestParam String period) {
        List<Prescription> list = prescriptionService.getPrescriptionsByDoctorIdAndPeriod(doctorId, period);
        if (list.isEmpty()) {
            throw new RuntimeException("Doktor ID'si " + doctorId + " için '" + period + "' süresinde reçete bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Prescription>> searchByKeyword(@RequestParam String keyword) {
        List<Prescription> list = prescriptionService.searchPrescriptionsByKeyword(keyword);
        if (list.isEmpty()) {
            throw new RuntimeException("Anahtar kelime '" + keyword + "' ile eşleşen reçete bulunamadı.");
        }
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Prescription> update(@PathVariable Long id,
                                               @RequestBody Prescription updated) {
        Prescription result = prescriptionService.updatePrescription(id, updated);
        if (result == null) {
            throw new RuntimeException("Güncellenecek reçete bulunamadı. ID: " + id);
        }
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
}

