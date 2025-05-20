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
                .map(u -> u.getBirthDate() + "," + u.getId() + "," + u.getName() + "," + u.getSurname() + "," + u.getEmail()
                        + "," + u.getPhoneNumber() + "," + u.getRole() + "," + u.getGender()
                        + "," + u.getBloodType()
                        + "," + (u.getChronicDiseases() == null ? "-" : u.getChronicDiseases())
                        + "," + (u.getSpecialization() == null ? "-" : u.getSpecialization())
                        + "," + (u.getClinic() == null ? "-" : u.getClinic().getName())
                )
                .collect(Collectors.joining("\n", "Doğum Tarihi,ID,Ad,Soyad,Email,Telefon,Rol,Cinsiyet,Kan Grubu,Kronik Hastalık,Uzmanlık,Klinik\n", ""));

        return toCsvResponse(csv, "users.csv");
    }

    @Override
    public ResponseEntity<Resource> exportAppointments() {
        requireAdmin();
        List<Appointments> list = appointmentRepository.findAll();

        String csv = list.stream()
                .map(a -> a.getId() + "," + a.getPatient().getName() + " " + a.getPatient().getSurname()
                        + "," + a.getDoctor().getName() + " " + a.getDoctor().getSurname()
                        + "," + a.getClinic().getName()
                        + "," + a.getDate() + "," + a.getTime()
                        + "," + a.getStatus() + "," + a.getDescription().replaceAll(",", " "))
                .collect(Collectors.joining("\n", "ID,Hasta,Doktor,Klinik,Tarih,Saat,Durum,Açıklama\n", ""));

        return toCsvResponse(csv, "appointments.csv");
    }

    @Override
    public ResponseEntity<Resource> exportComplaints() {
        requireAdmin();
        List<Complaint> list = complaintRepository.findAll();

        String csv = list.stream()
                .map(c -> c.getId() + "," + c.getSubject()
                        + "," + c.getContent().replaceAll(",", " ")
                        + "," + c.getStatus() + "," + c.getCreatedAt()
                        + "," + (c.getAdminNote() == null ? "-" : c.getAdminNote().replaceAll(",", " "))
                        + "," + (c.getClinic() == null ? "-" : c.getClinic().getName())
                        + "," + c.getUser().getName() + " " + c.getUser().getSurname())
                .collect(Collectors.joining("\n", "ID,Konusu,İçerik,Durum,Oluşturulma Tarihi,Yönetici Notu,Klinik,Kullanıcı\n", ""));

        return toCsvResponse(csv, "complaints.csv");
    }

    @Override
    public ResponseEntity<Resource> exportPrescriptions() {
        requireAdmin();
        List<Prescription> list = prescriptionRepository.findAll();

        String csv = list.stream()
                .map(p -> p.getId() + "," + p.getPrescriptionCode() + "," + p.getDate()
                        + "," + p.getDescription().replaceAll(",", " ")
                        + "," + p.getMedications().replaceAll(",", ";")
                        + "," + p.getDoctor().getName() + " " + p.getDoctor().getSurname()
                        + "," + p.getPatient().getName() + " " + p.getPatient().getSurname())
                .collect(Collectors.joining("\n", "ID,Reçete Kodu,Tarih,Açıklama,İlaçlar,Doktor,Hasta\n", ""));

        return toCsvResponse(csv, "prescriptions.csv");
    }

    @Override
    public ResponseEntity<Resource> exportTestResults() {
        requireAdmin();
        List<TestResult> list = testResultRepository.findAll();

        String csv = list.stream()
                .map(t -> t.getId() + "," + t.getPatient().getName() + " " + t.getPatient().getSurname()
                        + "," + t.getDoctor().getName() + " " + t.getDoctor().getSurname()
                        + "," + t.getTestDate() + "," + t.getTestName() + "," + t.getTestType()
                        + "," + t.getResult().replaceAll(",", " ")
                        + "," + (t.getDoctorComment() == null ? "-" : t.getDoctorComment().replaceAll(",", " ")))
                .collect(Collectors.joining("\n", "ID,Hasta,Doktor,Test Tarihi,Test Adı,Test Türü,Sonuç,Doktor Yorumu\n", ""));

        return toCsvResponse(csv, "test-results.csv");
    }

    @Override
    public ResponseEntity<Resource> exportPatientHistories() {
        requireAdmin();
        List<PatientHistory> list = patientHistoryRepository.findAll();

        String csv = list.stream()
                .map(h -> h.getId() + "," + h.getPatient().getName() + " " + h.getPatient().getSurname()
                        + "," + h.getDoctor().getName() + " " + h.getDoctor().getSurname()
                        + "," + h.getDiagnosis().replaceAll(",", ";")
                        + "," + h.getTreatment().replaceAll(",", ";")
                        + "," + (h.getNotes() == null ? "-" : h.getNotes().replaceAll(",", " "))
                        + "," + h.getDate())
                .collect(Collectors.joining("\n", "ID,Hasta,Doktor,Teşhis,Tedavi,Notlar,Tarih\n", ""));

        return toCsvResponse(csv, "patient-histories.csv");
    }

    @Override
    public ResponseEntity<Resource> exportPatientReports() {
        requireAdmin();
        List<PatientReports> list = patientReportRepository.findAll();

        String csv = list.stream()
                .map(r -> r.getId() + "," + r.getPatient().getName() + " " + r.getPatient().getSurname()
                        + "," + r.getDoctor().getName() + " " + r.getDoctor().getSurname()
                        + "," + r.getReportType()
                        + "," + r.getReportDate()
                        + "," + r.getFileUrl())
                .collect(Collectors.joining("\n", "ID,Hasta,Doktor,Rapor Türü,Tarih,Dosya\n", ""));

        return toCsvResponse(csv, "patient-reports.csv");
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