package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.service.ExportService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hastarandevu/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    // Kullanıcıları dışa aktar
    @GetMapping("/users")
    public ResponseEntity<Resource> exportUsers() {
        return exportService.exportUsers();
    }

    // Randevuları dışa aktar
    @GetMapping("/appointments")
    public ResponseEntity<Resource> exportAppointments() {
        return exportService.exportAppointments();
    }

    // Şikayetleri dışa aktar
    @GetMapping("/complaints")
    public ResponseEntity<Resource> exportComplaints() {
        return exportService.exportComplaints();
    }

    // Test sonuçlarını dışa aktar
    @GetMapping("/test-results")
    public ResponseEntity<Resource> exportTestResults() {
        return exportService.exportTestResults();
    }

    // Reçeteleri dışa aktar
    @GetMapping("/prescriptions")
    public ResponseEntity<Resource> exportPrescriptions() {
        return exportService.exportPrescriptions();
    }

    // Hasta geçmişlerini dışa aktar
    @GetMapping("/patient-histories")
    public ResponseEntity<Resource> exportPatientHistories() {
        return exportService.exportPatientHistories();
    }

    // Hasta raporlarını dışa aktar
    @GetMapping("/patient-reports")
    public ResponseEntity<Resource> exportPatientReports() {
        return exportService.exportPatientReports();
    }
}
