package com.hastanerandevu.app.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
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

    @ManyToOne
    @JoinColumn(name = "clinic_id") //clinic (doktor icin)
    private Clinic clinic;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() ->"ROLE_"+ role.name());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
         return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


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
