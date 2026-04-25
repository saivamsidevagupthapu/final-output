package com.stablespringbootproject.repository;

import com.stablespringbootproject.Entity.Countryentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Countryrepo extends JpaRepository<Countryentity, Long> {
    Optional<Countryentity> findByCountryCode(String countryCode);
}