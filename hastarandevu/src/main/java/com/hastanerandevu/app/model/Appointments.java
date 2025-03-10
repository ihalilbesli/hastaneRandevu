package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column
    private LocalDate date;

    @Column
    private LocalTime minute;

    @Column(columnDefinition = "Text")
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status=Status.AKTIF;



    public enum Status{
    AKTIF,IPTAL_EDILDI
    }

    @PrePersist
    @PreUpdate
    protected void formatTime(){
        if(this.minute !=null){
            this.minute=this.minute.withSecond(0).withNano(0);
        }
    }

}
