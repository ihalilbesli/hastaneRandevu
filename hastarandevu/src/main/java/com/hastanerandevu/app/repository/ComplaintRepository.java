package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.Complaint;
import com.hastanerandevu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint,Long> {
    // Belirli bir kullanıcının tüm şikayetlerini getir
    List<Complaint> findByUser(User user);

    // Şikayetleri durumlarına göre listele (BEKLEMEDE, ÇÖZÜLDÜ)
    List<Complaint> findByStatus(Complaint.Status status);

    // Tüm şikayetleri en yeniye göre sırala
    List<Complaint> findAllByOrderByCreatedAtDesc();

    // Şikayet metni içinde belirli bir kelime geçenleri getir (Arama için)
    List<Complaint> findByContentContainingIgnoreCase(String keyword);

    // Kullanıcının son X gün içindeki şikayetlerini getir
    List<Complaint> findByUserAndCreatedAtAfter(User user, LocalDate date); //(Hasta icin)

    List<Complaint> findByCreatedAtAfter(LocalDate date); //(Admin icin)
    
    List<Complaint> findByUser_NameContainingIgnoreCase(String name);

    @Query("""
    SELECT c.clinic 
    FROM Complaint c 
    WHERE c.createdAt >= :date AND c.clinic IS NOT NULL 
    GROUP BY c.clinic 
    ORDER BY COUNT(c) DESC
""")
    List<Clinic> findClinicsWithComplaintsAfter(@Param("date") LocalDate date);





}
