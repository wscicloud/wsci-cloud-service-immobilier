package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpRapportJournalierDto;
import com.wscicloud.service.immobilier.domain.dto.BtpTachePlanningDto;

public interface BtpPlanningService {

    /** Enregistre la tache ; date_fin = date_debut + duree_jours - 1. */
    BtpTachePlanningDto saveTache(BtpTachePlanningDto dto);

    List<BtpTachePlanningDto> findTaches(Long projetId);

    void deleteTache(Long id);

    BtpRapportJournalierDto saveRapport(BtpRapportJournalierDto dto);

    List<BtpRapportJournalierDto> findRapports(Long projetId);

    void deleteRapport(Long id);
}
