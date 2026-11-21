package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationCompleteDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationTravauxDto;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpLotDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpPosteDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.mapper.BtpSituationTravauxMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLotDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpPosteDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationTravauxRepository;
import com.wscicloud.service.immobilier.service.calcul.BtpMontants;
import com.wscicloud.service.immobilier.service.calcul.BtpSituationCalculator;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Getter
@Setter
@Service
@Slf4j
public class BtpSituationServiceImpl implements BtpSituationService {

    @Autowired
    private BtpSituationTravauxRepository situationRepository;

    @Autowired
    private BtpSituationLigneRepository situationLigneRepository;

    @Autowired
    private BtpSituationTravauxMapper situationMapper;

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpLotDevisRepository lotRepository;

    @Autowired
    private BtpPosteDevisRepository posteRepository;

    @Autowired
    private BtpLigneDevisRepository ligneDevisRepository;

    private BtpSituationTravaux getSituation(Long id) {
        return situationRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Situation de travaux introuvable : " + id));
    }

    @Override
    public BtpSituationCompleteDto generer(Long projetId, Integer mois, Integer annee) {
        if (mois == null || mois < 1 || mois > 12 || annee == null) {
            throw new IllegalArgumentException("Mois (1-12) et annee obligatoires");
        }
        projetRepository
                .findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet BTP introuvable : " + projetId));

        List<BtpSituationTravaux> existantes = situationRepository.findByProjetIdOrderByNumeroDesc(projetId);
        boolean doublon = existantes.stream().anyMatch(s -> mois.equals(s.getMois()) && annee.equals(s.getAnnee()));
        if (doublon) {
            throw new IllegalStateException("Une situation existe deja pour " + mois + "/" + annee);
        }
        BtpSituationTravaux precedente = existantes.isEmpty() ? null : existantes.get(0);

        Map<Long, BigDecimal> cumulPrecedent = new HashMap<>();
        if (precedente != null) {
            situationLigneRepository
                    .findBySituationId(precedente.getId())
                    .forEach(l -> cumulPrecedent.put(l.getLigneDevisId(), BtpMontants.nz(l.getAvancementCumulPct())));
        }

        BtpSituationTravaux situation = new BtpSituationTravaux();
        situation.setProjetId(projetId);
        situation.setNumero(precedente == null ? 1 : precedente.getNumero() + 1);
        situation.setMois(mois);
        situation.setAnnee(annee);
        situation.setDateSituation(LocalDate.of(annee, mois, 1).with(TemporalAdjusters.lastDayOfMonth()));
        situation.setStatut("BROUILLON");
        situation = situationRepository.save(situation);

        for (BtpLotDevis lot : lotRepository.findByProjetIdOrderByOrdre(projetId)) {
            for (BtpPosteDevis poste : posteRepository.findByLotIdOrderByOrdre(lot.getId())) {
                for (BtpLigneDevis ligne : ligneDevisRepository.findByPosteIdOrderByOrdre(poste.getId())) {
                    BigDecimal precedentPct = cumulPrecedent.getOrDefault(ligne.getId(), BigDecimal.ZERO);
                    BtpSituationLigne sl = new BtpSituationLigne();
                    sl.setSituationId(situation.getId());
                    sl.setLigneDevisId(ligne.getId());
                    sl.setAvancementPrecedentPct(precedentPct);
                    sl.setAvancementCumulPct(precedentPct);
                    situationLigneRepository.save(sl);
                }
            }
        }
        return recalculer(situation.getId());
    }

    @Override
    public BtpSituationCompleteDto findById(Long situationId) {
        return complete(getSituation(situationId));
    }

    @Override
    public List<BtpSituationTravauxDto> findByProjet(Long projetId) {
        return situationMapper.modeleToDto(situationRepository.findByProjetIdOrderByNumeroDesc(projetId));
    }

    @Override
    public BtpSituationCompleteDto saisirAvancement(Long situationId, List<BtpSituationLigneDto> lignes) {
        BtpSituationTravaux situation = getSituation(situationId);
        if (!"BROUILLON".equals(situation.getStatut())) {
            throw new IllegalStateException("Seule une situation en BROUILLON peut etre modifiee");
        }
        Map<Long, BtpSituationLigne> parLigneDevis = new HashMap<>();
        situationLigneRepository.findBySituationId(situationId).forEach(l -> parLigneDevis.put(l.getLigneDevisId(), l));

        for (BtpSituationLigneDto dto : lignes) {
            BtpSituationLigne sl = parLigneDevis.get(dto.getLigneDevisId());
            if (sl == null) {
                throw new IllegalArgumentException("Ligne de devis absente de la situation : " + dto.getLigneDevisId());
            }
            BigDecimal pct = BtpMontants.nz(dto.getAvancementCumulPct());
            if (pct.signum() < 0 || pct.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("L'avancement doit etre compris entre 0 et 1 (0 % - 100 %)");
            }
            sl.setAvancementCumulPct(pct);
            situationLigneRepository.save(sl);
        }
        return recalculer(situationId);
    }

    @Override
    public BtpSituationCompleteDto recalculer(Long situationId) {
        BtpSituationTravaux situation = getSituation(situationId);
        BtpProjet projet = projetRepository.findById(situation.getProjetId()).orElseThrow();

        BigDecimal marche = BigDecimal.ZERO;
        BigDecimal cumul = BigDecimal.ZERO;
        BigDecimal precedent = BigDecimal.ZERO;
        for (BtpSituationLigne sl : situationLigneRepository.findBySituationId(situationId)) {
            BigDecimal montantMarche = ligneDevisRepository
                    .findById(sl.getLigneDevisId())
                    .map(BtpLigneDevis::getMontantHt)
                    .orElse(BigDecimal.ZERO);
            BtpSituationCalculator.Ligne calc = BtpSituationCalculator.calculerLigne(
                    montantMarche, sl.getAvancementCumulPct(), sl.getAvancementPrecedentPct());
            sl.setMontantCumulHt(calc.montantCumul());
            sl.setMontantPrecedentHt(calc.montantPrecedent());
            sl.setAvancementPeriodePct(calc.avancementPeriodePct());
            sl.setMontantPeriodeHt(calc.montantPeriode());
            situationLigneRepository.save(sl);
            marche = marche.add(montantMarche);
            cumul = cumul.add(calc.montantCumul());
            precedent = precedent.add(calc.montantPrecedent());
        }

        BtpSituationCalculator.Totaux t =
                BtpSituationCalculator.totaliser(marche, cumul, precedent, projet.getRemiseMontant());
        situation.setMontantMarcheHt(t.marche());
        situation.setMontantCumuleHt(t.cumul());
        situation.setMontantPrecedentHt(t.precedent());
        situation.setMontantPeriodeHt(t.periode());
        situation.setRemiseCumul(t.remiseCumul());
        situation.setRemisePrecedent(t.remisePrecedent());
        situation.setRemisePeriode(t.remisePeriode());
        situation.setMontantApresRemise(t.apresRemise());
        situation.setAvancementGlobalPct(t.avancementGlobalPct());
        situationRepository.save(situation);
        return complete(situation);
    }

    @Override
    public BtpSituationCompleteDto valider(Long situationId) {
        BtpSituationTravaux situation = getSituation(situationId);
        if (!"BROUILLON".equals(situation.getStatut())) {
            throw new IllegalStateException("Seule une situation en BROUILLON peut etre validee");
        }
        recalculer(situationId);
        situation.setStatut("VALIDEE");
        situationRepository.save(situation);
        return complete(situation);
    }

    @Override
    public BtpSituationCompleteDto marquerFacturee(Long situationId) {
        BtpSituationTravaux situation = getSituation(situationId);
        if (!"VALIDEE".equals(situation.getStatut())) {
            throw new IllegalStateException("Seule une situation VALIDEE peut etre facturee");
        }
        situation.setStatut("FACTUREE");
        situationRepository.save(situation);
        return complete(situation);
    }

    @Override
    public void delete(Long situationId) {
        BtpSituationTravaux situation = getSituation(situationId);
        if ("FACTUREE".equals(situation.getStatut())) {
            throw new IllegalStateException("Une situation facturee ne peut pas etre supprimee");
        }
        situationRepository.delete(situation);
    }

    private BtpSituationCompleteDto complete(BtpSituationTravaux situation) {
        BtpSituationCompleteDto result = new BtpSituationCompleteDto();
        result.setSituation(situationMapper.modeleToDto(situation));
        BtpProjet projet = projetRepository.findById(situation.getProjetId()).orElseThrow();

        Map<Long, BtpSituationCompleteDto.RecapPoste> recap = new LinkedHashMap<>();
        Map<Long, BtpPosteDevis> postes = new HashMap<>();
        Map<Long, BtpLotDevis> lots = new HashMap<>();
        BigDecimal totalGo = BigDecimal.ZERO;
        BigDecimal totalSo = BigDecimal.ZERO;

        for (BtpSituationLigne sl : situationLigneRepository.findBySituationId(situation.getId())) {
            BtpLigneDevis ld =
                    ligneDevisRepository.findById(sl.getLigneDevisId()).orElse(null);
            if (ld == null) {
                continue;
            }
            BtpPosteDevis poste = postes.computeIfAbsent(
                    ld.getPosteId(), id -> posteRepository.findById(id).orElseThrow());
            BtpLotDevis lot = lots.computeIfAbsent(
                    poste.getLotId(), id -> lotRepository.findById(id).orElseThrow());

            BtpSituationCompleteDto.LigneDetail d = new BtpSituationCompleteDto.LigneDetail();
            d.setSituationLigneId(sl.getId());
            d.setLigneDevisId(ld.getId());
            d.setCodeLot(lot.getCodeLot());
            d.setNomPoste(poste.getNomPoste());
            d.setSousEnsemble(ld.getSousEnsemble());
            d.setDesignation(ld.getDesignation());
            d.setUnite(ld.getUnite());
            d.setQuantite(ld.getQuantite());
            d.setPrixUnitaire(ld.getPrixUnitaire());
            d.setMontantMarcheHt(ld.getMontantHt());
            d.setAvancementCumulPct(sl.getAvancementCumulPct());
            d.setMontantCumulHt(sl.getMontantCumulHt());
            d.setAvancementPrecedentPct(sl.getAvancementPrecedentPct());
            d.setMontantPrecedentHt(sl.getMontantPrecedentHt());
            d.setAvancementPeriodePct(sl.getAvancementPeriodePct());
            d.setMontantPeriodeHt(sl.getMontantPeriodeHt());
            result.getLignes().add(d);

            BtpSituationCompleteDto.RecapPoste r = recap.computeIfAbsent(poste.getId(), id -> {
                BtpSituationCompleteDto.RecapPoste rp = new BtpSituationCompleteDto.RecapPoste();
                rp.setCodeLot(lot.getCodeLot());
                rp.setNomPoste(poste.getNomPoste());
                return rp;
            });
            r.setMontantMarcheHt(r.getMontantMarcheHt().add(BtpMontants.nz(ld.getMontantHt())));
            r.setMontantCumulHt(r.getMontantCumulHt().add(BtpMontants.nz(sl.getMontantCumulHt())));
            r.setMontantPrecedentHt(r.getMontantPrecedentHt().add(BtpMontants.nz(sl.getMontantPrecedentHt())));
            r.setMontantPeriodeHt(r.getMontantPeriodeHt().add(BtpMontants.nz(sl.getMontantPeriodeHt())));

            if ("GO".equals(lot.getCodeLot())) {
                totalGo = totalGo.add(BtpMontants.nz(sl.getMontantCumulHt()));
            } else if ("SO".equals(lot.getCodeLot())) {
                totalSo = totalSo.add(BtpMontants.nz(sl.getMontantCumulHt()));
            }
        }
        recap.values()
                .forEach(r -> r.setAvancementPct(BtpMontants.ratio(r.getMontantCumulHt(), r.getMontantMarcheHt())));
        result.setRecapitulatif(List.copyOf(recap.values()));
        result.setTotalGrosOeuvre(BtpMontants.montant(totalGo));
        result.setTotalSecondOeuvre(BtpMontants.montant(totalSo));
        result.setAlerteAvancement(BtpSituationCalculator.alerteAvancement(
                situation.getAvancementGlobalPct(), projet.getTauxAvancementSeuilAlerte()));
        return result;
    }
}
