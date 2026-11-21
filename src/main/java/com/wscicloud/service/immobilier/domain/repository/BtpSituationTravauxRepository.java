package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;

@Repository
public interface BtpSituationTravauxRepository extends JpaRepository<BtpSituationTravaux, Long> {

    List<BtpSituationTravaux> findByProjetIdOrderByNumeroDesc(Long projetId);
}
