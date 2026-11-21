package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.util.Collection;

import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;

import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.montant;
import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.nz;

/** Calculs de situation de travaux (logique de l'ancien trigger fn_sync_situation_totaux). */
public final class BtpSituationCalculator {

    private BtpSituationCalculator() {}

    public record Totaux(BigDecimal montantPeriodeHt, BigDecimal montantCumuleHt, BigDecimal tauxAvancementPct) {}

    /** montantPeriodeHt de ligne = quantitePeriode x prixUnitaireHt de la ligne de devis. */
    public static BigDecimal montantLigne(BigDecimal quantitePeriode, BigDecimal prixUnitaireHt) {
        return BtpMontants.produit(quantitePeriode, prixUnitaireHt);
    }

    public static BigDecimal quantiteCumulee(BigDecimal quantitePrecedente, BigDecimal quantitePeriode) {
        return nz(quantitePeriode).add(nz(quantitePrecedente));
    }

    /** Somme des montants de periode des AUTRES situations de date <= date de la situation courante. */
    public static BigDecimal cumulAutresSituations(
            BtpSituationTravaux courante, Collection<BtpSituationTravaux> situationsProjet) {
        BigDecimal total = BigDecimal.ZERO;
        for (BtpSituationTravaux s : situationsProjet) {
            boolean autre = s.getId() == null || !s.getId().equals(courante.getId());
            if (autre && !s.getDateSituation().isAfter(courante.getDateSituation())) {
                total = total.add(nz(s.getMontantPeriodeHt()));
            }
        }
        return montant(total);
    }

    /**
     * periode = somme des lignes ; cumule = cumul des autres situations + periode ;
     * taux d'avancement = cumule / budget x 100 (0 si budget nul).
     */
    public static Totaux totaux(Collection<BigDecimal> montantsLignes, BigDecimal cumulAutres, BigDecimal budget) {
        BigDecimal periode = BtpMontants.somme(montantsLignes);
        BigDecimal cumule = montant(nz(cumulAutres).add(periode));
        return new Totaux(periode, cumule, BtpMontants.pourcentage(cumule, budget));
    }
}
