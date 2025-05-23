package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.Appointments;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    //Randevu olusturma
    Appointments createAppointment(Appointments appointments);

    // Belirli bir hastanın tüm randevularını getir
    List<Appointments> getAppointemnrsByPatientId(Long patientid);

    // Belirli bir doktorun randevularını getir
    List<Appointments> getAppointmensByDoctorId(Long doctorId);

    // Randevuyu ID ile getir
    Optional<Appointments> getAppointmentById(Long id);

    // Doktor o tarihte ve saatte musait mi?
    boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime time);

    // Randevuyu iptal et
    void  deteAppointment(Long id);

    // Tüm randevuları getir
    List<Appointments> getAllAppointments();

    List<Appointments> getAppointmentsByDoctorIdAndDate(Long doctorId, LocalDate date);

    void cancelAppointment(Long id);

    Appointments updateStatus(Long appointmentId, Appointments.Status newStatus, String note);

    // Belirli zaman aralığına göre tüm randevuları getir
    List<Appointments> getAllAppointmentsByPeriod(String period);

    // Açıklama içinde arama (admin kullanımı)
    List<Appointments> searchAppointmentsByKeyword(String keyword);

    // Belirli bir duruma göre toplam randevu sayısını döner (istatistik amaçlı)
    long countAppointmentsByStatus(Appointments.Status status);
}
