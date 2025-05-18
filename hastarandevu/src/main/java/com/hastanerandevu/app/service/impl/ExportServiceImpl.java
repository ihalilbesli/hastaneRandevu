package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.model.*;
import com.hastanerandevu.app.repository.*;
import com.hastanerandevu.app.service.ExportService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportServiceImpl implements ExportService {

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final ComplaintRepository complaintRepository;
    private final TestResultRepository testResultRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientHistoryRepository patientHistoryRepository;
    private final PatientReportRepository patientReportRepository;

    public ExportServiceImpl(UserRepository userRepository,
                             AppointmentRepository appointmentRepository,
                             ComplaintRepository complaintRepository,
                             TestResultRepository testResultRepository,
                             PrescriptionRepository prescriptionRepository,
                             PatientHistoryRepository patientHistoryRepository,
                             PatientReportRepository patientReportRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.complaintRepository = complaintRepository;
        this.testResultRepository = testResultRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.patientHistoryRepository = patientHistoryRepository;
        this.patientReportRepository = patientReportRepository;
    }

    private void requireAdmin() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Bu işlem yalnızca admin tarafından yapılabilir.");
        }
    }

    @Override
    public ResponseEntity<Resource> exportUsers() {
        requireAdmin();
        List<User> users = userRepository.findAll();

        String csv = users.stream()
                .map(u -> u.getId() + "," + u.getName() + "," + u.getSurname() + "," + u.getEmail() + "," + u.getRole())
                .collect(Collectors.joining("\n", "ID,Ad,Soyad,Email,Rol\n", ""));

        return toCsvResponse(csv, "users.csv");
    }

    @Override
    public ResponseEntity<Resource> exportAppointments() {
        requireAdmin();
        List<Appointments> list = appointmentRepository.findAll();

        String csv = list.stream()
                .map(a -> a.getId() + "," + a.getDate() + "," + a.getTime() + "," + a.getPatient().getEmail() + "," + a.getDoctor().getEmail() + "," + a.getClinic().getName() + "," + a.getStatus())
                .collect(Collectors.joining("\n", "ID,Tarih,Saat,Hasta,Doktor,Klinik,Durum\n", ""));

        return toCsvResponse(csv, "appointments.csv");
    }

    @Override
    public ResponseEntity<Resource> exportComplaints() {
        requireAdmin();
        List<Complaint> list = complaintRepository.findAll();

        String csv = list.stream()
                .map(c -> c.getId() + "," + c.getUser().getEmail() + "," + c.getSubject() + "," + c.getContent().replaceAll(",", " ") + "," + c.getStatus() + "," + c.getCreatedAt())
                .collect(Collectors.joining("\n", "ID,Hasta,Konusu,İçerik,Durum,Tarih\n", ""));

        return toCsvResponse(csv, "complaints.csv");
    }

    @Override
    public ResponseEntity<Resource> exportTestResults() {
        requireAdmin();
        List<TestResult> list = testResultRepository.findAll();

        String csv = list.stream()
                .map(t -> t.getId() + "," + t.getTestName() + "," + t.getTestType() + "," + t.getPatient().getEmail() + "," + t.getDoctor().getEmail() + "," + t.getTestDate())
                .collect(Collectors.joining("\n", "ID,Test Adı,Test Türü,Hasta,Doktor,Tarih\n", ""));

        return toCsvResponse(csv, "test_results.csv");
    }

    @Override
    public ResponseEntity<Resource> exportPrescriptions() {
        requireAdmin();
        List<Prescription> list = prescriptionRepository.findAll();

        String csv = list.stream()
                .map(p -> p.getId() + "," + p.getPrescriptionCode() + "," + p.getDate() + "," + p.getPatient().getEmail() + "," + p.getDoctor().getEmail() + "," + p.getMedications().replaceAll(",", ";"))
                .collect(Collectors.joining("\n", "ID,Kod,Tarih,Hasta,Doktor,İlaçlar\n", ""));

        return toCsvResponse(csv, "prescriptions.csv");
    }

    @Override
    public ResponseEntity<Resource> exportPatientHistories() {
        requireAdmin();
        List<PatientHistory> list = patientHistoryRepository.findAll();

        String csv = list.stream()
                .map(h -> h.getId() + "," + h.getPatient().getEmail() + "," + h.getDoctor().getEmail() + "," + h.getDate() + "," + h.getDiagnosis().replaceAll(",", ";") + "," + h.getTreatment().replaceAll(",", ";"))
                .collect(Collectors.joining("\n", "ID,Hasta,Doktor,Tarih,Tanı,Tedavi\n", ""));

        return toCsvResponse(csv, "patient_histories.csv");
    }

    @Override
    public ResponseEntity<Resource> exportPatientReports() {
        requireAdmin();
        List<PatientReports> list = patientReportRepository.findAll();

        String csv = list.stream()
                .map(r -> r.getId() + "," + r.getReportType() + "," + r.getReportDate() + "," + r.getPatient().getEmail() + "," + r.getDoctor().getEmail())
                .collect(Collectors.joining("\n", "ID,Rapor Türü,Tarih,Hasta,Doktor\n", ""));

        return toCsvResponse(csv, "patient_reports.csv");
    }

    // Ortak CSV dönüştürücü - içerik ve dosya adı ile ResponseEntity üretir
    private ResponseEntity<Resource> toCsvResponse(String csv, String filename) {
        ByteArrayResource resource = new ByteArrayResource(csv.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}