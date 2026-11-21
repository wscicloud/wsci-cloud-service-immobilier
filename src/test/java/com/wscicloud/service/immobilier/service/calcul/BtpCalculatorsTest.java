package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.wscicloud.service.immobilier.service.BtpPlanningServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

class BtpCalculatorsTest {

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    // --- Devis ---

    @Test
    void montantLigne_estQuantiteFoisPrixUnitaireArrondiHalfUp() {
        assertThat(BtpDevisCalculator.montantLigne(bd("12.5"), bd("1000.005"))).isEqualByComparingTo("12500.06");
    }

    @Test
    void totalApresRemise_triplex() {
        assertThat(BtpDevisCalculator.totalApresRemise(bd("63800123"), bd("3000000")))
                .isEqualByComparingTo("60800123");
    }

    @Test
    void tvaEtTtc_a18Pourcent() {
        assertThat(BtpDevisCalculator.montantTva(bd("1000"), bd("0.18"))).isEqualByComparingTo("180.00");
        assertThat(BtpDevisCalculator.totalTtc(bd("1000"), bd("0.18"))).isEqualByComparingTo("1180.00");
    }

    // --- Facture ---

    @Test
    void facture_tva18_retenue5_avance4() {
        BtpFactureCalculator.Totaux t = BtpFactureCalculator.calculer(bd("1000000"), null, null, null);
        assertThat(t.montantTva()).isEqualByComparingTo("180000.00");
        assertThat(t.montantTtc()).isEqualByComparingTo("1180000.00");
        assertThat(t.retenueGarantie()).isEqualByComparingTo("50000.00");
        assertThat(t.avance()).isEqualByComparingTo("40000.00");
        assertThat(t.netAPayer()).isEqualByComparingTo("1090000.00");
    }

    @Test
    void facture_statutPaiementEtReste() {
        assertThat(BtpFactureCalculator.statutPaiement(bd("1000"), BigDecimal.ZERO))
                .isEqualTo("EMISE");
        assertThat(BtpFactureCalculator.statutPaiement(bd("1000"), bd("400"))).isEqualTo("PAYEE_PARTIELLE");
        assertThat(BtpFactureCalculator.statutPaiement(bd("1000"), bd("1000"))).isEqualTo("PAYEE");
        assertThat(BtpFactureCalculator.resteAPayer(bd("1000"), bd("400"))).isEqualByComparingTo("600.00");
    }

    // --- Situation ---

    @Test
    void situation_productionMEgaleCumulMoinsCumulMMoinsUn() {
        BtpSituationCalculator.Ligne l = BtpSituationCalculator.calculerLigne(bd("1000000"), bd("0.60"), bd("0.25"));
        assertThat(l.montantCumul()).isEqualByComparingTo("600000.00");
        assertThat(l.montantPrecedent()).isEqualByComparingTo("250000.00");
        assertThat(l.avancementPeriodePct()).isEqualByComparingTo("0.35");
        assertThat(l.montantPeriode()).isEqualByComparingTo("350000.00");
    }

    @Test
    void situation_remiseProrataAvancement() {
        BtpSituationCalculator.Totaux t =
                BtpSituationCalculator.totaliser(bd("63800123"), bd("31900061.50"), BigDecimal.ZERO, bd("3000000"));
        assertThat(t.remiseCumul()).isEqualByComparingTo("1500000.00");
        assertThat(t.apresRemise()).isEqualByComparingTo("30400061.50");
        assertThat(t.avancementGlobalPct()).isEqualByComparingTo("0.5");
    }

    @Test
    void situation_marcheNulSansDivisionParZero() {
        BtpSituationCalculator.Totaux t =
                BtpSituationCalculator.totaliser(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, bd("1000"));
        assertThat(t.remiseCumul()).isEqualByComparingTo("0");
    }

    @Test
    void situation_alerteAvancement() {
        assertThat(BtpSituationCalculator.alerteAvancement(bd("0.95"), bd("0.90")))
                .isTrue();
        assertThat(BtpSituationCalculator.alerteAvancement(bd("0.90"), bd("0.90")))
                .isFalse();
        assertThat(BtpSituationCalculator.alerteAvancement(bd("0.95"), null)).isFalse();
    }

    // --- Planning ---

    @Test
    void planning_dateFin() {
        assertThat(BtpPlanningServiceImpl.calculerDateFin(LocalDate.of(2025, 7, 1), 10))
                .isEqualTo(LocalDate.of(2025, 7, 10));
    }
}
