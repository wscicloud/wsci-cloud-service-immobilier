package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpRapportJournalier;

@Repository
public interface BtpRapportJournalierRepository extends JpaRepository<BtpRapportJournalier, Long> {

    List<BtpRapportJournalier> findByProjetIdOrderByDateRapportDesc(Long projetId);
}
