package com.hastanerandevu.app.service;
import com.hastanerandevu.app.dto.*;

import java.util.List;
public interface AnalyticsService {
    List<ClinicAppointmentCountDTO> getAppointmentCountByClinic();

    List<DateAppointmentCountDTO> getAppointmentCountByDate();

    List<AppointmentStatusCountDTO> getAppointmentCountByStatus();

    List<DoctorAppointmentCountDTO> getAppointmentCountByDoctor();

    List<MonthlyUserRegistrationDTO> getMonthlyUserRegistration();

    List<ComplaintStatusCountDTO> getComplaintCountByStatus();

    List<ClinicComplaintCountDTO> getComplaintCountByClinic();

    List<TimeSlotAppointmentCountDTO> getAppointmentCountByTimeSlot();
}
