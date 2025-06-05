package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.service.ExportService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/hastarandevu/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/users")
    public ResponseEntity<Resource> exportUsers() {
        Resource file = exportService.exportUsers().getBody();
        if (isEmpty(file)) {
            throw new RuntimeException("Kullanıcı verileri dışa aktarılamadı (boş içerik).");
        }
        return exportService.exportUsers();
    }

    @GetMapping("/appointments")
    public ResponseEntity<Resource> exportAppointments() {
        Resource file = exportService.exportAppointments().getBody();
        if (isEmpty(file)) {
            throw new RuntimeException("Randevu verileri dışa aktarılamadı (boş içerik).");
        }
        return exportService.exportAppointments();
    }

    @GetMapping("/complaints")
    public ResponseEntity<Resource> exportComplaints() {
        Resource file = exportService.exportComplaints().getBody();
        if (isEmpty(file)) {
            throw new RuntimeException("Şikayet verileri dışa aktarılamadı (boş içerik).");
        }
        return exportService.exportComplaints();
    }

    @GetMapping("/test-results")
    public ResponseEntity<Resource> exportTestResults() {
        Resource file = exportService.exportTestResults().getBody();
        if (isEmpty(file)) {
            throw new RuntimeException("Test sonuçları dışa aktarılamadı (boş içerik).");
        }
        return exportService.exportTestResults();
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<Resource> exportPrescriptions() {
        Resource file = exportService.exportPrescriptions().getBody();
        if (isEmpty(file)) {
            throw new RuntimeException("Reçete verileri dışa aktarılamadı (boş içerik).");
        }
        return exportService.exportPrescriptions();
    }

    @GetMapping("/patient-histories")
    public ResponseEntity<Resource> exportPatientHistories() {
        Resource file = exportService.exportPatientHistories().getBody();
        if (isEmpty(file)) {
            throw new RuntimeException("Hasta geçmişi verileri dışa aktarılamadı (boş içerik).");
        }
        return exportService.exportPatientHistories();
    }

    @GetMapping("/patient-reports")
    public ResponseEntity<Resource> exportPatientReports() {
        Resource file = exportService.exportPatientReports().getBody();
        if (isEmpty(file)) {
            throw new RuntimeException("Hasta raporu verileri dışa aktarılamadı (boş içerik).");
        }
        return exportService.exportPatientReports();
    }

    // Ortak kontrol metodu
    private boolean isEmpty(Resource file) {
        try {
            return file == null || file.contentLength() == 0;
        } catch (IOException e) {
            throw new RuntimeException("Dosya uzunluğu kontrol edilirken hata oluştu.", e);
        }
    }
}
