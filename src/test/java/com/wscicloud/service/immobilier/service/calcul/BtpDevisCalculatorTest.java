package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.wscicloud.service.immobilier.domain.entities.BtpDevis;

import static org.assertj.core.api.Assertions.assertThat;

class BtpDevisCalculatorTest {

    private static BtpDevis devis(String statut, String montantHt) {
        BtpDevis d = new BtpDevis();
        d.setStatut(statut);
        d.setMontantHt(new BigDecimal(montantHt));
        return d;
    }

    @Test
    @DisplayName(
            "Given quantite 3.333 et PU 10.005 When on calcule la ligne Then le montant est arrondi a 2 decimales HALF_UP")
    void montantLigneArrondiHalfUp() {
        BigDecimal montant = BtpDevisCalculator.montantLigne(new BigDecimal("3.333"), new BigDecimal("10.005"));

        // 3.333 x 10.005 = 33.346665
        assertThat(montant).isEqualByComparingTo("33.35");
        assertThat(montant.scale()).isEqualTo(2);
    }

    @Test
    @DisplayName("Given un PU absent When on calcule la ligne Then le montant vaut 0")
    void montantLigneSansPrixVautZero() {
        assertThat(BtpDevisCalculator.montantLigne(new BigDecimal("5"), null)).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Given trois lignes et TVA 18% When on totalise Then HT = somme et TTC = HT x 1.18")
    void totauxHtEtTtc() {
        BtpDevisCalculator.Totaux totaux = BtpDevisCalculator.totaux(
                List.of(new BigDecimal("1000.00"), new BigDecimal("250.50"), new BigDecimal("0.25")),
                new BigDecimal("18.00"));

        assertThat(totaux.montantHt()).isEqualByComparingTo("1250.75");
        assertThat(totaux.montantTtc()).isEqualByComparingTo("1475.89"); // 1250.75 x 1.18 = 1475.885
    }

    @Test
    @DisplayName("Given aucune ligne When on totalise Then HT et TTC valent 0")
    void totauxSansLigne() {
        BtpDevisCalculator.Totaux totaux = BtpDevisCalculator.totaux(List.of(), new BigDecimal("18"));

        assertThat(totaux.montantHt()).isEqualByComparingTo("0");
        assertThat(totaux.montantTtc()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName(
            "Given des devis de statuts varies When on calcule le budget Then seuls VALIDE, ACCEPTE et EN_COURS comptent")
    void budgetNeRetientQueLesDevisValides() {
        BigDecimal budget = BtpDevisCalculator.budget(List.of(
                devis("VALIDE", "100.00"),
                devis("ACCEPTE", "200.00"),
                devis("EN_COURS", "300.00"),
                devis("BROUILLON", "1000.00"),
                devis("REFUSE", "5000.00")));

        assertThat(budget).isEqualByComparingTo("600.00");
    }
}
