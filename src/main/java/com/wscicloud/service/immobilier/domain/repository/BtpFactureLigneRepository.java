package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpFactureLigne;

@Repository
public interface BtpFactureLigneRepository extends JpaRepository<BtpFactureLigne, Long> {

    List<BtpFactureLigne> findByFactureIdOrderByOrdre(Long factureId);
}
