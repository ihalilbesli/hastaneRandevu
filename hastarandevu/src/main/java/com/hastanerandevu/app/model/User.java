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


    @Column(nullable = false)
    private String gender;

    @Column
    private LocalDate birthDate;

    @Column
    private String bloodTyper;  //(hasta icin)

    @Column
    private String chronicDiseases; //kronik rahatsizlik (hasta icin)

    @Column
    private String specialization; //uzmanlik (doktor icin)

    public enum Role {
        HASTA, DOKTOR, ADMIN
    }

    @PrePersist
    protected void onCreate() {
        this.birthDate = generateRandomBirthDate();
}
private LocalDate generateRandomBirthDate(){
    // Rastgele bir tarih oluştur (1950 ile 2022 arası)
    long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
    long maxDay = LocalDate.of(2020, 12, 31).toEpochDay();
    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);

    return LocalDate.ofEpochDay(randomDay);
    }
}
