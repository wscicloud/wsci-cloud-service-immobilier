package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationCompleteDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationTravauxDto;

public interface BtpSituationService {

    List<BtpSituationTravauxDto> listerParProjet(Long projetId);

    BtpSituationCompleteDto lire(Long situationId);

    BtpSituationTravauxDto creer(BtpSituationTravauxDto dto);

    /** Modifie l'entete de la situation et recalcule ses totaux. */
    BtpSituationTravauxDto modifier(BtpSituationTravauxDto dto);

    void supprimer(Long situationId);

    BtpSituationTravauxDto changerStatut(Long situationId, String statut);

    BtpSituationCompleteDto ajouterLigne(Long situationId, BtpSituationLigneDto ligne);

    BtpSituationCompleteDto modifierLigne(Long situationId, Long ligneId, BtpSituationLigneDto ligne);

    BtpSituationCompleteDto supprimerLigne(Long situationId, Long ligneId);
}
