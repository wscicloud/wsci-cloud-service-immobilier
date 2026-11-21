package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

/** Utilitaires d'arrondi BigDecimal (HALF_UP) communs aux calculs BTP. */
public final class BtpMontants {

    public static final int SCALE_MONTANT = 2;
    public static final int SCALE_TAUX = 4;

    private BtpMontants() {}

    public static BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static BigDecimal montant(BigDecimal value) {
        return nz(value).setScale(SCALE_MONTANT, RoundingMode.HALF_UP);
    }

    public static BigDecimal taux(BigDecimal value) {
        return nz(value).setScale(SCALE_TAUX, RoundingMode.HALF_UP);
    }

    public static BigDecimal somme(Collection<BigDecimal> values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            total = total.add(nz(v));
        }
        return montant(total);
    }

    /** Division protegee : retourne 0 si le diviseur est nul. */
    public static BigDecimal ratio(BigDecimal numerateur, BigDecimal denominateur) {
        if (denominateur == null || denominateur.signum() == 0) {
            return BigDecimal.ZERO.setScale(SCALE_TAUX);
        }
        return nz(numerateur).divide(denominateur, SCALE_TAUX, RoundingMode.HALF_UP);
    }
}
