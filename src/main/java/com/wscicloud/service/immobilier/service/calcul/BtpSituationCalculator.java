package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;

import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.montant;
import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.nz;
import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.ratio;
import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.taux;

/** Calculs de situation de travaux : cumul M, cumul M-1, production M, remise. */
public final class BtpSituationCalculator {

    private BtpSituationCalculator() {}

    public record Ligne(
            BigDecimal montantCumul,
            BigDecimal montantPrecedent,
            BigDecimal avancementPeriodePct,
            BigDecimal montantPeriode) {}

    public record Totaux(
            BigDecimal marche,
            BigDecimal cumul,
            BigDecimal precedent,
            BigDecimal periode,
            BigDecimal remiseCumul,
            BigDecimal remisePrecedent,
            BigDecimal remisePeriode,
            BigDecimal apresRemise,
            BigDecimal avancementGlobalPct) {}

    /** production M = cumul M - cumul M-1 (montants et pourcentages). */
    public static Ligne calculerLigne(
            BigDecimal montantMarche, BigDecimal avancementCumulPct, BigDecimal avancementPrecedentPct) {
        BigDecimal cumul = montant(nz(montantMarche).multiply(nz(avancementCumulPct)));
        BigDecimal precedent = montant(nz(montantMarche).multiply(nz(avancementPrecedentPct)));
        BigDecimal pctPeriode = taux(nz(avancementCumulPct).subtract(nz(avancementPrecedentPct)));
        return new Ligne(cumul, precedent, pctPeriode, montant(cumul.subtract(precedent)));
    }

    /** La remise est repartie proportionnellement a l'avancement (cumul / marche). */
    public static Totaux totaliser(BigDecimal marche, BigDecimal cumul, BigDecimal precedent, BigDecimal remiseTotale) {
        BigDecimal remiseCumul = marche == null || marche.signum() == 0
                ? BigDecimal.ZERO.setScale(2)
                : montant(nz(remiseTotale).multiply(nz(cumul)).divide(marche, 6, java.math.RoundingMode.HALF_UP));
        BigDecimal remisePrecedent = marche == null || marche.signum() == 0
                ? BigDecimal.ZERO.setScale(2)
                : montant(nz(remiseTotale).multiply(nz(precedent)).divide(marche, 6, java.math.RoundingMode.HALF_UP));
        BigDecimal periode = montant(nz(cumul).subtract(nz(precedent)));
        return new Totaux(
                montant(marche),
                montant(cumul),
                montant(precedent),
                periode,
                remiseCumul,
                remisePrecedent,
                montant(remiseCumul.subtract(remisePrecedent)),
                montant(nz(cumul).subtract(remiseCumul)),
                ratio(cumul, marche));
    }

    /** Alerte si l'avancement depasse le seuil du projet. */
    public static boolean alerteAvancement(BigDecimal avancementPct, BigDecimal seuil) {
        return seuil != null && nz(avancementPct).compareTo(seuil) > 0;
    }
}
