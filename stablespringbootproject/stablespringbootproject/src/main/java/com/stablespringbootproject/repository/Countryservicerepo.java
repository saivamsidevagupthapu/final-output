package com.stablespringbootproject.repository;

import com.stablespringbootproject.Entity.Countryserviceentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Countryservicerepo extends JpaRepository<Countryserviceentity, Long> {
    Optional<Countryserviceentity> findFirstByCountryCodeAndActiveTrue(String countryCode);

	List<Countryserviceentity> findByActiveTrue();
}