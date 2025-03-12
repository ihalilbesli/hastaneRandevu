package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column
    private String birthDate;

    @Column
    private String bloodTyper;  //(hasta icin)

    @Column
    private String chronicDiseases; //kronik rahatsizlik (hasta icin)

    @Column
    private String specialization; //uzmanlik (doktor icin)


    public enum Role {
        HASTA, DOKTOR, ADMIN
    }
    public enum Gender{
        ERKEK,KADIN,BELIRTILMEMIS
    }
}
