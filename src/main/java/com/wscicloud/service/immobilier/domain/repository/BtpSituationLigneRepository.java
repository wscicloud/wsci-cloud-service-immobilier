package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;

@Repository
public interface BtpSituationLigneRepository extends JpaRepository<BtpSituationLigne, Long> {

    List<BtpSituationLigne> findBySituationId(Long situationId);
}
