package com.wscicloud.service.immobilier.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpSousDetailPrix;

@Repository
public interface BtpSousDetailPrixRepository extends JpaRepository<BtpSousDetailPrix, Long> {

    Optional<BtpSousDetailPrix> findByLigneId(Long ligneId);
}
