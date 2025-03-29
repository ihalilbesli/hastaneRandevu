package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.PatientHistory;

import java.util.List;

public interface PatientHistoryService {

    // Yeni gecmis bilgisi olusturur (sadece doktor)
    PatientHistory createHistory(PatientHistory history);

    // Gecmis bilgisini gunceller (sadece doktor)
    PatientHistory updateHistory(Long id, PatientHistory updatedHistory);

    // Gecmis bilgisini siler (doktor veya admin)
    void deleteHistory(Long id);

    // ID ile gecmis bilgisini getirir
    PatientHistory getHistoryById(Long id);

    // Tum gecmis bilgilerini listeler (admin icin)
    List<PatientHistory> getAllHistories();

    // Hastaya ait gecmis bilgilerini getirir
    List<PatientHistory> getHistoriesByPatientId(Long patientId);

    // Doktorun ekledigi gecmis bilgilerini getirir
    List<PatientHistory> getHistoriesByDoctorId(Long doctorId);

    // Hastaya ait zamana gore listeleme yapar
    List<PatientHistory> getHistoriesByPatientIdAndPeriod(Long patientId, String period);

    // Doktora ait zamana gore listeleme yapar
    List<PatientHistory> getHistoriesByDoctorIdAndPeriod(Long doctorId, String period);

    // Tani icerigine gore arama yapar
    List<PatientHistory> searchHistoriesByDiagnosis(String keyword);

    // Tedavi icerigine gore arama yapar
    List<PatientHistory> searchHistoriesByTreatment(String keyword);
}
