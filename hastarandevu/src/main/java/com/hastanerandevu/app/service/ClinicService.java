package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.User;

import java.util.List;
import java.util.Optional;

public interface ClinicService {
    Clinic createClinic(Clinic clinic);

    List<Clinic> getAllClinics();

    Optional<Clinic> getClinicById(Long id);

    Clinic updateClinic(Long id, Clinic updatedClinic);

    void deactivateClinic(Long id); // pasifleştir

    void activateClinic(Long id);   // aktifleştir

    boolean existsByName(String name);

    List<User> getDoctorsByClinicId(Long clinicId);

}
