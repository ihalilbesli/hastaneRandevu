package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patient_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User patient; //Hasta

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor; //Doktor

    @Column(nullable = false)
    private LocalDate date; //TEshis tarihi

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnosis;       //tani

    @Column(nullable = false, columnDefinition = "TEXT")
    private String treatment; //tedavi

    @Column(columnDefinition = "TEXT")
    private String notes;  //Genel notlar
}
