package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog,Long> {
    // Belirli bir kullanıcıya ait logları getir

    List<AccessLog> findByUserEmail(String userEmail);

    //  Belirli bir role sahip kullanıcıların loglarını getir
    List<AccessLog> findByRole(String role);
    //  Başarısız (error) logları getir
    List<AccessLog> findByStatus(String status);

    //  Belirli bir tarih aralığındaki logları getir
    List<AccessLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

}
