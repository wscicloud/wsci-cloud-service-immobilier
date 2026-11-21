package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureCompletDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureDto;
import com.wscicloud.service.immobilier.domain.dto.BtpPaiementDto;

public interface BtpFactureService {

    List<BtpFactureDto> listerParProjet(Long projetId);

    BtpFactureCompletDto lire(Long factureId);

    /** Cree une facture (statut EMISE) ; si des lignes sont fournies, HT = somme des lignes. */
    BtpFactureCompletDto creer(BtpFactureCompletDto dto);

    /** Cree une facture a partir des lignes d'une situation VALIDEE, qui passe a FACTUREE. */
    BtpFactureCompletDto creerDepuisSituation(Long situationId, String numeroFacture, BigDecimal tauxTva);

    /** Enregistre un paiement et met a jour le statut de la facture. */
    BtpFactureCompletDto enregistrerPaiement(Long factureId, BtpPaiementDto paiement);

    BtpFactureCompletDto supprimerPaiement(Long paiementId);

    BigDecimal resteAPayer(Long factureId);

    BtpFactureCompletDto annuler(Long factureId);

    void supprimer(Long factureId);
}
