package com.hastanerandevu.app.repository.impl;

import com.hastanerandevu.app.dto.Analytics.*;
import com.hastanerandevu.app.repository.AnalyticsRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AnalyticsRepositoryImpl implements AnalyticsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ClinicAppointmentCountDTO> getAppointmentCountByClinic() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.ClinicAppointmentCountDTO(c.name, COUNT(a))
            FROM Appointments a
            JOIN a.clinic c
            WHERE a.status = com.hastanerandevu.app.model.Appointments.Status.COMPLETED
            GROUP BY c.name
        """;
        return entityManager.createQuery(jpql, ClinicAppointmentCountDTO.class).getResultList();
    }

    @Override
    public List<DateAppointmentCountDTO> getAppointmentCountByDate() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.DateAppointmentCountDTO(a.date, COUNT(a))
            FROM Appointments a
            WHERE a.status = com.hastanerandevu.app.model.Appointments.Status.COMPLETED
            GROUP BY a.date
            ORDER BY a.date
        """;
        return entityManager.createQuery(jpql, DateAppointmentCountDTO.class).getResultList();
    }

    @Override
    public List<AppointmentStatusCountDTO> getAppointmentCountByStatus() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.AppointmentStatusCountDTO(a.status, COUNT(a))
            FROM Appointments a
            GROUP BY a.status
        """;
        return entityManager.createQuery(jpql, AppointmentStatusCountDTO.class).getResultList();
    }

    @Override
    public List<DoctorAppointmentCountDTO> getAppointmentCountByDoctor() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.DoctorAppointmentCountDTO(CONCAT(d.name, ' ', d.surname), COUNT(a))
            FROM Appointments a
            JOIN a.doctor d
            WHERE a.status = com.hastanerandevu.app.model.Appointments.Status.COMPLETED
            GROUP BY d.name, d.surname
        """;
        return entityManager.createQuery(jpql, DoctorAppointmentCountDTO.class).getResultList();
    }

    @Override
    public List<MonthlyUserRegistrationDTO> getMonthlyUserRegistration() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.MonthlyUserRegistrationDTO(
                FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m'), u.role, COUNT(u))
            FROM User u
            GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m'), u.role
            ORDER BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m')
        """;
        return entityManager.createQuery(jpql, MonthlyUserRegistrationDTO.class).getResultList();
    }

    @Override
    public List<ComplaintStatusCountDTO> getComplaintCountByStatus() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.ComplaintStatusCountDTO(c.status, COUNT(c))
            FROM Complaint c
            GROUP BY c.status
        """;
        return entityManager.createQuery(jpql, ComplaintStatusCountDTO.class).getResultList();
    }

    @Override
    public List<ClinicComplaintCountDTO> getComplaintCountByClinic() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.ClinicComplaintCountDTO(c.clinic.name, COUNT(c))
            FROM Complaint c
            WHERE c.clinic IS NOT NULL
            GROUP BY c.clinic.name
        """;
        return entityManager.createQuery(jpql, ClinicComplaintCountDTO.class).getResultList();
    }

    @Override
    public List<TimeSlotAppointmentCountDTO> getAppointmentCountByTimeSlot() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.TimeSlotAppointmentCountDTO(FUNCTION('DATE_FORMAT', a.time, '%H:%i'), COUNT(a))
            FROM Appointments a
            WHERE a.status = com.hastanerandevu.app.model.Appointments.Status.COMPLETED
            GROUP BY FUNCTION('DATE_FORMAT', a.time, '%H:%i')
            ORDER BY FUNCTION('DATE_FORMAT', a.time, '%H:%i')
        """;
        return entityManager.createQuery(jpql, TimeSlotAppointmentCountDTO.class).getResultList();
    }

    @Override
    public List<UserRoleCountDTO> getUserCountByRole() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.UserRoleCountDTO(u.role, COUNT(u))
            FROM User u
            GROUP BY u.role
        """;
        return entityManager.createQuery(jpql, UserRoleCountDTO.class).getResultList();
    }

    @Override
    public List<UserGenderCountDTO> getUserCountByGender() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.UserGenderCountDTO(u.gender, COUNT(u))
            FROM User u
            GROUP BY u.gender
        """;
        return entityManager.createQuery(jpql, UserGenderCountDTO.class).getResultList();
    }

    @Override
    public List<UserBloodTypeCountDTO> getUserCountByBloodType() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.UserBloodTypeCountDTO(u.bloodType, COUNT(u))
            FROM User u
            WHERE u.bloodType IS NOT NULL
            GROUP BY u.bloodType
        """;
        return entityManager.createQuery(jpql, UserBloodTypeCountDTO.class).getResultList();
    }

    @Override
    public List<ClinicDoctorCountDTO> getDoctorCountByClinic() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.ClinicDoctorCountDTO(c.name, COUNT(u))
            FROM User u
            JOIN u.clinic c
            WHERE u.role = com.hastanerandevu.app.model.User.Role.DOKTOR
            GROUP BY c.name
        """;
        return entityManager.createQuery(jpql, ClinicDoctorCountDTO.class).getResultList();
    }

    @Override
    public List<ComplaintSubjectCountDTO> getComplaintCountBySubject() {
        String jpql = """
            SELECT new com.hastanerandevu.app.dto.Analytics.ComplaintSubjectCountDTO(c.subject, COUNT(c))
            FROM Complaint c
            GROUP BY c.subject
        """;
        return entityManager.createQuery(jpql, ComplaintSubjectCountDTO.class).getResultList();
    }
}
