package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patient_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientReports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor; // Raporu ekleyen doktor

    @Column(nullable = false)
    private String reportType;  // Ornek: "MR Sonucu"

    @Column(nullable = false)
    private String fileUrl;     //Yuklenen Rapor yolu veya resim yolu

    @Column(nullable = false)
    private LocalDate reportDate;

}
