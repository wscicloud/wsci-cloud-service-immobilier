package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/** Utilitaires d'arrondi BigDecimal (HALF_UP, 2 decimales) communs aux calculs BTP. */
public final class BtpMontants {

    public static final int SCALE = 2;

    private BtpMontants() {}

    public static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static BigDecimal montant(BigDecimal value) {
        return nz(value).setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal somme(Collection<BigDecimal> values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            total = total.add(nz(v));
        }
        return montant(total);
    }

    /** Quantite x prix unitaire, arrondi a 2 decimales. */
    public static BigDecimal produit(BigDecimal quantite, BigDecimal prixUnitaire) {
        return montant(nz(quantite).multiply(nz(prixUnitaire)));
    }

    /** HT x (1 + tauxTva / 100), le taux etant exprime en pourcentage (ex : 18.00). */
    public static BigDecimal ttc(BigDecimal montantHt, BigDecimal tauxTvaPct) {
        BigDecimal coefficient = BigDecimal.ONE.add(nz(tauxTvaPct).movePointLeft(2));
        return montant(nz(montantHt).multiply(coefficient));
    }

    /** numerateur / denominateur x 100 arrondi a 2 decimales ; 0 si le denominateur est nul. */
    public static BigDecimal pourcentage(BigDecimal numerateur, BigDecimal denominateur) {
        if (denominateur == null || denominateur.signum() == 0) {
            return montant(BigDecimal.ZERO);
        }
        return nz(numerateur).multiply(BigDecimal.valueOf(100)).divide(denominateur, SCALE, RoundingMode.HALF_UP);
    }

    /** max(valeur, 0). */
    public static BigDecimal positif(BigDecimal value) {
        return montant(nz(value).max(BigDecimal.ZERO));
    }
}
