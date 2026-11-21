package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureCompletDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFacturePaiementDto;

public interface BtpFactureService {

    /** Cree ou met a jour la facture avec ses lignes ; les montants sont recalcules. */
    BtpFactureCompletDto save(BtpFactureCompletDto dto);

    /** Genere une facture a partir des lignes de la situation (production M). */
    BtpFactureCompletDto genererDepuisSituation(Long situationId, String numero);

    BtpFactureCompletDto findById(Long id);

    List<BtpFactureCompletDto> findByProjet(Long projetId);

    BtpFactureCompletDto ajouterPaiement(Long factureId, BtpFacturePaiementDto paiement);

    BtpFactureCompletDto supprimerPaiement(Long paiementId);

    void delete(Long id);

    BigDecimal resteAPayer(Long factureId);
}
