package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.Prescription;
import com.hastanerandevu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {
    //  Hasta bazlı reçeteler
    List<Prescription> findByPatient(User patient);

    //  Doktor bazlı reçeteler
    List<Prescription> findByDoctor(User doctor);

    //  Belirli tarihten sonraki reçeteler (hasta)
    List<Prescription> findByPatientAndDateAfter(User patient, LocalDate date);

    //  Belirli tarihten sonraki reçeteler (doktor)
    List<Prescription> findByDoctorAndDateAfter(User doctor, LocalDate date);

    // Açıklama içinde geçen kelimeye göre arama
    List<Prescription> findByDescriptionContainingIgnoreCase(String keyword);
}
