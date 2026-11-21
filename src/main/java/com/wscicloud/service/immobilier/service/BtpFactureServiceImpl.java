package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureCompletDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFacturePaiementDto;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;
import com.wscicloud.service.immobilier.domain.entities.BtpFacturePaiement;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.mapper.BtpFactureLigneMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpFactureMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpFacturePaiementMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpFacturePaiementRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationTravauxRepository;
import com.wscicloud.service.immobilier.service.calcul.BtpDevisCalculator;
import com.wscicloud.service.immobilier.service.calcul.BtpFactureCalculator;
import com.wscicloud.service.immobilier.service.calcul.BtpMontants;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Getter
@Setter
@Service
@Slf4j
public class BtpFactureServiceImpl implements BtpFactureService {

    @Autowired
    private BtpFactureRepository factureRepository;

    @Autowired
    private BtpFactureLigneRepository ligneRepository;

    @Autowired
    private BtpFacturePaiementRepository paiementRepository;

    @Autowired
    private BtpFactureMapper factureMapper;

    @Autowired
    private BtpFactureLigneMapper ligneMapper;

    @Autowired
    private BtpFacturePaiementMapper paiementMapper;

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpSituationTravauxRepository situationRepository;

    @Autowired
    private BtpSituationLigneRepository situationLigneRepository;

    @Autowired
    private BtpLigneDevisRepository ligneDevisRepository;

    @Override
    public BtpFactureCompletDto save(BtpFactureCompletDto dto) {
        BtpFactureDto f = dto.getFacture();
        BtpProjet projet = projetRepository
                .findById(f.getProjetId())
                .orElseThrow(() -> new IllegalArgumentException("Projet BTP introuvable : " + f.getProjetId()));
        if (f.getTvaTaux() == null) {
            f.setTvaTaux(projet.getTvaTaux());
        }
        if (f.getRetenueGarantieTaux() == null) {
            f.setRetenueGarantieTaux(projet.getRetenueGarantieTaux());
        }
        if (f.getAvanceTaux() == null) {
            f.setAvanceTaux(projet.getAvanceTaux());
        }
        if (f.getStatut() == null) {
            f.setStatut("BROUILLON");
        }

        List<BtpFactureLigneDto> lignes = dto.getLignes() == null ? new ArrayList<>() : dto.getLignes();
        BigDecimal totalLignes = BigDecimal.ZERO;
        for (BtpFactureLigneDto l : lignes) {
            l.setMontantHt(BtpDevisCalculator.montantLigne(l.getQuantite(), l.getPrixUnitaire()));
            totalLignes = totalLignes.add(l.getMontantHt());
        }
        if (!lignes.isEmpty()) {
            f.setMontantHt(BtpMontants.montant(totalLignes));
        }

        BigDecimal paye = BigDecimal.ZERO;
        if (f.getId() != null) {
            paye = sommePaiements(f.getId());
        }
        appliquerTotaux(f);
        f.setMontantPaye(paye);

        BtpFacture saved = factureRepository.save(factureMapper.dtoToModele(f));
        if (f.getId() != null) {
            ligneRepository.deleteAll(ligneRepository.findByFactureIdOrderByOrdre(saved.getId()));
        }
        int ordre = 1;
        for (BtpFactureLigneDto l : lignes) {
            l.setId(null);
            l.setFactureId(saved.getId());
            l.setOrdre(ordre++);
            ligneRepository.save(ligneMapper.dtoToModele(l));
        }
        return findById(saved.getId());
    }

    private static void appliquerTotaux(BtpFactureDto f) {
        BtpFactureCalculator.Totaux t = BtpFactureCalculator.calculer(
                f.getMontantHt(), f.getTvaTaux(), f.getRetenueGarantieTaux(), f.getAvanceTaux());
        f.setMontantHt(t.montantHt());
        f.setMontantTva(t.montantTva());
        f.setMontantTtc(t.montantTtc());
        f.setRetenueGarantieMontant(t.retenueGarantie());
        f.setAvanceMontant(t.avance());
        f.setNetAPayer(t.netAPayer());
    }

    @Override
    public BtpFactureCompletDto genererDepuisSituation(Long situationId, String numero) {
        BtpSituationTravaux situation = situationRepository
                .findById(situationId)
                .orElseThrow(() -> new IllegalArgumentException("Situation introuvable : " + situationId));
        if ("BROUILLON".equals(situation.getStatut())) {
            throw new IllegalStateException("La situation doit etre validee avant facturation");
        }

        BtpFactureCompletDto dto = new BtpFactureCompletDto();
        BtpFactureDto f = new BtpFactureDto();
        f.setProjetId(situation.getProjetId());
        f.setSituationId(situationId);
        f.setNumero(numero);
        f.setDateFacture(java.time.LocalDate.now());
        f.setObjet("Situation de travaux n°" + situation.getNumero() + " - " + situation.getMois() + "/"
                + situation.getAnnee());
        dto.setFacture(f);

        for (BtpSituationLigne sl : situationLigneRepository.findBySituationId(situationId)) {
            if (BtpMontants.nz(sl.getMontantPeriodeHt()).signum() == 0) {
                continue;
            }
            BtpLigneDevis ld =
                    ligneDevisRepository.findById(sl.getLigneDevisId()).orElse(null);
            BtpFactureLigneDto l = new BtpFactureLigneDto();
            l.setLigneDevisId(sl.getLigneDevisId());
            l.setDesignation(ld == null ? "Ligne " + sl.getLigneDevisId() : ld.getDesignation());
            l.setUnite("fft");
            l.setQuantite(BigDecimal.ONE);
            l.setPrixUnitaire(sl.getMontantPeriodeHt());
            dto.getLignes().add(l);
        }
        if (BtpMontants.nz(situation.getRemisePeriode()).signum() > 0) {
            BtpFactureLigneDto remise = new BtpFactureLigneDto();
            remise.setDesignation("Remise");
            remise.setUnite("fft");
            remise.setQuantite(BigDecimal.ONE);
            remise.setPrixUnitaire(situation.getRemisePeriode().negate());
            dto.getLignes().add(remise);
        }
        return save(dto);
    }

    @Override
    public BtpFactureCompletDto findById(Long id) {
        return factureRepository.findById(id).map(this::complet).orElse(null);
    }

    private BtpFactureCompletDto complet(BtpFacture facture) {
        BtpFactureCompletDto dto = new BtpFactureCompletDto();
        dto.setFacture(factureMapper.modeleToDto(facture));
        dto.setLignes(ligneMapper.modeleToDto(ligneRepository.findByFactureIdOrderByOrdre(facture.getId())));
        dto.setPaiements(
                paiementMapper.modeleToDto(paiementRepository.findByFactureIdOrderByDatePaiement(facture.getId())));
        return dto;
    }

    @Override
    public List<BtpFactureCompletDto> findByProjet(Long projetId) {
        return factureRepository.findByProjetIdOrderByDateFactureDesc(projetId).stream()
                .map(this::complet)
                .toList();
    }

    private BigDecimal sommePaiements(Long factureId) {
        return BtpMontants.somme(paiementRepository.findByFactureIdOrderByDatePaiement(factureId).stream()
                .map(BtpFacturePaiement::getMontant)
                .toList());
    }

    private void majPaiements(BtpFacture facture) {
        BigDecimal paye = sommePaiements(facture.getId());
        facture.setMontantPaye(paye);
        if (!"BROUILLON".equals(facture.getStatut()) || paye.signum() > 0) {
            facture.setStatut(BtpFactureCalculator.statutPaiement(facture.getNetAPayer(), paye));
        }
        factureRepository.save(facture);
    }

    @Override
    public BtpFactureCompletDto ajouterPaiement(Long factureId, BtpFacturePaiementDto paiement) {
        BtpFacture facture = factureRepository
                .findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable : " + factureId));
        if (paiement.getMontant() == null || paiement.getMontant().signum() <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit etre positif");
        }
        paiement.setId(null);
        paiement.setFactureId(factureId);
        if (paiement.getDatePaiement() == null) {
            paiement.setDatePaiement(java.time.LocalDate.now());
        }
        paiement.setMontant(BtpMontants.montant(paiement.getMontant()));
        paiementRepository.save(paiementMapper.dtoToModele(paiement));
        majPaiements(facture);
        return complet(facture);
    }

    @Override
    public BtpFactureCompletDto supprimerPaiement(Long paiementId) {
        BtpFacturePaiement paiement = paiementRepository
                .findById(paiementId)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable : " + paiementId));
        BtpFacture facture = factureRepository.findById(paiement.getFactureId()).orElseThrow();
        paiementRepository.delete(paiement);
        majPaiements(facture);
        return complet(facture);
    }

    @Override
    public void delete(Long id) {
        factureRepository.deleteById(id);
    }

    @Override
    public BigDecimal resteAPayer(Long factureId) {
        BtpFacture facture = factureRepository
                .findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture introuvable : " + factureId));
        return BtpFactureCalculator.resteAPayer(facture.getNetAPayer(), sommePaiements(factureId));
    }
}
