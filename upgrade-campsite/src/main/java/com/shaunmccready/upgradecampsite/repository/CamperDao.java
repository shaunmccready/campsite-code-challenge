package com.shaunmccready.upgradecampsite.repository;

import com.shaunmccready.upgradecampsite.domain.Camper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CamperDao extends JpaRepository<Camper, String> {

    Optional<Camper> findByEmail(String email);
}
