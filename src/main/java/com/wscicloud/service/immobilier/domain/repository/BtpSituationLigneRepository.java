package com.wscicloud.service.immobilier.domain.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;

@Repository
public interface BtpSituationLigneRepository extends JpaRepository<BtpSituationLigne, Long> {

    List<BtpSituationLigne> findBySituationIdOrderByIdAsc(Long situationId);

    List<BtpSituationLigne> findBySituationIdIn(Collection<Long> situationIds);

    boolean existsBySituationIdAndLigneDevisId(Long situationId, Long ligneDevisId);

    void deleteBySituationId(Long situationId);
}
