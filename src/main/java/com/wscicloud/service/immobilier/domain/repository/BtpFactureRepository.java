package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpFacture;

@Repository
public interface BtpFactureRepository extends JpaRepository<BtpFacture, Long> {

    List<BtpFacture> findByProjetIdOrderByDateEmissionDescIdDesc(Long projetId);

    boolean existsByNumeroFacture(String numeroFacture);
}
