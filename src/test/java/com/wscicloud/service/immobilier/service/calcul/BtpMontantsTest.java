package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BtpMontantsTest {

    @Test
    @DisplayName(
            "Given un pourcentage dont le denominateur est nul When on calcule Then on renvoie 0 sans division par zero")
    void pourcentageDenominateurNul() {
        assertThat(BtpMontants.pourcentage(new BigDecimal("50"), BigDecimal.ZERO))
                .isEqualByComparingTo("0.00");
        assertThat(BtpMontants.pourcentage(new BigDecimal("50"), null)).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Given 1 sur 3 When on calcule le pourcentage Then 33.33 (arrondi HALF_UP a 2 decimales)")
    void pourcentageArrondi() {
        assertThat(BtpMontants.pourcentage(BigDecimal.ONE, new BigDecimal("3"))).isEqualByComparingTo("33.33");
        assertThat(BtpMontants.pourcentage(new BigDecimal("2"), new BigDecimal("3")))
                .isEqualByComparingTo("66.67");
    }

    @Test
    @DisplayName("Given un montant negatif When positif Then 0 ; un montant positif reste inchange")
    void positifBorneLeResultat() {
        assertThat(BtpMontants.positif(new BigDecimal("-12.5"))).isEqualByComparingTo("0");
        assertThat(BtpMontants.positif(new BigDecimal("12.5"))).isEqualByComparingTo("12.50");
    }

    @Test
    @DisplayName("Given des valeurs dont une nulle When on somme Then la valeur nulle est ignoree")
    void sommeIgnoreLesNull() {
        List<BigDecimal> valeurs = new java.util.ArrayList<>();
        valeurs.add(new BigDecimal("1.10"));
        valeurs.add(null);
        valeurs.add(new BigDecimal("2.20"));

        assertThat(BtpMontants.somme(valeurs)).isEqualByComparingTo("3.30");
    }

    @Test
    @DisplayName("Given un HT de 100 et une TVA de 18 (pourcent) When ttc Then 118.00 ; TVA nulle => HT")
    void ttcAvecTauxEnPourcentage() {
        assertThat(BtpMontants.ttc(new BigDecimal("100"), new BigDecimal("18.00")))
                .isEqualByComparingTo("118.00");
        assertThat(BtpMontants.ttc(new BigDecimal("100"), null)).isEqualByComparingTo("100.00");
    }
}
