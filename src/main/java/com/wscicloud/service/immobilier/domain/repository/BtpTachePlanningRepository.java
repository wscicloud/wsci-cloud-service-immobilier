package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpTachePlanning;

@Repository
public interface BtpTachePlanningRepository extends JpaRepository<BtpTachePlanning, Long> {

    List<BtpTachePlanning> findByProjetIdOrderByDateDebut(Long projetId);
}
