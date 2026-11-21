package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.util.Collection;

import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.nz;

/** Calculs de facture : lignes, TTC, paiements et statut. */
public final class BtpFactureCalculator {

    private BtpFactureCalculator() {}

    public static BigDecimal montantLigne(BigDecimal quantite, BigDecimal prixUnitaireHt) {
        return BtpMontants.produit(quantite, prixUnitaireHt);
    }

    public static BigDecimal montantTtc(BigDecimal montantHt, BigDecimal tauxTva) {
        return BtpMontants.ttc(montantHt, tauxTva);
    }

    public static BigDecimal totalPaye(Collection<BigDecimal> montantsPaiements) {
        return BtpMontants.somme(montantsPaiements);
    }

    /** Reste a payer, jamais negatif (un surpaiement donne 0). */
    public static BigDecimal resteAPayer(BigDecimal montantTtc, BigDecimal totalPaye) {
        return BtpMontants.positif(nz(montantTtc).subtract(nz(totalPaye)));
    }

    /** EMISE (aucun paiement), PARTIELLEMENT_PAYEE (paiements < TTC), PAYEE (paiements >= TTC). */
    public static String statutSelonPaiements(BigDecimal montantTtc, BigDecimal totalPaye) {
        if (nz(totalPaye).signum() <= 0) {
            return BtpStatuts.FACTURE_EMISE;
        }
        return nz(totalPaye).compareTo(nz(montantTtc)) >= 0
                ? BtpStatuts.FACTURE_PAYEE
                : BtpStatuts.FACTURE_PARTIELLEMENT_PAYEE;
    }
}
