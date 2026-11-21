package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureCompletDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpPaiementDto;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;
import com.wscicloud.service.immobilier.domain.entities.BtpFactureLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpPaiement;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.mapper.BtpFactureLigneMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpFactureMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpPaiementMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpPaiementRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationTravauxRepository;
import com.wscicloud.service.immobilier.service.calcul.BtpFactureCalculator;
import com.wscicloud.service.immobilier.service.calcul.BtpMontants;
import com.wscicloud.service.immobilier.service.calcul.BtpStatuts;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class BtpFactureServiceImpl implements BtpFactureService {

    static final String MODE_PAIEMENT_DEFAUT = "VIREMENT";

    @Autowired
    private BtpFactureRepository factureRepository;

    @Autowired
    private BtpFactureLigneRepository ligneRepository;

    @Autowired
    private BtpPaiementRepository paiementRepository;

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpSituationTravauxRepository situationRepository;

    @Autowired
    private BtpSituationLigneRepository situationLigneRepository;

    @Autowired
    private BtpLigneDevisRepository ligneDevisRepository;

    @Autowired
    private BtpFactureMapper factureMapper;

    @Autowired
    private BtpFactureLigneMapper ligneMapper;

    @Autowired
    private BtpPaiementMapper paiementMapper;

    @Override
    public List<BtpFactureDto> listerParProjet(Long projetId) {
        return factureMapper.modeleToDto(factureRepository.findByProjetIdOrderByDateEmissionDescIdDesc(projetId));
    }

    @Override
    public BtpFactureCompletDto lire(Long factureId) {
        return versComplet(trouverFacture(factureId));
    }

    @Override
    public BtpFactureCompletDto creer(BtpFactureCompletDto dto) {
        BtpFactureDto entete = dto.getFacture();
        if (entete == null || entete.getProjetId() == null || !projetRepository.existsById(entete.getProjetId())) {
            throw new IllegalArgumentException(
                    "Projet introuvable : " + (entete == null ? null : entete.getProjetId()));
        }
        if (factureRepository.existsByNumeroFacture(entete.getNumeroFacture())) {
            throw new IllegalArgumentException("Le numero de facture existe deja : " + entete.getNumeroFacture());
        }
        List<BtpFactureLigne> lignes = new ArrayList<>();
        if (dto.getLignes() != null) {
            for (BtpFactureLigneDto ligneDto : dto.getLignes()) {
                lignes.add(construireLigne(ligneDto));
            }
        }

        BtpFacture facture = factureMapper.dtoToModele(entete);
        facture.setId(null);
        facture.setCreeLe(OffsetDateTime.now());
        if (facture.getDateEmission() == null) {
            facture.setDateEmission(LocalDate.now());
        }
        if (facture.getTauxTva() == null) {
            facture.setTauxTva(BigDecimal.ZERO);
        }
        BigDecimal ht = lignes.isEmpty()
                ? BtpMontants.montant(entete.getMontantHt())
                : BtpMontants.somme(
                        lignes.stream().map(BtpFactureLigne::getMontantHt).toList());
        facture.setMontantHt(ht);
        facture.setMontantTtc(BtpFactureCalculator.montantTtc(ht, facture.getTauxTva()));
        facture.setStatut(BtpStatuts.FACTURE_EMISE);
        BtpFacture sauvee = factureRepository.save(facture);
        for (BtpFactureLigne ligne : lignes) {
            ligne.setFactureId(sauvee.getId());
            ligneRepository.save(ligne);
        }
        return versComplet(sauvee);
    }

    @Override
    public BtpFactureCompletDto creerDepuisSituation(Long situationId, String numeroFacture, BigDecimal tauxTva) {
        BtpSituationTravaux situation = situationRepository
                .findById(situationId)
                .orElseThrow(() -> new IllegalArgumentException("Situation introuvable : " + situationId));
        if (!BtpStatuts.SITUATION_VALIDEE.equals(situation.getStatut())) {
            throw new IllegalArgumentException("Seule une situation VALIDEE peut etre facturee");
        }
        List<BtpSituationLigne> lignesSituation = situationLigneRepository.findBySituationIdOrderByIdAsc(situationId);
        Map<Long, BtpLigneDevis> lignesDevis =
                ligneDevisRepository
                        .findAllById(lignesSituation.stream()
                                .map(BtpSituationLigne::getLigneDevisId)
                                .toList())
                        .stream()
                        .collect(Collectors.toMap(BtpLigneDevis::getId, Function.identity()));

        BtpFactureDto entete = new BtpFactureDto();
        entete.setProjetId(situation.getProjetId());
        entete.setSituationId(situationId);
        entete.setNumeroFacture(numeroFacture);
        entete.setTypeFacture(BtpStatuts.TYPE_FACTURE_SITUATION);
        entete.setTauxTva(tauxTva);

        BtpFactureCompletDto demande = new BtpFactureCompletDto();
        demande.setFacture(entete);
        for (BtpSituationLigne sl : lignesSituation) {
            BtpLigneDevis ld = lignesDevis.get(sl.getLigneDevisId());
            BtpFactureLigneDto ligne = new BtpFactureLigneDto();
            ligne.setLigneDevisId(sl.getLigneDevisId());
            ligne.setSituationLigneId(sl.getId());
            ligne.setDesignation(ld == null ? "Ligne " + sl.getLigneDevisId() : ld.getDesignation());
            ligne.setUnite(ld == null ? null : ld.getUnite());
            ligne.setQuantite(sl.getQuantitePeriode());
            ligne.setPrixUnitaireHt(ld == null ? null : ld.getPrixUnitaireHt());
            ligne.setMontantHt(sl.getMontantPeriodeHt());
            demande.getLignes().add(ligne);
        }
        BtpFactureCompletDto creee = creer(demande);
        situation.setStatut(BtpStatuts.SITUATION_FACTUREE);
        situationRepository.save(situation);
        return creee;
    }

    @Override
    public BtpFactureCompletDto enregistrerPaiement(Long factureId, BtpPaiementDto dto) {
        BtpFacture facture = trouverFacture(factureId);
        if (BtpStatuts.FACTURE_ANNULEE.equals(facture.getStatut())) {
            throw new IllegalArgumentException("Impossible d'enregistrer un paiement sur une facture annulee");
        }
        if (dto.getMontant() == null || dto.getMontant().signum() <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit etre strictement positif");
        }
        BtpPaiement paiement = paiementMapper.dtoToModele(dto);
        paiement.setId(null);
        paiement.setFactureId(factureId);
        paiement.setMontant(BtpMontants.montant(dto.getMontant()));
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(OffsetDateTime.now());
        }
        if (paiement.getModePaiement() == null) {
            paiement.setModePaiement(MODE_PAIEMENT_DEFAUT);
        }
        paiementRepository.save(paiement);
        return versComplet(majStatut(facture));
    }

    @Override
    public BtpFactureCompletDto supprimerPaiement(Long paiementId) {
        BtpPaiement paiement = paiementRepository
                .findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable : " + paiementId));
        BtpFacture facture = trouverFacture(paiement.getFactureId());
        paiementRepository.delete(paiement);
        return versComplet(majStatut(facture));
    }

    @Override
    public BigDecimal resteAPayer(Long factureId) {
        BtpFacture facture = trouverFacture(factureId);
        return BtpFactureCalculator.resteAPayer(facture.getMontantTtc(), totalPaye(factureId));
    }

    @Override
    public BtpFactureCompletDto annuler(Long factureId) {
        BtpFacture facture = trouverFacture(factureId);
        facture.setStatut(BtpStatuts.FACTURE_ANNULEE);
        return versComplet(factureRepository.save(facture));
    }

    @Override
    public void supprimer(Long factureId) {
        BtpFacture facture = trouverFacture(factureId);
        ligneRepository.deleteByFactureId(factureId);
        factureRepository.delete(facture);
    }

    private BtpFactureLigne construireLigne(BtpFactureLigneDto dto) {
        if (dto.getQuantite() != null && dto.getQuantite().signum() < 0) {
            throw new IllegalArgumentException("La quantite ne peut pas etre negative");
        }
        BtpFactureLigne ligne = ligneMapper.dtoToModele(dto);
        ligne.setId(null);
        ligne.setCreeLe(OffsetDateTime.now());
        if (dto.getQuantite() != null && dto.getPrixUnitaireHt() != null) {
            ligne.setMontantHt(BtpFactureCalculator.montantLigne(dto.getQuantite(), dto.getPrixUnitaireHt()));
        } else {
            ligne.setMontantHt(BtpMontants.montant(dto.getMontantHt()));
        }
        return ligne;
    }

    /** Statut selon les paiements ; une facture ANNULEE le reste. */
    private BtpFacture majStatut(BtpFacture facture) {
        if (!BtpStatuts.FACTURE_ANNULEE.equals(facture.getStatut())) {
            facture.setStatut(
                    BtpFactureCalculator.statutSelonPaiements(facture.getMontantTtc(), totalPaye(facture.getId())));
        }
        return factureRepository.save(facture);
    }

    private BigDecimal totalPaye(Long factureId) {
        return BtpFactureCalculator.totalPaye(
                paiementRepository.findByFactureIdOrderByDatePaiementAscIdAsc(factureId).stream()
                        .map(BtpPaiement::getMontant)
                        .toList());
    }

    private BtpFactureCompletDto versComplet(BtpFacture facture) {
        List<BtpPaiement> paiements = paiementRepository.findByFactureIdOrderByDatePaiementAscIdAsc(facture.getId());
        BigDecimal paye = BtpFactureCalculator.totalPaye(
                paiements.stream().map(BtpPaiement::getMontant).toList());
        BtpFactureCompletDto complet = new BtpFactureCompletDto();
        complet.setFacture(factureMapper.modeleToDto(facture));
        complet.setLignes(ligneMapper.modeleToDto(ligneRepository.findByFactureIdOrderByIdAsc(facture.getId())));
        complet.setPaiements(paiementMapper.modeleToDto(paiements));
        complet.setTotalPaye(paye);
        complet.setResteAPayer(BtpFactureCalculator.resteAPayer(facture.getMontantTtc(), paye));
        return complet;
    }

    private BtpFacture trouverFacture(Long id) {
        return factureRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable : " + id));
    }
}
