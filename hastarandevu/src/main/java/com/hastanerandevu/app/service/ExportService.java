package com.hastanerandevu.app.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface ExportService {

    // ✅ Kullanıcıları dışa aktar
    ResponseEntity<Resource> exportUsers();

    // ✅ Randevuları dışa aktar
    ResponseEntity<Resource> exportAppointments();

    // ✅ Şikayetleri dışa aktar
    ResponseEntity<Resource> exportComplaints();

    // ✅ Test sonuçlarını dışa aktar
    ResponseEntity<Resource> exportTestResults();

    // ✅ Reçeteleri dışa aktar
    ResponseEntity<Resource> exportPrescriptions();

    // ✅ Hasta geçmişini dışa aktar
    ResponseEntity<Resource> exportPatientHistories();

    // ✅ Hasta raporlarını dışa aktar
    ResponseEntity<Resource> exportPatientReports();
}
