package com.hastanerandevu.app.service;

import com.hastanerandevu.app.model.Prescription;

import java.util.List;

public interface PrescriptionService {
    //  Reçete oluşturma (sadece doktor)
    Prescription createPrescription(Prescription prescription);

    //  Tüm reçeteleri getir
    List<Prescription> getAllPrescriptions();

    //  Belirli ID ile reçete getir
    Prescription getPrescriptionById(Long id);

    //  Hasta ID'sine göre reçeteleri getir
    List<Prescription> getPrescriptionsByPatientId(Long patientId);

    //  Doktor ID'sine göre yazdığı reçeteleri getir
    List<Prescription> getPrescriptionsByDoctorId(Long doctorId);

    //  Hasta - zaman filtresi
    List<Prescription> getPrescriptionsByPatientIdAndPeriod(Long patientId, String period);

    //  Doktor - zaman filtresi
    List<Prescription> getPrescriptionsByDoctorIdAndPeriod(Long doctorId, String period);

    //  Açıklama`daki kelimelerden  arama
    List<Prescription> searchPrescriptionsByKeyword(String keyword);

    //  Güncelleme
    Prescription updatePrescription(Long id, Prescription updatedPrescription);

    //  Silme
    void deletePrescription(Long id);
}

