package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.ClinicRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.ClinicService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;

    public ClinicServiceImpl(ClinicRepository clinicRepository, UserRepository userRepository) {
        this.clinicRepository = clinicRepository;
        this.userRepository = userRepository;
    }
    @Override
    public Clinic createClinic(Clinic clinic) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin yeni klinik ekleyebilir.");
        }

        if (clinicRepository.existsByNameIgnoreCase(clinic.getName())) {
            throw new RuntimeException("Bu isimde bir klinik zaten mevcut.");
        }

        return clinicRepository.save(clinic);
    }

    @Override
    public List<Clinic> getAllClinics() {
        if (!SecurityUtil.hasRole("ADMIN") && !SecurityUtil.hasRole("DOKTOR") && !SecurityUtil.hasRole("HASTA")) {
            throw new RuntimeException("Klinikleri sadece yetkili kullanıcılar görüntüleyebilir.");
        }

        return clinicRepository.findAll();
    }

    @Override
    public Optional<Clinic> getClinicById(Long id) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin klinik detaylarını görüntüleyebilir.");
        }

        return clinicRepository.findById(id);
    }

    @Override
    public Clinic updateClinic(Long id, Clinic updatedClinic) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin klinik güncelleyebilir.");
        }

        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klinik bulunamadı"));

        clinic.setName(updatedClinic.getName());
        clinic.setDescription(updatedClinic.getDescription());

        return clinicRepository.save(clinic);
    }

    @Override
    public void deactivateClinic(Long id) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin klinik pasif hale getirebilir.");
        }

        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klinik bulunamadı"));

        clinic.setIsActive(false);
        clinicRepository.save(clinic);
    }

    @Override
    public void activateClinic(Long id) {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin klinik aktif hale getirebilir.");
        }

        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Klinik bulunamadı"));

        clinic.setIsActive(true);
        clinicRepository.save(clinic);
    }

    @Override
    public boolean existsByName(String name) {
        return clinicRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public List<User> getDoctorsByClinicId(Long clinicId) {
        if (
                !SecurityUtil.hasRole("ADMIN") &&
                        !SecurityUtil.hasRole("DOKTOR") &&
                        !SecurityUtil.hasRole("HASTA")
        ) {
            throw new RuntimeException("Bu işlem için yetkiniz yok.");
        }

        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Klinik bulunamadı."));

        return userRepository.findByClinic(clinic);
    }

}
