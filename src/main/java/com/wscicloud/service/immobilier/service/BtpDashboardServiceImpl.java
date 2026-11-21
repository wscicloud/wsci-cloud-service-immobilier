package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto;
import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpEcartDto;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.entities.BtpSoustraitant;
import com.wscicloud.service.immobilier.domain.entities.BtpTachePlanning;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationTravauxRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSoustraitantRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpTachePlanningRepository;
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
public class BtpDashboardServiceImpl implements BtpDashboardService {

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpDevisService devisService;

    @Autowired
    private BtpDepenseService depenseService;

    @Autowired
    private BtpSituationTravauxRepository situationRepository;

    @Autowired
    private BtpTachePlanningRepository tacheRepository;

    @Autowired
    private BtpFactureRepository factureRepository;

    @Autowired
    private BtpSoustraitantRepository soustraitantRepository;

    @Override
    public BtpDashboardDto findByProjet(Long projetId) {
        BtpProjet projet = projetRepository
                .findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet BTP introuvable : " + projetId));
        BtpDashboardDto dash = new BtpDashboardDto();
        dash.setProjetId(projetId);
        dash.setNomProjet(projet.getNom());

        BtpDevisDto devis = devisService.findDevis(projetId);
        dash.setBudgetHt(devis.getTotalHt());
        dash.setBudgetApresRemise(devis.getTotalApresRemise());

        BtpEcartDto ecart = depenseService.calculerEcart(projetId);
        dash.setRealiseHt(ecart.getRealiseHt());
        dash.setEcart(ecart.getEcart());
        dash.setMargeBrute(ecart.getMargeBrute());
        dash.setMargeBrutePct(ecart.getMargeBrutePct());
        if (ecart.isAlerteMarge()) {
            dash.getAlertes().add("Marge brute inferieure au minimum acceptable");
        }

        List<BtpSituationTravaux> situations = situationRepository.findByProjetIdOrderByNumeroDesc(projetId);
        dash.setNombreSituations(situations.size());
        if (!situations.isEmpty()) {
            BigDecimal avancement = BtpMontants.nz(situations.get(0).getAvancementGlobalPct());
            dash.setAvancementTravauxPct(avancement);
            if (BtpSituationCalculator.alerteAvancement(avancement, projet.getTauxAvancementSeuilAlerte())) {
                dash.getAlertes().add("Avancement des travaux superieur au seuil d'alerte");
            }
        }

        dash.setAvancementPlanningPct(avancementPlanning(projetId, dash));

        BigDecimal ttc = BigDecimal.ZERO;
        BigDecimal paye = BigDecimal.ZERO;
        for (BtpFacture f : factureRepository.findByProjetIdOrderByDateFactureDesc(projetId)) {
            ttc = ttc.add(BtpMontants.nz(f.getMontantTtc()));
            paye = paye.add(BtpMontants.nz(f.getMontantPaye()));
            boolean impayee = f.getDateEcheance() != null
                    && f.getDateEcheance().isBefore(LocalDate.now())
                    && BtpMontants.nz(f.getMontantPaye()).compareTo(BtpMontants.nz(f.getNetAPayer())) < 0
                    && !"BROUILLON".equals(f.getStatut());
            if (impayee) {
                dash.getAlertes().add("Facture " + f.getNumero() + " echue et non soldee");
            }
        }
        dash.setMontantFactureTtc(BtpMontants.montant(ttc));
        dash.setMontantPaye(BtpMontants.montant(paye));

        dash.setMontantSousTraitance(BtpMontants.somme(soustraitantRepository.findByProjetId(projetId).stream()
                .map(BtpSoustraitant::getMarcheMontant)
                .toList()));
        return dash;
    }

    /** Avancement du planning pondere par la duree des taches ; alerte si une tache est en retard. */
    private BigDecimal avancementPlanning(Long projetId, BtpDashboardDto dash) {
        BigDecimal poids = BigDecimal.ZERO;
        BigDecimal realise = BigDecimal.ZERO;
        for (BtpTachePlanning t : tacheRepository.findByProjetIdOrderByDateDebut(projetId)) {
            BigDecimal duree = BigDecimal.valueOf(t.getDureeJours());
            poids = poids.add(duree);
            realise = realise.add(duree.multiply(BtpMontants.nz(t.getAvancementPct())));
            boolean enRetard = t.getDateFin() != null
                    && t.getDateFin().isBefore(LocalDate.now())
                    && BtpMontants.nz(t.getAvancementPct()).compareTo(BigDecimal.ONE) < 0;
            if (enRetard) {
                dash.getAlertes().add("Tache en retard : " + t.getNomTache());
            }
        }
        return BtpMontants.ratio(realise, poids);
    }

    @Override
    public List<BtpDashboardDto> findBySociete(Long societeId) {
        return projetRepository.findBySocieteIdOrderByNom(societeId).stream()
                .map(p -> findByProjet(p.getId()))
                .toList();
    }
}
