package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpDepenseReelle;

@Repository
public interface BtpDepenseReelleRepository extends JpaRepository<BtpDepenseReelle, Long> {

    List<BtpDepenseReelle> findByProjetIdOrderByDateDepenseDesc(Long projetId);

    List<BtpDepenseReelle> findBySocieteId(Long societeId);
}
