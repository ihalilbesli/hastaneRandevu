package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.User;

import java.util.List;

public interface DoctorPatientService {
    List<User> getMyPatients();
    List<User> searchMyPatientsByName(String name);
    List<User> searchMyPatientsByEmail(String emailPart);
}
