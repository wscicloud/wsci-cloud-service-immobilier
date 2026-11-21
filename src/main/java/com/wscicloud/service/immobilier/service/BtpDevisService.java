package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;

public interface BtpDevisService {

    List<BtpDevisDto> listerParProjet(Long projetId);

    BtpDevisDto lire(Long devisId);

    /** Cree un devis, avec ses lignes eventuelles, et calcule ses totaux. */
    BtpDevisDto creer(BtpDevisDto dto);

    /** Modifie l'entete du devis (les lignes sont ignorees) et recalcule le TTC. */
    BtpDevisDto modifier(BtpDevisDto dto);

    void supprimer(Long devisId);

    BtpDevisDto changerStatut(Long devisId, String statut);

    BtpDevisDto ajouterLigne(Long devisId, BtpLigneDevisDto ligne);

    BtpDevisDto modifierLigne(Long devisId, Long ligneId, BtpLigneDevisDto ligne);

    BtpDevisDto supprimerLigne(Long devisId, Long ligneId);
}
