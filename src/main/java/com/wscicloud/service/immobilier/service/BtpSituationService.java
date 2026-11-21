package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationCompleteDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationTravauxDto;

public interface BtpSituationService {

    /** Genere la situation du mois a partir de la precedente (M-1 repris automatiquement). */
    BtpSituationCompleteDto generer(Long projetId, Integer mois, Integer annee);

    BtpSituationCompleteDto findById(Long situationId);

    List<BtpSituationTravauxDto> findByProjet(Long projetId);

    /** Saisit l'avancement cumule par ligne puis recalcule la situation. */
    BtpSituationCompleteDto saisirAvancement(Long situationId, List<BtpSituationLigneDto> lignes);

    BtpSituationCompleteDto recalculer(Long situationId);

    BtpSituationCompleteDto valider(Long situationId);

    BtpSituationCompleteDto marquerFacturee(Long situationId);

    void delete(Long situationId);
}
