package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.AvancementLot;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.ResteAFacturer;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.SoldeClient;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpPaiement;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;

import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.montant;
import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.nz;

/** Agregations du tableau de bord (ex-vues SQL v_avancement_financier_par_lot, v_reste_a_facturer, v_solde_client). */
public final class BtpDashboardCalculator {

    private BtpDashboardCalculator() {}

    /** Avancement financier par lot : devis du budget vs lignes de situations VALIDEE / FACTUREE. */
    public static List<AvancementLot> avancementParLot(
            Collection<BtpDevis> devis,
            Collection<BtpLigneDevis> lignesDevis,
            Collection<BtpSituationTravaux> situations,
            Collection<BtpSituationLigne> lignesSituations) {
        Set<Long> devisBudget = devis.stream()
                .filter(d -> BtpStatuts.DEVIS_BUDGET.contains(d.getStatut()))
                .map(BtpDevis::getId)
                .collect(Collectors.toSet());
        Set<Long> situationsExecutees = situations.stream()
                .filter(s -> BtpStatuts.SITUATION_EXECUTEE.contains(s.getStatut()))
                .map(BtpSituationTravaux::getId)
                .collect(Collectors.toSet());
        Map<Long, BtpLigneDevis> ligneParId = new HashMap<>();
        lignesDevis.forEach(l -> ligneParId.put(l.getId(), l));

        Map<Long, BigDecimal> devisParLot = new HashMap<>();
        Map<Long, BigDecimal> executeParLot = new HashMap<>();
        for (BtpLigneDevis l : lignesDevis) {
            if (devisBudget.contains(l.getDevisId())) {
                devisParLot.merge(l.getLotId(), nz(l.getMontantHt()), BigDecimal::add);
            }
        }
        for (BtpSituationLigne sl : lignesSituations) {
            BtpLigneDevis ligne = ligneParId.get(sl.getLigneDevisId());
            if (ligne != null && situationsExecutees.contains(sl.getSituationId())) {
                executeParLot.merge(ligne.getLotId(), nz(sl.getMontantPeriodeHt()), BigDecimal::add);
            }
        }

        Map<Long, AvancementLot> resultat = new TreeMap<>(Comparator.nullsLast(Comparator.<Long>naturalOrder()));
        devisParLot.keySet().forEach(lot -> resultat.put(lot, null));
        executeParLot.keySet().forEach(lot -> resultat.put(lot, null));
        resultat.replaceAll((lot, v) -> {
            BigDecimal montantDevis = montant(devisParLot.get(lot));
            BigDecimal execute = montant(executeParLot.get(lot));
            return new AvancementLot(
                    lot,
                    null,
                    null,
                    montantDevis,
                    execute,
                    BtpMontants.positif(montantDevis.subtract(execute)),
                    BtpMontants.pourcentage(execute, montantDevis));
        });
        return List.copyOf(resultat.values());
    }

    /** Reste a facturer sur devis et sur travaux valides. */
    public static ResteAFacturer resteAFacturer(
            Long projetId,
            Collection<BtpDevis> devis,
            Collection<BtpSituationTravaux> situations,
            Collection<BtpFacture> factures) {
        BigDecimal montantDevis = BtpDevisCalculator.budget(devis);
        BigDecimal travaux = situations.stream()
                .filter(s -> BtpStatuts.SITUATION_EXECUTEE.contains(s.getStatut()))
                .map(s -> nz(s.getMontantCumuleHt()))
                .max(Comparator.naturalOrder())
                .map(BtpMontants::montant)
                .orElse(montant(BigDecimal.ZERO));
        BigDecimal facture = montant(factures.stream()
                .filter(BtpDashboardCalculator::nonAnnulee)
                .map(f -> nz(f.getMontantHt()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return new ResteAFacturer(
                projetId,
                montantDevis,
                travaux,
                facture,
                BtpMontants.positif(montantDevis.subtract(facture)),
                BtpMontants.positif(travaux.subtract(facture)));
    }

    /** Solde client : facture TTC (hors ANNULEE) - encaissements de ces factures. */
    public static SoldeClient soldeClient(
            Long projetId, Collection<BtpFacture> factures, Collection<BtpPaiement> paiements) {
        Set<Long> facturesValides = factures.stream()
                .filter(BtpDashboardCalculator::nonAnnulee)
                .map(BtpFacture::getId)
                .collect(Collectors.toSet());
        BigDecimal totalFacture = montant(factures.stream()
                .filter(BtpDashboardCalculator::nonAnnulee)
                .map(f -> nz(f.getMontantTtc()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        BigDecimal encaisse = montant(paiements.stream()
                .filter(p -> facturesValides.contains(p.getFactureId()))
                .map(p -> nz(p.getMontant()))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return new SoldeClient(projetId, totalFacture, encaisse, montant(totalFacture.subtract(encaisse)));
    }

    private static boolean nonAnnulee(BtpFacture f) {
        return !BtpStatuts.FACTURE_ANNULEE.equals(f.getStatut());
    }
}
