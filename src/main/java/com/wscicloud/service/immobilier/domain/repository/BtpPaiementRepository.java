package com.wscicloud.service.immobilier.domain.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpPaiement;

@Repository
public interface BtpPaiementRepository extends JpaRepository<BtpPaiement, Long> {

    List<BtpPaiement> findByFactureIdOrderByDatePaiementAscIdAsc(Long factureId);

    List<BtpPaiement> findByFactureIdIn(Collection<Long> factureIds);
}
