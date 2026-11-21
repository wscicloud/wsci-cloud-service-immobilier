package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;

import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.montant;
import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.nz;

/** Calculs du devis : montant de ligne, remise, TVA. */
public final class BtpDevisCalculator {

    public static final BigDecimal TVA_DEFAUT = new BigDecimal("0.1800");

    private BtpDevisCalculator() {}

    /** montant_ht = quantite x prix unitaire. */
    public static BigDecimal montantLigne(BigDecimal quantite, BigDecimal prixUnitaire) {
        return montant(nz(quantite).multiply(nz(prixUnitaire)));
    }

    public static BigDecimal totalApresRemise(BigDecimal totalHt, BigDecimal remise) {
        return montant(nz(totalHt).subtract(nz(remise)));
    }

    /** TVA calculee sur le total apres remise. */
    public static BigDecimal montantTva(BigDecimal baseHt, BigDecimal tauxTva) {
        BigDecimal taux = tauxTva == null ? TVA_DEFAUT : tauxTva;
        return montant(nz(baseHt).multiply(taux));
    }

    public static BigDecimal totalTtc(BigDecimal baseHt, BigDecimal tauxTva) {
        return montant(nz(baseHt).add(montantTva(baseHt, tauxTva)));
    }
}
