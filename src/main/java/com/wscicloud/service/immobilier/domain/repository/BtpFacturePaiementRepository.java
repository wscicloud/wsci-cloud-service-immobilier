package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpFacturePaiement;

@Repository
public interface BtpFacturePaiementRepository extends JpaRepository<BtpFacturePaiement, Long> {

    List<BtpFacturePaiement> findByFactureIdOrderByDatePaiement(Long factureId);
}
