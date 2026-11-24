package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.mapper.BtpDevisMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpLigneDevisMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.service.calcul.BtpDevisCalculator;
import com.wscicloud.service.immobilier.service.calcul.BtpStatuts;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class BtpDevisServiceImpl implements BtpDevisService {

    @Autowired
    private BtpDevisRepository devisRepository;

    @Autowired
    private BtpLigneDevisRepository ligneRepository;

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpDevisMapper devisMapper;

    @Autowired
    private BtpLigneDevisMapper ligneMapper;

    @Override
    public List<BtpDevisDto> listerParProjet(Long projetId) {
        return devisMapper.modeleToDto(devisRepository.findByProjetIdOrderByDateDevisDescIdDesc(projetId));
    }

    @Override
    public BtpDevisDto lire(Long devisId) {
        return versDtoComplet(trouverDevis(devisId));
    }

    @Override
    public BtpDevisDto creer(BtpDevisDto dto) {
        if (dto.getProjetId() == null || !projetRepository.existsById(dto.getProjetId())) {
            throw new IllegalArgumentException("Projet introuvable : " + dto.getProjetId());
        }
        if (devisRepository.existsByNumeroDevis(dto.getNumeroDevis())) {
            throw new IllegalArgumentException("Le numero de devis existe deja : " + dto.getNumeroDevis());
        }
        BtpDevis devis = devisMapper.dtoToModele(dto);
        devis.setId(null);
        devis.setCreeLe(OffsetDateTime.now());
        if (devis.getDateDevis() == null) {
            devis.setDateDevis(LocalDate.now());
        }
        if (devis.getStatut() == null) {
            devis.setStatut(BtpStatuts.DEVIS_BROUILLON);
        }
        if (devis.getTauxTva() == null) {
            devis.setTauxTva(BigDecimal.ZERO);
        }
        devis.setMontantHt(BigDecimal.ZERO);
        devis.setMontantTtc(BigDecimal.ZERO);
        BtpDevis sauve = devisRepository.save(devis);
        if (dto.getLignes() != null) {
            for (BtpLigneDevisDto ligneDto : dto.getLignes()) {
                ligneRepository.save(construireLigne(sauve.getId(), ligneDto, null));
            }
        }
        return recalculer(sauve);
    }

    @Override
    public BtpDevisDto modifier(BtpDevisDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("L'identifiant du devis est obligatoire");
        }
        BtpDevis existant = trouverDevis(dto.getId());
        if (!existant.getNumeroDevis().equals(dto.getNumeroDevis())
                && devisRepository.existsByNumeroDevis(dto.getNumeroDevis())) {
            throw new IllegalArgumentException("Le numero de devis existe deja : " + dto.getNumeroDevis());
        }
        existant.setNumeroDevis(dto.getNumeroDevis());
        if (dto.getDateDevis() != null) {
            existant.setDateDevis(dto.getDateDevis());
        }
        if (dto.getTauxTva() != null) {
            existant.setTauxTva(dto.getTauxTva());
        }
        existant.setNotes(dto.getNotes());
        return recalculer(existant);
    }

    @Override
    public void supprimer(Long devisId) {
        BtpDevis devis = trouverDevis(devisId);
        ligneRepository.deleteByDevisId(devisId);
        devisRepository.delete(devis);
    }

    @Override
    public BtpDevisDto changerStatut(Long devisId, String statut) {
        if (statut == null || !BtpStatuts.DEVIS_AUTORISES.contains(statut)) {
            throw new IllegalArgumentException("Statut de devis invalide : " + statut);
        }
        BtpDevis devis = trouverDevis(devisId);
        devis.setStatut(statut);
        return versDtoComplet(devisRepository.save(devis));
    }

    @Override
    public BtpDevisDto ajouterLigne(Long devisId, BtpLigneDevisDto ligne) {
        BtpDevis devis = trouverDevis(devisId);
        ligneRepository.save(construireLigne(devisId, ligne, null));
        return recalculer(devis);
    }

    @Override
    public BtpDevisDto modifierLigne(Long devisId, Long ligneId, BtpLigneDevisDto ligne) {
        BtpDevis devis = trouverDevis(devisId);
        BtpLigneDevis existante = trouverLigne(devisId, ligneId);
        ligneRepository.save(construireLigne(devisId, ligne, existante));
        return recalculer(devis);
    }

    @Override
    public BtpDevisDto supprimerLigne(Long devisId, Long ligneId) {
        BtpDevis devis = trouverDevis(devisId);
        ligneRepository.delete(trouverLigne(devisId, ligneId));
        return recalculer(devis);
    }

    private BtpLigneDevis construireLigne(Long devisId, BtpLigneDevisDto dto, BtpLigneDevis existante) {
        if (dto.getQuantite() != null && dto.getQuantite().signum() < 0) {
            throw new IllegalArgumentException("La quantite ne peut pas etre negative");
        }
        if (dto.getPrixUnitaireHt() != null && dto.getPrixUnitaireHt().signum() < 0) {
            throw new IllegalArgumentException("Le prix unitaire ne peut pas etre negatif");
        }
        BtpLigneDevis ligne = ligneMapper.dtoToModele(dto);
        ligne.setId(existante == null ? null : existante.getId());
        ligne.setCreeLe(existante == null ? OffsetDateTime.now() : existante.getCreeLe());
        ligne.setDevisId(devisId);
        if (ligne.getOrdreAffichage() == null) {
            ligne.setOrdreAffichage(
                    existante != null && existante.getOrdreAffichage() != null
                            ? existante.getOrdreAffichage()
                            : ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(devisId).stream()
                                            .map(BtpLigneDevis::getOrdreAffichage)
                                            .filter(java.util.Objects::nonNull)
                                            .max(Integer::compare)
                                            .orElse(0)
                                    + 1);
        }
        ligne.setMontantHt(BtpDevisCalculator.montantLigne(dto.getQuantite(), dto.getPrixUnitaireHt()));
        return ligne;
    }

    /** Recalcule HT (somme des lignes) et TTC du devis, le persiste et renvoie le devis complet. */
    private BtpDevisDto recalculer(BtpDevis devis) {
        List<BtpLigneDevis> lignes = ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(devis.getId());
        BtpDevisCalculator.Totaux totaux = BtpDevisCalculator.totaux(
                lignes.stream().map(BtpLigneDevis::getMontantHt).toList(), devis.getTauxTva());
        devis.setMontantHt(totaux.montantHt());
        devis.setMontantTtc(totaux.montantTtc());
        BtpDevis sauve = devisRepository.save(devis);
        BtpDevisDto dto = devisMapper.modeleToDto(sauve);
        dto.setLignes(ligneMapper.modeleToDto(lignes));
        return dto;
    }

    private BtpDevisDto versDtoComplet(BtpDevis devis) {
        BtpDevisDto dto = devisMapper.modeleToDto(devis);
        dto.setLignes(
                ligneMapper.modeleToDto(ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(devis.getId())));
        return dto;
    }

    private BtpDevis trouverDevis(Long id) {
        return devisRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Devis introuvable : " + id));
    }

    private BtpLigneDevis trouverLigne(Long devisId, Long ligneId) {
        BtpLigneDevis ligne = ligneRepository
                .findById(ligneId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de devis introuvable : " + ligneId));
        if (!devisId.equals(ligne.getDevisId())) {
            throw new IllegalArgumentException("La ligne " + ligneId + " n'appartient pas au devis " + devisId);
        }
        return ligne;
    }
}
