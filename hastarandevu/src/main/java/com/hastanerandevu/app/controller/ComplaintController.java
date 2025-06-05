package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.Complaint;
import com.hastanerandevu.app.service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hastarandevu/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Belirli kullanıcıya ait şikayetler
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Complaint>> getComplaintByUserId(@PathVariable Long id) {
        List<Complaint> complaints = complaintService.getComplaintByUserId(id);
        if (complaints == null || complaints.isEmpty()) {
            throw new RuntimeException("Kullanıcıya ait şikayet bulunamadı. ID: " + id);
        }
        return ResponseEntity.ok(complaints);
    }

    // Belirli kullanıcı ve tarih aralığına göre filtreleme
    @GetMapping("/filter")
    public ResponseEntity<List<Complaint>> getComplaintByDate(
            @RequestParam Long userId,
            @RequestParam String period) {
        if (userId == null || period == null || period.trim().isEmpty()) {
            throw new RuntimeException("Kullanıcı ID ve zaman aralığı boş olamaz.");
        }
        List<Complaint> complaints = complaintService.getComplaintByUserIdAndDate(userId, period);
        if (complaints == null || complaints.isEmpty()) {
            throw new RuntimeException("Belirtilen tarihte şikayet bulunamadı.");
        }
        return ResponseEntity.ok(complaints);
    }

    // Tüm şikayetleri getir
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaint() {
        List<Complaint> complaints = complaintService.getAllComplaint();
        if (complaints == null || complaints.isEmpty()) {
            throw new RuntimeException("Hiçbir şikayet kaydı bulunamadı.");
        }
        return ResponseEntity.ok(complaints);
    }

    // Yeni şikayet oluştur
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@RequestBody Complaint complaint) {
        if (complaint.getContent() == null || complaint.getContent().trim().isEmpty()) {
            throw new RuntimeException("Şikayet içeriği boş olamaz.");
        }

        return ResponseEntity.ok(complaintService.createComplaint(complaint));
    }

    // Şikayet silme
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCeomplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }

    // Statüye göre şikayet listele
    @GetMapping("/status")
    public ResponseEntity<List<Complaint>> getComplaintsByStatus(@RequestParam Complaint.Status status) {
        List<Complaint> complaints = complaintService.getComplaintByStatus(status);
        if (complaints == null || complaints.isEmpty()) {
            throw new RuntimeException("Belirtilen statüye sahip şikayet bulunamadı: " + status);
        }
        return ResponseEntity.ok(complaints);
    }

    // Şikayet güncelleme
    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id, @RequestBody Complaint updatedComplaint) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, updatedComplaint));
    }
}
