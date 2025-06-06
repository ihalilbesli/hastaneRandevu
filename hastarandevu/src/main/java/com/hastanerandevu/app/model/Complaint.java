package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "complaint")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;         //sikayetler

    @Column(nullable = false)
    private String subject; // Şikayet konusu

    @Enumerated(EnumType.STRING)
    private Status status=Status.BEKLEMEDE;

    @Column(columnDefinition = "TEXT")
    private String adminNote; // Admin veya doktorun bıraktığı çözüm notu

    @Column(nullable = false,updatable = false)
    private LocalDate createdAt=LocalDate.now();

    public enum Status {
        BEKLEMEDE,INCELEMEDE, COZULDU
    }
    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;  //opsiyonel

}
