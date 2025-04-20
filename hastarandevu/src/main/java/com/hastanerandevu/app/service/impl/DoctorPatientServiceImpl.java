package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.User;
import com.hastanerandevu.app.repository.*;
import com.hastanerandevu.app.service.DoctorPatientService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.*;
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
                                    PatientReportRepository patientReportRepository,
                                    UserRepository userRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.testResultRepository = testResultRepository;
        this.patientHistoryRepository = patientHistoryRepository;
        this.patientReportRepository = patientReportRepository;
        this.userRepository = userRepository;
    }

    /**
     * Doktorun geçmişte işlem yaptığı tüm hastaları tekilleştirip döner.
     * Sadece DOKTOR rolü erişebilir.
     */
    @Override
    public List<User> getMyPatients() {
        User currentDoctor = SecurityUtil.getCurrentUser(userRepository);

        if (currentDoctor.getRole() != User.Role.DOKTOR) {
            throw new RuntimeException("Sadece doktorlar kendi hastalarını görüntüleyebilir.");
        }

        Set<User> uniquePatients = new HashSet<>();

        prescriptionRepository.findByDoctor(currentDoctor).forEach(p -> uniquePatients.add(p.getPatient()));
        testResultRepository.findByDoctor(currentDoctor).forEach(t -> uniquePatients.add(t.getPatient()));
        patientHistoryRepository.findByDoctor(currentDoctor).forEach(h -> uniquePatients.add(h.getPatient()));
        patientReportRepository.findByDoctor(currentDoctor).forEach(r -> uniquePatients.add(r.getPatient()));

        return new ArrayList<>(uniquePatients);
    }

    /**
     * Doktorun işlem yaptığı hastalardan isme göre arama.
     */
    @Override
    public List<User> searchMyPatientsByName(String name) {
        return getMyPatients().stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Doktorun işlem yaptığı hastalardan email’e göre arama.
     */
    @Override
    public List<User> searchMyPatientsByEmail(String emailPart) {
        return getMyPatients().stream()
                .filter(user -> user.getEmail().toLowerCase().contains(emailPart.toLowerCase()))
                .collect(Collectors.toList());
    }
}
