package com.wscicloud.service.immobilier.domain.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpDevis;

@Repository
public interface BtpDevisRepository extends JpaRepository<BtpDevis, Long> {

    List<BtpDevis> findByProjetIdOrderByDateDevisDescIdDesc(Long projetId);

    List<BtpDevis> findByProjetIdAndStatutIn(Long projetId, Collection<String> statuts);

    boolean existsByNumeroDevis(String numeroDevis);
}
