package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.service.DoctorPatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(doctorPatientService.getMyPatients());
    }

    // Doktorun hastaları içinde isimle arama
    @GetMapping("/my-patients/search-by-name")
    public ResponseEntity<List<User>> searchMyPatientsByName(@RequestParam String name) {
        return ResponseEntity.ok(doctorPatientService.searchMyPatientsByName(name));
    }

    // Doktorun hastaları içinde email ile arama
    @GetMapping("/my-patients/search-by-email")
    public ResponseEntity<List<User>> searchMyPatientsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(doctorPatientService.searchMyPatientsByEmail(email));
    }
}
