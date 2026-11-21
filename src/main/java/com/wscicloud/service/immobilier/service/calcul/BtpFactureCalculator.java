package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;

import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.montant;
import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.nz;

/** Calculs de facture : TVA 18 %, retenue de garantie 5 %, avance 4 % (par defaut). */
public final class BtpFactureCalculator {

    public static final BigDecimal TVA_DEFAUT = new BigDecimal("0.1800");
    public static final BigDecimal RETENUE_GARANTIE_DEFAUT = new BigDecimal("0.0500");
    public static final BigDecimal AVANCE_DEFAUT = new BigDecimal("0.0400");

    private BtpFactureCalculator() {}

    public record Totaux(
            BigDecimal montantHt,
            BigDecimal montantTva,
            BigDecimal montantTtc,
            BigDecimal retenueGarantie,
            BigDecimal avance,
            BigDecimal netAPayer) {}

    /** Retenue de garantie et avance calculees sur le HT ; net a payer = TTC - retenue - avance. */
    public static Totaux calculer(
            BigDecimal montantHt, BigDecimal tvaTaux, BigDecimal retenueGarantieTaux, BigDecimal avanceTaux) {
        BigDecimal ht = montant(montantHt);
        BigDecimal tva = montant(ht.multiply(tvaTaux == null ? TVA_DEFAUT : tvaTaux));
        BigDecimal ttc = montant(ht.add(tva));
        BigDecimal rg =
                montant(ht.multiply(retenueGarantieTaux == null ? RETENUE_GARANTIE_DEFAUT : retenueGarantieTaux));
        BigDecimal avance = montant(ht.multiply(avanceTaux == null ? AVANCE_DEFAUT : avanceTaux));
        BigDecimal net = montant(ttc.subtract(rg).subtract(avance));
        return new Totaux(ht, tva, ttc, rg, avance, net);
    }

    public static BigDecimal resteAPayer(BigDecimal netAPayer, BigDecimal montantPaye) {
        return montant(nz(netAPayer).subtract(nz(montantPaye)));
    }

    /** BROUILLON / EMISE / PAYEE_PARTIELLE / PAYEE selon le cumul des paiements. */
    public static String statutPaiement(BigDecimal netAPayer, BigDecimal montantPaye) {
        if (nz(montantPaye).signum() <= 0) {
            return "EMISE";
        }
        return nz(montantPaye).compareTo(nz(netAPayer)) >= 0 ? "PAYEE" : "PAYEE_PARTIELLE";
    }
}
