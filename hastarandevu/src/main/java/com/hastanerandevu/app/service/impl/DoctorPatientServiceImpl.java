package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.*;
import com.hastanerandevu.app.service.DoctorPatientService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DoctorPatientServiceImpl implements DoctorPatientService {
    private final PrescriptionRepository prescriptionRepository;
    private final TestResultRepository testResultRepository;
    private final PatientHistoryRepository patientHistoryRepository;
    private final PatientReportRepository patientReportRepository;
    private final UserRepository userRepository;


    public DoctorPatientServiceImpl(PrescriptionRepository prescriptionRepository,
                                    TestResultRepository testResultRepository,
                                    PatientHistoryRepository patientHistoryRepository,
                                    PatientReportRepository patientReportRepository, UserRepository userRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.testResultRepository = testResultRepository;
        this.patientHistoryRepository = patientHistoryRepository;
        this.patientReportRepository = patientReportRepository;
        this.userRepository = userRepository;
    }

    // Doktorun geçmişte işlem yaptığı tüm hastaları tekilleştirip döner
    @Override
    public List<User> getMyPatients() {
        User currentDoctor = SecurityUtil.getCurrentUser(userRepository);

        Set<User> uniquePatients = new HashSet<>();

        // Reçetelerden hastaları topla
        prescriptionRepository.findByDoctor(currentDoctor).forEach(p -> uniquePatients.add(p.getPatient()));

        // Test sonuçlarından hastaları topla
        testResultRepository.findByDoctor(currentDoctor).forEach(t -> uniquePatients.add(t.getPatient()));

        // Hasta geçmişlerinden hastaları topla
        patientHistoryRepository.findByDoctor(currentDoctor).forEach(h -> uniquePatients.add(h.getPatient()));

        // Raporlardan hastaları topla
        patientReportRepository.findByDoctor(currentDoctor).forEach(r -> uniquePatients.add(r.getPatient()));

        return new ArrayList<>(uniquePatients);
    }

    // Doktorun işlem yaptığı hastalardan isme göre arama
    @Override
    public List<User> searchMyPatientsByName(String name) {
        return getMyPatients().stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Doktorun işlem yaptığı hastalardan email’e göre arama
    @Override
    public List<User> searchMyPatientsByEmail(String emailPart) {
        return getMyPatients().stream()
                .filter(user -> user.getEmail().toLowerCase().contains(emailPart.toLowerCase()))
                .collect(Collectors.toList());
    }
}
