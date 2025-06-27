package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.PatientHistory;
import com.hastanerandevu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PatientHistoryRepository extends JpaRepository<PatientHistory,Long> {

    List<PatientHistory> findByPatient(User patient);
    List<PatientHistory> findByDoctor(User doctor);

    List<PatientHistory> findByPatientAndDateAfter(User patient, LocalDate date);
    List<PatientHistory> findByDoctorAndDateAfter(User doctor, LocalDate date);

    List<PatientHistory> findByDiagnosisContainingIgnoreCase(String keyword);
    List<PatientHistory> findByTreatmentContainingIgnoreCase(String keyword);

    boolean existsByPatientId(Long patientId);
    boolean existsByDoctorId(Long doctorId);




}
