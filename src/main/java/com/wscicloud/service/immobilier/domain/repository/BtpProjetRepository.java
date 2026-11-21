package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpProjet;

@Repository
public interface BtpProjetRepository extends JpaRepository<BtpProjet, Long> {

    List<BtpProjet> findBySocieteIdOrderByNom(Long societeId);
}
