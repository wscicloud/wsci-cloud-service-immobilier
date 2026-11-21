package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpLotDevis;

@Repository
public interface BtpLotDevisRepository extends JpaRepository<BtpLotDevis, Long> {

    List<BtpLotDevis> findByProjetIdOrderByOrdre(Long projetId);
}
