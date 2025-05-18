package com.hastanerandevu.app.service;
import com.hastanerandevu.app.dto.Analytics.*;

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

    List<UserRoleCountDTO> getUserCountByRole();
    List<UserGenderCountDTO> getUserCountByGender();
    List<UserBloodTypeCountDTO> getUserCountByBloodType();
    List<ClinicDoctorCountDTO> getDoctorCountByClinic();

    List<ComplaintSubjectCountDTO> getComplaintCountBySubject();


}
