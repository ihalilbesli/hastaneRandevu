package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "test_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id",nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id",nullable = false)
    private User doctor;    // Sonucu ekleyen doktor

    @Column(nullable = false)   // Ornek: "Kan Tahlili"
    private String testName;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String result;

    @Column(columnDefinition = "TEXT")
    private String doctorComment;   // Doktor yorumu (Opsiyonel)

    @Column(nullable = false)
    private LocalDate testDate;


}
