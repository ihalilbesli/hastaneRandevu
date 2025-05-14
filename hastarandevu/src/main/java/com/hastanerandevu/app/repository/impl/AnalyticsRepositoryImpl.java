package com.hastanerandevu.app.repository.impl;

import com.hastanerandevu.app.dto.*;
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
        String jpql = "SELECT new com.hastanerandevu.app.dto.ClinicAppointmentCountDTO(c.name, COUNT(a)) " +
                "FROM Appointments a JOIN a.clinic c GROUP BY c.name";
        return entityManager.createQuery(jpql, ClinicAppointmentCountDTO.class).getResultList();
    }

    @Override
    public List<DateAppointmentCountDTO> getAppointmentCountByDate() {
        String jpql = "SELECT new com.hastanerandevu.app.dto.DateAppointmentCountDTO(a.date, COUNT(a)) " +
                "FROM Appointments a GROUP BY a.date ORDER BY a.date";
        return entityManager.createQuery(jpql, DateAppointmentCountDTO.class).getResultList();
    }

    @Override
    public List<AppointmentStatusCountDTO> getAppointmentCountByStatus() {
        String jpql = "SELECT new com.hastanerandevu.app.dto.AppointmentStatusCountDTO(a.status, COUNT(a)) " +
                "FROM Appointments a GROUP BY a.status";
        return entityManager.createQuery(jpql, AppointmentStatusCountDTO.class).getResultList();
    }

    @Override
    public List<DoctorAppointmentCountDTO> getAppointmentCountByDoctor() {
        String jpql = "SELECT new com.hastanerandevu.app.dto.DoctorAppointmentCountDTO(CONCAT(d.name, ' ', d.surname), COUNT(a)) " +
                "FROM Appointments a JOIN a.doctor d GROUP BY d.name, d.surname";
        return entityManager.createQuery(jpql, DoctorAppointmentCountDTO.class).getResultList();
    }

    @Override
    public List<MonthlyUserRegistrationDTO> getMonthlyUserRegistration() {
        String jpql = "SELECT new com.hastanerandevu.app.dto.MonthlyUserRegistrationDTO(" +
                "FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m'), u.role, COUNT(u)) " +
                "FROM User u GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m'), u.role ORDER BY FUNCTION('DATE_FORMAT', u.createdAt, '%Y-%m')";
        return entityManager.createQuery(jpql, MonthlyUserRegistrationDTO.class).getResultList();
    }

    @Override
    public List<ComplaintStatusCountDTO> getComplaintCountByStatus() {
        String jpql = "SELECT new com.hastanerandevu.app.dto.ComplaintStatusCountDTO(c.status, COUNT(c)) " +
                "FROM Complaint c GROUP BY c.status";
        return entityManager.createQuery(jpql, ComplaintStatusCountDTO.class).getResultList();
    }

    @Override
    public List<ClinicComplaintCountDTO> getComplaintCountByClinic() {
        String jpql = "SELECT new com.hastanerandevu.app.dto.ClinicComplaintCountDTO(cl.name, COUNT(c)) " +
                "FROM Complaint c JOIN c.user u JOIN u.clinic cl GROUP BY cl.name";
        return entityManager.createQuery(jpql, ClinicComplaintCountDTO.class).getResultList();
    }

    @Override
    public List<TimeSlotAppointmentCountDTO> getAppointmentCountByTimeSlot() {
        String jpql = "SELECT new com.hastanerandevu.app.dto.TimeSlotAppointmentCountDTO(FUNCTION('DATE_FORMAT', a.time, '%H:%i'), COUNT(a)) " +
                "FROM Appointments a GROUP BY FUNCTION('DATE_FORMAT', a.time, '%H:%i') ORDER BY FUNCTION('DATE_FORMAT', a.time, '%H:%i')";
        return entityManager.createQuery(jpql, TimeSlotAppointmentCountDTO.class).getResultList();
    }
}
