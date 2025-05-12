package com.hastanerandevu.app.repository;

import com.hastanerandevu.app.model.Clinic;
import com.hastanerandevu.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic,Long> {
    Optional<Clinic> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);


}
