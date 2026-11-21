package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;

import static org.assertj.core.api.Assertions.assertThat;

class BtpSituationCalculatorTest {

    private static BtpSituationTravaux situation(Long id, String date, String montantPeriode) {
        BtpSituationTravaux s = new BtpSituationTravaux();
        s.setId(id);
        s.setDateSituation(LocalDate.parse(date));
        s.setMontantPeriodeHt(new BigDecimal(montantPeriode));
        return s;
    }

    @Test
    @DisplayName("Given quantite periode 2.5 et PU 1000 When on calcule la ligne Then 2500.00")
    void montantLigne() {
        assertThat(BtpSituationCalculator.montantLigne(new BigDecimal("2.5"), new BigDecimal("1000")))
                .isEqualByComparingTo("2500.00");
    }

    @Test
    @DisplayName("Given precedente 4 et periode 1.5 When on calcule Then cumulee = 5.5 ; precedente absente => periode")
    void quantiteCumulee() {
        assertThat(BtpSituationCalculator.quantiteCumulee(new BigDecimal("4"), new BigDecimal("1.5")))
                .isEqualByComparingTo("5.5");
        assertThat(BtpSituationCalculator.quantiteCumulee(null, new BigDecimal("1.5")))
                .isEqualByComparingTo("1.5");
    }

    @Test
    @DisplayName(
            "Given des situations anterieures, posterieures et la courante When cumul des autres Then seules les autres de date <= comptent")
    void cumulAutresSituations() {
        BtpSituationTravaux courante = situation(3L, "2025-03-31", "700.00");
        List<BtpSituationTravaux> projet = List.of(
                situation(1L, "2025-01-31", "1000.00"),
                situation(2L, "2025-03-31", "500.00"), // meme date, autre id : comptee
                courante,
                situation(4L, "2025-04-30", "9999.00")); // posterieure : ignoree

        assertThat(BtpSituationCalculator.cumulAutresSituations(courante, projet))
                .isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName(
            "Given une situation courante pas encore persistee (id null) When cumul Then toutes les anterieures comptent")
    void cumulSituationNonPersistee() {
        BtpSituationTravaux courante = situation(null, "2025-03-31", "0");
        List<BtpSituationTravaux> projet = List.of(situation(1L, "2025-01-31", "400.00"));

        assertThat(BtpSituationCalculator.cumulAutresSituations(courante, projet))
                .isEqualByComparingTo("400.00");
    }

    @Test
    @DisplayName(
            "Given 2 lignes, un cumul precedent de 1500 et un budget de 10000 When totaux Then periode 1000, cumule 2500, avancement 25.00%")
    void totauxAvecCumulEtAvancement() {
        BtpSituationCalculator.Totaux t = BtpSituationCalculator.totaux(
                List.of(new BigDecimal("600.00"), new BigDecimal("400.00")),
                new BigDecimal("1500.00"),
                new BigDecimal("10000.00"));

        assertThat(t.montantPeriodeHt()).isEqualByComparingTo("1000.00");
        assertThat(t.montantCumuleHt()).isEqualByComparingTo("2500.00");
        assertThat(t.tauxAvancementPct()).isEqualByComparingTo("25.00");
    }

    @Test
    @DisplayName("Given un budget nul When totaux Then le taux d'avancement vaut 0 sans division par zero")
    void totauxBudgetNul() {
        BtpSituationCalculator.Totaux t =
                BtpSituationCalculator.totaux(List.of(new BigDecimal("600.00")), BigDecimal.ZERO, BigDecimal.ZERO);

        assertThat(t.montantCumuleHt()).isEqualByComparingTo("600.00");
        assertThat(t.tauxAvancementPct()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Given un cumul de 1/3 du budget When totaux Then le taux est arrondi a 33.33")
    void totauxTauxArrondi() {
        BtpSituationCalculator.Totaux t = BtpSituationCalculator.totaux(
                List.of(new BigDecimal("100.00")), BigDecimal.ZERO, new BigDecimal("300.00"));

        assertThat(t.tauxAvancementPct()).isEqualByComparingTo("33.33");
    }

    @Test
    @DisplayName("Given aucune ligne When totaux Then periode 0 et cumule = cumul des autres")
    void totauxSansLigne() {
        BtpSituationCalculator.Totaux t =
                BtpSituationCalculator.totaux(List.of(), new BigDecimal("800.00"), new BigDecimal("1000.00"));

        assertThat(t.montantPeriodeHt()).isEqualByComparingTo("0");
        assertThat(t.montantCumuleHt()).isEqualByComparingTo("800.00");
        assertThat(t.tauxAvancementPct()).isEqualByComparingTo("80.00");
    }
}
