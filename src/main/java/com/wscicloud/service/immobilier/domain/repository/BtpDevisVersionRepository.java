package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpDevisVersion;

@Repository
public interface BtpDevisVersionRepository extends JpaRepository<BtpDevisVersion, Long> {

    List<BtpDevisVersion> findByProjetIdOrderByNumeroVersionDesc(Long projetId);
}
