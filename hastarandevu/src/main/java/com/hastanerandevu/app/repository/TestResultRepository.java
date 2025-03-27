package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.TestResult;
import com.hastanerandevu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TestResultRepository extends JpaRepository<TestResult,Long> {

    // Hastanın tüm test sonuçları
    List<TestResult> findByPatient(User patient);

    // Doktorun eklediği test sonuçları
    List<TestResult> findByDoctor(User doctor);

    // Hastanın son X günde aldığı testler
    List<TestResult> findByPatientAndTestDateAfter(User patient,LocalDate date);

    // Doktorun son X günde eklediği testler
    List<TestResult> findByDoctorAndTestDateAfter(User doctor, LocalDate date);

}
