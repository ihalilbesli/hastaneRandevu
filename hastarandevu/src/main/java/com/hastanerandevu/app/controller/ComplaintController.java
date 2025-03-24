package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.Complaint;
import com.hastanerandevu.app.service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
