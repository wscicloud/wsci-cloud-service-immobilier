package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BtpFactureCalculatorTest {

    private static final BigDecimal TTC = new BigDecimal("1180.00");

    @Test
    @DisplayName("Given HT 1000 et TVA 18% When montantTtc Then 1180.00")
    void ttc() {
        assertThat(BtpFactureCalculator.montantTtc(new BigDecimal("1000"), new BigDecimal("18.00")))
                .isEqualByComparingTo("1180.00");
    }

    @Test
    @DisplayName("Given aucun paiement When statut Then EMISE")
    void statutSansPaiement() {
        assertThat(BtpFactureCalculator.statutSelonPaiements(TTC, BigDecimal.ZERO))
                .isEqualTo("EMISE");
    }

    @Test
    @DisplayName("Given un paiement partiel When statut Then PARTIELLEMENT_PAYEE")
    void statutPaiementPartiel() {
        assertThat(BtpFactureCalculator.statutSelonPaiements(TTC, new BigDecimal("500.00")))
                .isEqualTo("PARTIELLEMENT_PAYEE");
    }

    @Test
    @DisplayName("Given 1 centime manquant When statut Then reste PARTIELLEMENT_PAYEE")
    void statutIlManqueUnCentime() {
        assertThat(BtpFactureCalculator.statutSelonPaiements(TTC, new BigDecimal("1179.99")))
                .isEqualTo("PARTIELLEMENT_PAYEE");
    }

    @Test
    @DisplayName("Given paiements egaux au TTC When statut Then PAYEE")
    void statutSolde() {
        assertThat(BtpFactureCalculator.statutSelonPaiements(TTC, TTC)).isEqualTo("PAYEE");
    }

    @Test
    @DisplayName("Given un surpaiement When statut et reste Then PAYEE et reste a payer 0 (jamais negatif)")
    void surpaiement() {
        BigDecimal paye = BtpFactureCalculator.totalPaye(List.of(new BigDecimal("1000"), new BigDecimal("300")));

        assertThat(BtpFactureCalculator.statutSelonPaiements(TTC, paye)).isEqualTo("PAYEE");
        assertThat(BtpFactureCalculator.resteAPayer(TTC, paye)).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Given deux paiements partiels When reste a payer Then TTC - somme des paiements")
    void resteAPayer() {
        BigDecimal paye = BtpFactureCalculator.totalPaye(List.of(new BigDecimal("400.50"), new BigDecimal("100")));

        assertThat(paye).isEqualByComparingTo("500.50");
        assertThat(BtpFactureCalculator.resteAPayer(TTC, paye)).isEqualByComparingTo("679.50");
    }
}
