package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpPosteDevis;

@Repository
public interface BtpPosteDevisRepository extends JpaRepository<BtpPosteDevis, Long> {

    List<BtpPosteDevis> findByLotIdOrderByOrdre(Long lotId);
}
