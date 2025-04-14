package com.hastanerandevu.app.controller;

import com.hastanerandevu.app.model.Appointments;
import com.hastanerandevu.app.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hastarandevu/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Randevu oluştur
    @PostMapping
    public ResponseEntity<Appointments> createAppointment(@RequestBody Appointments appointments){
        Appointments saved=appointmentService.createAppointment(appointments);
        return ResponseEntity.ok(saved);
    }

    //Tum randevulari getir
    @GetMapping()
    public ResponseEntity<List<Appointments>> getAllAppointments(){
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    //  ID ile randevu getir
    @GetMapping("/{id}")
    public ResponseEntity<Appointments>getAppointmentById(@PathVariable Long id){
        Optional<Appointments> optionalAppointments=appointmentService.getAppointmentById(id);
        if (optionalAppointments.isPresent()){
            return ResponseEntity.ok(optionalAppointments.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }
    //  Doktora ait randevular
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointments>> getAppointmentsByDoctor(@PathVariable Long doctorId){
        return ResponseEntity.ok(appointmentService.getAppointmensByDoctorId(doctorId));
    }

    //  Doktor uygun mu? (tarih + saat)
    @GetMapping("/available")
    public ResponseEntity<Boolean> checkDoctorAvailablity(
            @RequestParam Long doctorId,
            @RequestParam String date,
            @RequestParam String time
    ){
        boolean available=appointmentService.isDoctorAvailable(
                doctorId, LocalDate.parse(date), LocalTime.parse(time)
        );
        return ResponseEntity.ok(available);
    }
    //  Randevuyu sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable long id){
        appointmentService.deteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    //Doktorun musait randevu saatlerini dondurur
    @GetMapping("/doctor/{id}/date")
    public ResponseEntity<List<Appointments>> getAppointmentsByDoctorAndDate(
            @PathVariable Long id,
            @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Appointments> appointments = appointmentService.getAppointmentsByDoctorIdAndDate(id, localDate);
        return ResponseEntity.ok(appointments);
    }

}
