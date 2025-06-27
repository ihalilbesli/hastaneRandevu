package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.PatientReports;
import com.hastanerandevu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PatientReportRepository extends JpaRepository<PatientReports,Long> {

    // Belirli hastaya ait raporlar
    List<PatientReports> findByPatientId(Long patientId);

    // Belirli doktorun ekledigi raporlar
    List<PatientReports> findByDoctorId(Long doctorId);

    // Belirli bir hastaya ait raporları getir
    List<PatientReports> findByPatient(User patient);

    // Belirli doktora ait tüm raporlar
    List<PatientReports> findByDoctor(User doctor);

    // Belirli hastaya ait ve tarih aralığına göre filtrelenmiş raporlar
    List<PatientReports> findByPatientAndReportDateAfter(User patient, LocalDate date);

    // Belirli doktora ait ve tarih aralığına göre filtrelenmiş raporlar
    List<PatientReports> findByDoctorAndReportDateAfter(User doctor, LocalDate date);

    // Anahtar kelimeyle rapor türüne göre arama
    List<PatientReports> findByReportTypeContainingIgnoreCase(String keyword);

    boolean existsByPatientId(Long patientId);
    boolean existsByDoctorId(Long doctorId);


}
