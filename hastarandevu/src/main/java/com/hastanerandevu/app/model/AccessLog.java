package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;          // İşlem zamanı
    private String userEmail;                // Kullanıcı email
    private String role;                     // Kullanıcı rolü (HASTA, DOKTOR, ADMIN)
    private String endpoint;                 // Çağrılan API URL’i
    private String method;                   // GET, POST, PUT, DELETE
    private String entity;                   // İlgili varlık: Appointment, User, vs.
    private String actionType;               // CREATE, READ, UPDATE, DELETE
    private String status;                   // BAŞARILI, HATA
    private String errorMessage;             // (Opsiyonel) Hata mesajı varsa

}
