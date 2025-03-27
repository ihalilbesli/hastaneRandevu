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
    //  Reçete oluştur (sadece doktor)
    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription){
        return ResponseEntity.ok(prescriptionService.createPrescription(prescription));
    }
    //  Tüm reçeteleri getir (admin kullanabilir)
    @GetMapping
    public ResponseEntity<List<Prescription>>getAll(){
        return ResponseEntity.ok(prescriptionService.getAllPrescriptions());
    }
    //  Reçeteyi ID ile getir
    @GetMapping("/{id}")
    public ResponseEntity<Prescription>getById(@PathVariable Long id){
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }
    //  Hastaya ait reçeteleri getir
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Prescription>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId));
    }

    //  Doktorun yazdığı reçeteleri getir
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctorId(doctorId));
    }
    //  Hasta - zaman filtresi
    @GetMapping("/patient/{patientId}/filter")
    public ResponseEntity<List<Prescription>> getByPatientAndPeriod(@PathVariable Long patientId,
                                                                    @RequestParam String period) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientIdAndPeriod(patientId, period));
    }

    //  Doktor - zaman filtresi
    @GetMapping("/doctor/{doctorId}/filter")
    public ResponseEntity<List<Prescription>> getByDoctorAndPeriod(@PathVariable Long doctorId,
                                                                   @RequestParam String period) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByDoctorIdAndPeriod(doctorId, period));
    }
    //  Aciklama icindeki kelimeyle reçete arama
    @GetMapping("/search")
    public ResponseEntity<List<Prescription>> searchByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(prescriptionService.searchPrescriptionsByKeyword(keyword));
    }

    //  Güncelleme
    @PutMapping("/{id}")
    public ResponseEntity<Prescription> update(@PathVariable Long id,
                                               @RequestBody Prescription updated) {
        return ResponseEntity.ok(prescriptionService.updatePrescription(id, updated));
    }

    //  Silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

}
