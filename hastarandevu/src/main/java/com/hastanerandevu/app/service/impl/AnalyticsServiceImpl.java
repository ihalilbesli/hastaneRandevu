package com.hastanerandevu.app.service.impl;

import com.hastanerandevu.app.dto.Analytics.*;
import com.hastanerandevu.app.repository.AnalyticsRepository;
import com.hastanerandevu.app.repository.UserRepository;
import com.hastanerandevu.app.service.AnalyticsService;
import com.hastanerandevu.app.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {
    private final AnalyticsRepository analyticsRepository;
    private final UserRepository userRepository;

    public AnalyticsServiceImpl(AnalyticsRepository analyticsRepository, UserRepository userRepository) {
        this.analyticsRepository = analyticsRepository;
        this.userRepository = userRepository;
    }
    private void checkAdminAccess() {
        if (!SecurityUtil.hasRole("ADMIN")) {
            throw new RuntimeException("Sadece admin bu verilere erişebilir.");
        }
    }


    @Override
    public List<ClinicAppointmentCountDTO> getAppointmentCountByClinic() {
        checkAdminAccess();
        return analyticsRepository.getAppointmentCountByClinic();
    }

    @Override
    public List<DateAppointmentCountDTO> getAppointmentCountByDate() {
        checkAdminAccess();
        return analyticsRepository.getAppointmentCountByDate();
    }

    @Override
    public List<AppointmentStatusCountDTO> getAppointmentCountByStatus() {
        checkAdminAccess();
        return analyticsRepository.getAppointmentCountByStatus();
    }

    @Override
    public List<DoctorAppointmentCountDTO> getAppointmentCountByDoctor() {
        checkAdminAccess();
        return analyticsRepository.getAppointmentCountByDoctor();
    }

    @Override
    public List<MonthlyUserRegistrationDTO> getMonthlyUserRegistration() {
        checkAdminAccess();
        return analyticsRepository.getMonthlyUserRegistration();
    }

    @Override
    public List<ComplaintStatusCountDTO> getComplaintCountByStatus() {
        checkAdminAccess();
        return analyticsRepository.getComplaintCountByStatus();
    }

    @Override
    public List<ClinicComplaintCountDTO> getComplaintCountByClinic() {
        checkAdminAccess();
        return analyticsRepository.getComplaintCountByClinic();

    }

    @Override
    public List<TimeSlotAppointmentCountDTO> getAppointmentCountByTimeSlot() {
        checkAdminAccess();
        return analyticsRepository.getAppointmentCountByTimeSlot();
    }
    @Override
    public List<UserRoleCountDTO> getUserCountByRole() {
        checkAdminAccess();
        return analyticsRepository.getUserCountByRole();
    }

    @Override
    public List<UserGenderCountDTO> getUserCountByGender() {
        checkAdminAccess();
        return analyticsRepository.getUserCountByGender();
    }

    @Override
    public List<UserBloodTypeCountDTO> getUserCountByBloodType() {
        checkAdminAccess();
        return analyticsRepository.getUserCountByBloodType();
    }

    @Override
    public List<ClinicDoctorCountDTO> getDoctorCountByClinic() {
        checkAdminAccess();
        return analyticsRepository.getDoctorCountByClinic();
    }

    @Override
    public List<ComplaintSubjectCountDTO> getComplaintCountBySubject() {
        checkAdminAccess();
        return analyticsRepository.getComplaintCountBySubject();
    }
}
