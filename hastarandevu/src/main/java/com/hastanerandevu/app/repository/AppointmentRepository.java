package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointments,Long> {
    // Belirli bir hastanın tüm randevularını getir (Geçmiş & Yaklaşan)
    List<Appointments> findByPatient(User patient);

    // Belirli bir doktorun tüm randevularını getir
    List<Appointments>findByDoctor(User doctor);

    // Belirli bir tarihteki tüm randevuları getir
    List<Appointments>findByDate(LocalDate date);

    // Hasta için, aynı doktora aynı gün içinde randevu var mı? (Engelleme için)
    Optional<Appointments> findByPatientAndDoctorAndDate(User patient, User doctor, LocalDate date);

    // Doktor için belirli bir tarihte randevu var mı?
    List<Appointments> findByDoctorAndDate(User doctor,LocalDate date);
    // Doktor için belirli bir tarih araliginda randevu var mı?
    List<Appointments> findByDoctorAndDateBetween(User doctor, LocalDate startDate, LocalDate endDate);



    // Belirli bir doktor için belirli bir tarih ve saat için çakışma var mı?
    List<Appointments> findByDoctorAndDateAndTime(User doctor, LocalDate date, LocalTime time);
    //Hasta ayni gun ayni saate 2 farkli randevu alabilir mi
    Optional<Appointments> findByPatientAndDateAndTime(User patient, LocalDate date, LocalTime time);


    // Belirli bir hastanın **aktif** randevularını listeleme
    List<Appointments> findByPatientAndStatus(User patient, Appointments.Status status);

    // Belirli bir doktorun **aktif** randevularını listeleme
    List<Appointments> findByDoctorAndStatus(User doctor, Appointments.Status status);

    List<Appointments> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    Optional<Appointments> findByPatientIdAndClinicAndStatus(Long patientId, Clinic clinic, Appointments.Status status);

    boolean existsByDoctorIdAndPatientId(Long doctorId, Long patientId);

    List<Appointments> findByDateAfter(LocalDate date);
    List<Appointments> findByDescriptionContainingIgnoreCase(String keyword);
    long countByStatus(Appointments.Status status);

    @Query("""
    SELECT a.clinic 
    FROM Appointments a 
    WHERE a.date >= :date 
    GROUP BY a.clinic 
    ORDER BY COUNT(a) DESC
""")
    List<Clinic> findTopClinicsByDate(@Param("date") LocalDate date);

    boolean existsByPatientId(Long patientId);
    boolean existsByDoctorId(Long doctorId);



}
