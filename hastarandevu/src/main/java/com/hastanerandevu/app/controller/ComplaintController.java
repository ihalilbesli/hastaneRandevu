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
    //  Kullanıcının son  zaman içindeki şikayetlerini getir
    @GetMapping("/filter")
    public ResponseEntity<List<Complaint>> getComplaintByDate(
            @RequestParam Long userId,
            @RequestParam String period){
        List<Complaint> complaints=complaintService.getComplaintByUserIdAndDate(userId,period);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaint(){
        return ResponseEntity.ok(complaintService.getAllComplaint());
    }

    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@RequestBody Complaint complaint){
        return ResponseEntity.ok(complaintService.createComplaint(complaint));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCeomplaint(@PathVariable Long id){
        complaintService.deleteComplaint(id);
        return ResponseEntity.noContent().build();
    }
    //  Statusune gore göre filtrele
    @GetMapping("/status")
    public ResponseEntity<List<Complaint>> getComplaintsByStatus(@RequestParam Complaint.Status status) {
        return ResponseEntity.ok(complaintService.getComplaintByStatus(status));
    }

    //Sikayeti guncelleme
    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id, @RequestBody Complaint updatedComplaint) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, updatedComplaint));
    }

}
