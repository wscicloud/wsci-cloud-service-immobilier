package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpDevisVersionDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLotDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpPosteDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSousDetailPrixDto;

public interface BtpDevisService {

    BtpDevisDto findDevis(Long projetId);

    /** Recalcule et persiste le montant de chaque ligne (quantite x PU) puis retourne le devis. */
    BtpDevisDto recalculer(Long projetId);

    BtpLotDevisDto saveLot(BtpLotDevisDto dto);

    void deleteLot(Long id);

    BtpPosteDevisDto savePoste(BtpPosteDevisDto dto);

    void deletePoste(Long id);

    BtpLigneDevisDto saveLigne(BtpLigneDevisDto dto);

    void deleteLigne(Long id);

    BtpSousDetailPrixDto saveSousDetail(BtpSousDetailPrixDto dto);

    BtpSousDetailPrixDto findSousDetail(Long ligneId);

    BtpProjetDto dupliquer(Long projetId);

    BtpDevisVersionDto nouvelleVersion(Long projetId, String commentaire);

    List<BtpDevisVersionDto> findVersions(Long projetId);

    BtpProjetDto changerStatut(Long projetId, String statut);

    byte[] exportPdf(Long projetId);

    byte[] exportExcel(Long projetId);
}
