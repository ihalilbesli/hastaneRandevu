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

    @Override
    public Appointments createAppointment(Appointments appointments) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getRole() != User.Role.HASTA) {
            throw new RuntimeException("Sadece hastalar randevu oluşturabilir.");
        }

        User patient = userRepository.findById(appointments.getPatient().getId()).orElseThrow();
        User doctor = userRepository.findById(appointments.getDoctor().getId()).orElseThrow();

        if (patient.getId()!=(currentUser.getId())) {
            throw new RuntimeException("Sadece kendi adınıza randevu oluşturabilirsiniz.");
        }

        Optional<Appointments> existing = appointmentRepository
                .findByPatientIdAndClinicAndStatus(patient.getId(), doctor.getSpecialization(), Appointments.Status.AKTIF);

        existing.ifPresent(a -> {
            a.setStatus(Appointments.Status.IPTAL_EDILDI);
            appointmentRepository.save(a);
        });

        appointments.setClinic(doctor.getSpecialization());
        appointments.setPatient(patient);
        appointments.setDoctor(doctor);
        appointments.setStatus(Appointments.Status.AKTIF);

        return appointmentRepository.save(appointments);
    }

    @Override
    public List<Appointments> getAppointemnrsByPatientId(Long patientId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getId()!=(patientId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi randevularınızı görüntüleyebilirsiniz.");
        }

        List<Appointments> appointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getPatient().getId()==(patientId))
                .toList();
        updateExpiredAppointments(appointments);
        return appointments;
    }

    @Override
    public List<Appointments> getAppointmensByDoctorId(Long doctorId) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        if (currentUser.getId()!=(doctorId) && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece kendi randevularınızı görüntüleyebilirsiniz.");
        }

        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctor().getId()==(doctorId))
                .toList();
    }

    @Override
    public Optional<Appointments> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime time) {
        User doctor = userRepository.findById(doctorId).orElseThrow();
        return appointmentRepository.findByDoctorAndDateAndTime(doctor, date, time).isEmpty();
    }

    @Override
    public void deteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<Appointments> getAllAppointments() {
        if (SecurityUtil.getCurrentUser(userRepository).getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece admin tüm randevuları görüntüleyebilir.");
        }
        return appointmentRepository.findAll();
    }

    @Override
    public void cancelAppointment(Long id) {
        Appointments appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı."));

        if (appointment.getStatus() == Appointments.Status.AKTIF) {
            appointment.setStatus(Appointments.Status.IPTAL_EDILDI);
            appointmentRepository.save(appointment);
        } else {
            throw new RuntimeException("Randevu zaten iptal edilmiş.");
        }
    }

    @Override
    public Appointments updateStatus(Long appointmentId, Appointments.Status newStatus, String note) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);

        Appointments appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));

        boolean isDoctor = currentUser.getRole() == User.Role.DOKTOR &&
                currentUser.getId()==(appointment.getDoctor().getId());
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;

        if (!isDoctor && !isAdmin) {
            throw new RuntimeException("Sadece ilgili doktor veya admin durumu güncelleyebilir.");
        }

        appointment.setStatus(newStatus);

        return appointmentRepository.save(appointment);
    }


    @Override
    public List<Appointments> getAppointmentsByDoctorIdAndDate(Long doctorId, LocalDate date) {
        User currentUser = SecurityUtil.getCurrentUser(userRepository);
        if (currentUser.getRole() != User.Role.HASTA && currentUser.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Sadece hasta ve adminler görüntüleyebilir.");
        }
        return appointmentRepository.findByDoctorIdAndDate(doctorId, date);
    }

    private void updateExpiredAppointments(List<Appointments> appointments) {
        LocalDate today = LocalDate.now();

        for (Appointments a : appointments) {
            if (a.getDate().isBefore(today) && a.getStatus() == Appointments.Status.AKTIF) {
                a.setStatus(Appointments.Status.IPTAL_EDILDI);
                appointmentRepository.save(a);
            }
        }
    }
}