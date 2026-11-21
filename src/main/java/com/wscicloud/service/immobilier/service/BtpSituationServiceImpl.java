package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationCompleteDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationTravauxDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.mapper.BtpSituationLigneMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpSituationTravauxMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationTravauxRepository;
import com.wscicloud.service.immobilier.service.calcul.BtpDevisCalculator;
import com.wscicloud.service.immobilier.service.calcul.BtpSituationCalculator;
import com.wscicloud.service.immobilier.service.calcul.BtpStatuts;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class BtpSituationServiceImpl implements BtpSituationService {

    @Autowired
    private BtpSituationTravauxRepository situationRepository;

    @Autowired
    private BtpSituationLigneRepository ligneRepository;

    @Autowired
    private BtpLigneDevisRepository ligneDevisRepository;

    @Autowired
    private BtpDevisRepository devisRepository;

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpSituationTravauxMapper situationMapper;

    @Autowired
    private BtpSituationLigneMapper ligneMapper;

    @Override
    public List<BtpSituationTravauxDto> listerParProjet(Long projetId) {
        return situationMapper.modeleToDto(situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(projetId));
    }

    @Override
    public BtpSituationCompleteDto lire(Long situationId) {
        return versComplet(trouverSituation(situationId));
    }

    @Override
    public BtpSituationTravauxDto creer(BtpSituationTravauxDto dto) {
        if (dto.getProjetId() == null || !projetRepository.existsById(dto.getProjetId())) {
            throw new IllegalArgumentException("Projet introuvable : " + dto.getProjetId());
        }
        if (situationRepository.existsByProjetIdAndNumeroSituation(dto.getProjetId(), dto.getNumeroSituation())) {
            throw new IllegalArgumentException("Le numero de situation existe deja : " + dto.getNumeroSituation());
        }
        BtpSituationTravaux situation = situationMapper.dtoToModele(dto);
        situation.setId(null);
        situation.setCreeLe(OffsetDateTime.now());
        if (situation.getDateSituation() == null) {
            situation.setDateSituation(LocalDate.now());
        }
        if (situation.getStatut() == null) {
            situation.setStatut(BtpStatuts.SITUATION_BROUILLON);
        }
        situation.setMontantPeriodeHt(BigDecimal.ZERO);
        situation.setMontantCumuleHt(BigDecimal.ZERO);
        situation.setTauxAvancementPct(BigDecimal.ZERO);
        BtpSituationTravaux sauvee = situationRepository.save(situation);
        return situationMapper.modeleToDto(recalculer(sauvee));
    }

    @Override
    public BtpSituationTravauxDto modifier(BtpSituationTravauxDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("L'identifiant de la situation est obligatoire");
        }
        BtpSituationTravaux existante = trouverSituation(dto.getId());
        if (!existante.getNumeroSituation().equals(dto.getNumeroSituation())
                && situationRepository.existsByProjetIdAndNumeroSituation(
                        existante.getProjetId(), dto.getNumeroSituation())) {
            throw new IllegalArgumentException("Le numero de situation existe deja : " + dto.getNumeroSituation());
        }
        existante.setNumeroSituation(dto.getNumeroSituation());
        if (dto.getDateSituation() != null) {
            existante.setDateSituation(dto.getDateSituation());
        }
        existante.setPeriodeDebut(dto.getPeriodeDebut());
        existante.setPeriodeFin(dto.getPeriodeFin());
        existante.setObservations(dto.getObservations());
        return situationMapper.modeleToDto(recalculer(existante));
    }

    @Override
    public void supprimer(Long situationId) {
        BtpSituationTravaux situation = trouverSituation(situationId);
        ligneRepository.deleteBySituationId(situationId);
        situationRepository.delete(situation);
    }

    @Override
    public BtpSituationTravauxDto changerStatut(Long situationId, String statut) {
        if (statut == null || !BtpStatuts.SITUATION_AUTORISES.contains(statut)) {
            throw new IllegalArgumentException("Statut de situation invalide : " + statut);
        }
        BtpSituationTravaux situation = trouverSituation(situationId);
        situation.setStatut(statut);
        return situationMapper.modeleToDto(situationRepository.save(situation));
    }

    @Override
    public BtpSituationCompleteDto ajouterLigne(Long situationId, BtpSituationLigneDto dto) {
        BtpSituationTravaux situation = trouverSituation(situationId);
        if (ligneRepository.existsBySituationIdAndLigneDevisId(situationId, dto.getLigneDevisId())) {
            throw new IllegalArgumentException(
                    "La ligne de devis " + dto.getLigneDevisId() + " est deja presente dans la situation");
        }
        ligneRepository.save(construireLigne(situation, dto, null));
        return recalculerEtLire(situation);
    }

    @Override
    public BtpSituationCompleteDto modifierLigne(Long situationId, Long ligneId, BtpSituationLigneDto dto) {
        BtpSituationTravaux situation = trouverSituation(situationId);
        BtpSituationLigne existante = trouverLigne(situationId, ligneId);
        ligneRepository.save(construireLigne(situation, dto, existante));
        return recalculerEtLire(situation);
    }

    @Override
    public BtpSituationCompleteDto supprimerLigne(Long situationId, Long ligneId) {
        BtpSituationTravaux situation = trouverSituation(situationId);
        ligneRepository.delete(trouverLigne(situationId, ligneId));
        return recalculerEtLire(situation);
    }

    private BtpSituationLigne construireLigne(
            BtpSituationTravaux situation, BtpSituationLigneDto dto, BtpSituationLigne existante) {
        if (dto.getQuantitePeriode() != null && dto.getQuantitePeriode().signum() < 0) {
            throw new IllegalArgumentException("La quantite de la periode ne peut pas etre negative");
        }
        if (dto.getQuantitePrecedente() != null && dto.getQuantitePrecedente().signum() < 0) {
            throw new IllegalArgumentException("La quantite precedente ne peut pas etre negative");
        }
        BtpLigneDevis ligneDevis = ligneDevisRepository
                .findById(dto.getLigneDevisId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Ligne de devis introuvable : " + dto.getLigneDevisId()));
        BtpDevis devis = devisRepository
                .findById(ligneDevis.getDevisId())
                .orElseThrow(() -> new IllegalArgumentException("Devis introuvable : " + ligneDevis.getDevisId()));
        if (!situation.getProjetId().equals(devis.getProjetId())) {
            throw new IllegalArgumentException("La ligne de devis n'appartient pas au projet de la situation");
        }

        BtpSituationLigne ligne = ligneMapper.dtoToModele(dto);
        ligne.setId(existante == null ? null : existante.getId());
        ligne.setCreeLe(existante == null ? OffsetDateTime.now() : existante.getCreeLe());
        ligne.setSituationId(situation.getId());
        BigDecimal precedente = dto.getQuantitePrecedente() != null
                ? dto.getQuantitePrecedente()
                : quantiteDejaRealisee(situation, ligneDevis.getId());
        ligne.setQuantitePrecedente(precedente);
        ligne.setQuantiteCumulee(BtpSituationCalculator.quantiteCumulee(precedente, dto.getQuantitePeriode()));
        ligne.setMontantPeriodeHt(
                BtpSituationCalculator.montantLigne(dto.getQuantitePeriode(), ligneDevis.getPrixUnitaireHt()));
        return ligne;
    }

    /** Quantites de la ligne de devis deja realisees dans les autres situations anterieures ou du meme jour. */
    private BigDecimal quantiteDejaRealisee(BtpSituationTravaux courante, Long ligneDevisId) {
        List<Long> idsAutres =
                situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(courante.getProjetId()).stream()
                        .filter(s -> !s.getId().equals(courante.getId()))
                        .filter(s -> !s.getDateSituation().isAfter(courante.getDateSituation()))
                        .map(BtpSituationTravaux::getId)
                        .toList();
        if (idsAutres.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return ligneRepository.findBySituationIdIn(idsAutres).stream()
                .filter(l -> ligneDevisId.equals(l.getLigneDevisId()))
                .map(BtpSituationLigne::getQuantitePeriode)
                .filter(q -> q != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Equivalent Java du trigger fn_sync_situation_totaux. */
    private BtpSituationTravaux recalculer(BtpSituationTravaux situation) {
        List<BtpSituationLigne> lignes = ligneRepository.findBySituationIdOrderByIdAsc(situation.getId());
        List<BtpSituationTravaux> situationsProjet =
                situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(situation.getProjetId());
        BigDecimal cumulAutres = BtpSituationCalculator.cumulAutresSituations(situation, situationsProjet);
        BigDecimal budget = BtpDevisCalculator.budget(
                devisRepository.findByProjetIdAndStatutIn(situation.getProjetId(), BtpStatuts.DEVIS_BUDGET));
        BtpSituationCalculator.Totaux totaux = BtpSituationCalculator.totaux(
                lignes.stream().map(BtpSituationLigne::getMontantPeriodeHt).toList(), cumulAutres, budget);
        situation.setMontantPeriodeHt(totaux.montantPeriodeHt());
        situation.setMontantCumuleHt(totaux.montantCumuleHt());
        situation.setTauxAvancementPct(totaux.tauxAvancementPct());
        return situationRepository.save(situation);
    }

    private BtpSituationCompleteDto recalculerEtLire(BtpSituationTravaux situation) {
        return versComplet(recalculer(situation));
    }

    private BtpSituationCompleteDto versComplet(BtpSituationTravaux situation) {
        BtpSituationCompleteDto complet = new BtpSituationCompleteDto();
        complet.setSituation(situationMapper.modeleToDto(situation));
        complet.setLignes(ligneMapper.modeleToDto(ligneRepository.findBySituationIdOrderByIdAsc(situation.getId())));
        return complet;
    }

    private BtpSituationTravaux trouverSituation(Long id) {
        return situationRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Situation introuvable : " + id));
    }

    private BtpSituationLigne trouverLigne(Long situationId, Long ligneId) {
        BtpSituationLigne ligne = ligneRepository
                .findById(ligneId)
                .orElseThrow(() -> new IllegalArgumentException("Ligne de situation introuvable : " + ligneId));
        if (!situationId.equals(ligne.getSituationId())) {
            throw new IllegalArgumentException(
                    "La ligne " + ligneId + " n'appartient pas a la situation " + situationId);
        }
        return ligne;
    }
}
