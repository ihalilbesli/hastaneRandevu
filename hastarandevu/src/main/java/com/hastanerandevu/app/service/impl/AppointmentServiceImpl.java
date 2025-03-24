package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.AppointmentRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AppointmentService;
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
        User patient = userRepository.findById(appointments.getPatient().getId()).orElseThrow();
        User doctor = userRepository.findById(appointments.getDoctor().getId()).orElseThrow();

        appointments.setPatient(patient);
        appointments.setDoctor(doctor);
        return appointmentRepository.save(appointments);
    }

    @Override
    public List<Appointments> getAppointemnrsByPatientId(Long patientid) {
        return appointmentRepository.findAll().stream()
                .filter(a->a.getPatient().getId()==patientid)
                .toList();
    }

    @Override
    public List<Appointments> getAppointmensByDoctorId(Long doctorId) {
        return appointmentRepository.findAll().stream()
                .filter(a->a.getDoctor().getId()==doctorId)
                .toList();
    }

    @Override
    public Optional<Appointments> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Override
    public boolean isDoctorAvailable(Long doctorId, LocalDate date, LocalTime time) {
        User doctor=userRepository.findById(doctorId).orElseThrow();
        return appointmentRepository.findByDoctorAndDateAndTime(doctor,date,time).isEmpty();
    }


    @Override
    public void deteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public List<Appointments> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}
