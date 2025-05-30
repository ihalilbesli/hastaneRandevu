package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 10,nullable = false)
    private String prescriptionCode;  //Recete kodu

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User patient; // Hasta

@ManyToOne
@JoinColumn(name = "doctor_id", nullable = false)
private User doctor; //Doktor

@Column(nullable = false, columnDefinition = "TEXT")
private String medications; // İlaç listesi

@Column(nullable = false)
private LocalDate date; // Recete Tarih

@Column(nullable = true, columnDefinition = "TEXT")
private String description;

}
