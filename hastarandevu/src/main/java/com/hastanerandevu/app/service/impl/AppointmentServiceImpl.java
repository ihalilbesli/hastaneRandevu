package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.AppointmentRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AppointmentService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Yeni bir randevu oluşturur.
     * Sadece HASTA rolündeki kullanıcılar kendi adlarına randevu oluşturabilir.
     */
    @Override
    public Appointments createAppointment(Appointments appointments) {
        String email = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (!SecurityUtil.hasRole("HASTA")) {
            throw new RuntimeException("Sadece hastalar randevu oluşturabilir.");
        }

        User patient = userRepository.findById(appointments.getPatient().getId()).orElseThrow();
        User doctor = userRepository.findById(appointments.getDoctor().getId()).orElseThrow();

        if (!patient.getEmail().equals(currentUser.getEmail())) {
            throw new RuntimeException("Sadece kendi adınıza randevu oluşturabilirsiniz.");
        }

        appointments.setPatient(patient);
        appointments.setDoctor(doctor);
        return appointmentRepository.save(appointments);
    }

    /**
     * Belirli bir hasta ID’sine ait tüm randevuları döner.
     * Kullanıcı sadece kendi randevularını görebilir, admin herkesin randevusunu görebilir.
     */
    @Override
    public List<Appointments> getAppointemnrsByPatientId(Long patientId) {
        String email = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId()!=(patientId) && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi randevularınızı görüntüleyebilirsiniz.");
        }

        return appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient().getId()==(patientId))
                .toList();
    }

    /**
     * Belirli bir doktor ID’sine ait randevuları döner.
     * Doktor kendi randevularını görebilir, admin tüm doktor randevularını görebilir.
     */
    @Override
    public List<Appointments> getAppointmensByDoctorId(Long doctorId) {
        String email = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        if (currentUser.getId()!=(doctorId) && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece kendi randevularınızı görüntüleyebilirsiniz.");
        }

        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId()==(doctorId))
                .toList();
    }

    /**
     * ID ile randevu bilgisini döner.
     */
    @Override
    public Optional<Appointments> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Verilen tarih ve saatte doktorun uygun olup olmadığını kontrol eder.
     */
    @Override
    public boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime time) {
        User doctor = userRepository.findById(doctorId).orElseThrow();
        return appointmentRepository.findByDoctorAndDateAndTime(doctor, date, time).isEmpty();
    }

    /**
     * Belirli bir randevuyu ID ile siler.
     * (İsteğe göre sadece admin veya kendi randevusunu silen kullanıcı kontrolü eklenebilir)
     */
    @Override
    public void deteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    /**
     * Sistemdeki tüm randevuları döner.
     * Sadece ADMIN erişebilir.
     */
    @Override
    public List<Appointments> getAllAppointments() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin tüm randevuları görüntüleyebilir.");
        }
        return appointmentRepository.findAll();
    }

    @Override
    public List<Appointments> getAppointmentsByDoctorIdAndDate(Long doctorId, LocalDate date) {

        if (!SecurityUtil.hasRole("HASTA") && !SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece hasta ve adminler görüntüleyebilir.");
        }
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }
}
