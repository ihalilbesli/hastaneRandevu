package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Complaint;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.ComplaintRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.ComplaintService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ComplaintServiceImpl implements ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Complaint createComplaint(Complaint complaint) {
        Long userId=complaint.getUser().getId();
        User user=userRepository.findById(userId).orElseThrow();
        complaint.setUser(user);
        return complaintRepository.save(complaint);
    }

    @Override
    public List<Complaint> getComplaintByUserId(Long id) {
        User user=userRepository.findById(id).orElseThrow();
        return complaintRepository.findByUser(user);
    }

    @Override
    public List<Complaint> getComplaintByUserIdAndDate(Long userId, String period) {
        LocalDate date;
        switch (period.toLowerCase()){
            case "week"->date=LocalDate.now().minusWeeks(1);
            case "month"->date=LocalDate.now().minusMonths(1);
            case "year"->date=LocalDate.now().minusYears(1);
            default -> throw new IllegalArgumentException("Gecersiz Zaman Secimi "+ period);

        }
        User user=userRepository.findById(userId).orElseThrow();
        return complaintRepository.findByUserAndCreatedAtAfter(user,date);
    }

    @Override
    public List<Complaint> getAllComplaint() {
        return complaintRepository.findAll();
    }

    @Override
    public List<Complaint> getComplaintByStatus(Complaint.Status status) {
        return complaintRepository.findByStatus(status);
    }

    @Override
    public Complaint updateComplaint(Long id, Complaint updatedComplaint) {
        Complaint complaint=complaintRepository.findById(id).orElseThrow(()->new RuntimeException("Sikayet Bulunamadi: "+id));

        complaint.setContent(updatedComplaint.getContent());
        complaint.setStatus(updatedComplaint.getStatus());

        return  complaintRepository.save(complaint);
    }

    @Override
    public void deleteComplaint(Long id) {

    }
}
