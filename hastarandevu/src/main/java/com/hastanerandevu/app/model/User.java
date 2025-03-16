package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Enumerated(EnumType.STRING)
    @Column
    private Bloodtype  bloodType;  //(hasta icin)

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
    public enum Bloodtype{
        ARH_POS, ARH_NEG, BRH_POS, BRH_NEG, ABRH_POS, ABRH_NEG, ORH_POS, ORH_NEG
    }

}
