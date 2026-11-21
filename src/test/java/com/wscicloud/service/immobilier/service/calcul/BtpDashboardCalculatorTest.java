package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.AvancementLot;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.ResteAFacturer;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.SoldeClient;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpPaiement;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;

import static org.assertj.core.api.Assertions.assertThat;

class BtpDashboardCalculatorTest {

    private static BtpDevis devis(long id, String statut, String ht) {
        BtpDevis d = new BtpDevis();
        d.setId(id);
        d.setStatut(statut);
        d.setMontantHt(new BigDecimal(ht));
        return d;
    }

    private static BtpLigneDevis ligneDevis(long id, long devisId, Long lotId, String ht) {
        BtpLigneDevis l = new BtpLigneDevis();
        l.setId(id);
        l.setDevisId(devisId);
        l.setLotId(lotId);
        l.setMontantHt(new BigDecimal(ht));
        return l;
    }

    private static BtpSituationTravaux situation(long id, String statut, String cumule) {
        BtpSituationTravaux s = new BtpSituationTravaux();
        s.setId(id);
        s.setStatut(statut);
        s.setDateSituation(LocalDate.of(2025, 1, 1));
        s.setMontantCumuleHt(new BigDecimal(cumule));
        return s;
    }

    private static BtpSituationLigne ligneSituation(long situationId, long ligneDevisId, String montant) {
        BtpSituationLigne l = new BtpSituationLigne();
        l.setSituationId(situationId);
        l.setLigneDevisId(ligneDevisId);
        l.setMontantPeriodeHt(new BigDecimal(montant));
        return l;
    }

    private static BtpFacture facture(long id, String statut, String ht, String ttc) {
        BtpFacture f = new BtpFacture();
        f.setId(id);
        f.setStatut(statut);
        f.setMontantHt(new BigDecimal(ht));
        f.setMontantTtc(new BigDecimal(ttc));
        return f;
    }

    private static BtpPaiement paiement(long factureId, String montant) {
        BtpPaiement p = new BtpPaiement();
        p.setFactureId(factureId);
        p.setMontant(new BigDecimal(montant));
        return p;
    }

    @Test
    @DisplayName(
            "Given deux lots, un devis brouillon et une situation soumise When avancement par lot Then seuls devis valides et situations VALIDEE/FACTUREE comptent")
    void avancementParLotAvecFiltresDeStatuts() {
        List<BtpDevis> devis = List.of(devis(1, "VALIDE", "0"), devis(2, "BROUILLON", "0"));
        List<BtpLigneDevis> lignes = List.of(
                ligneDevis(10, 1, 1L, "1000.00"),
                ligneDevis(11, 1, 2L, "500.00"),
                ligneDevis(12, 2, 1L, "7000.00")); // devis brouillon : ignore
        List<BtpSituationTravaux> situations = List.of(
                situation(100, "VALIDEE", "0"), situation(101, "FACTUREE", "0"), situation(102, "SOUMISE", "0"));
        List<BtpSituationLigne> lignesSituation = List.of(
                ligneSituation(100, 10, "250.00"),
                ligneSituation(101, 10, "100.00"),
                ligneSituation(102, 10, "9999.00"), // situation soumise : ignoree
                ligneSituation(100, 11, "600.00")); // depasse le devis du lot 2

        List<AvancementLot> resultat =
                BtpDashboardCalculator.avancementParLot(devis, lignes, situations, lignesSituation);

        assertThat(resultat).hasSize(2);
        AvancementLot lot1 = resultat.get(0);
        assertThat(lot1.getLotId()).isEqualTo(1L);
        assertThat(lot1.getMontantDevisHt()).isEqualByComparingTo("1000.00");
        assertThat(lot1.getMontantExecuteHt()).isEqualByComparingTo("350.00");
        assertThat(lot1.getResteAExecuterHt()).isEqualByComparingTo("650.00");
        assertThat(lot1.getTauxAvancementFinancierPct()).isEqualByComparingTo("35.00");
        AvancementLot lot2 = resultat.get(1);
        assertThat(lot2.getLotId()).isEqualTo(2L);
        assertThat(lot2.getMontantDevisHt()).isEqualByComparingTo("500.00");
        assertThat(lot2.getMontantExecuteHt()).isEqualByComparingTo("600.00");
        assertThat(lot2.getResteAExecuterHt()).isEqualByComparingTo("0"); // jamais negatif
        assertThat(lot2.getTauxAvancementFinancierPct()).isEqualByComparingTo("120.00");
    }

    @Test
    @DisplayName("Given un lot execute sans devis valide When avancement par lot Then taux 0 sans division par zero")
    void avancementLotSansDevis() {
        List<BtpDevis> devis = List.of(devis(2, "BROUILLON", "0"));
        List<BtpLigneDevis> lignes = List.of(ligneDevis(12, 2, 3L, "700.00"));
        List<BtpSituationTravaux> situations = List.of(situation(100, "VALIDEE", "0"));

        List<AvancementLot> resultat = BtpDashboardCalculator.avancementParLot(
                devis, lignes, situations, List.of(ligneSituation(100, 12, "50.00")));

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getMontantDevisHt()).isEqualByComparingTo("0");
        assertThat(resultat.get(0).getMontantExecuteHt()).isEqualByComparingTo("50.00");
        assertThat(resultat.get(0).getTauxAvancementFinancierPct()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Given aucune donnee When avancement par lot Then liste vide")
    void avancementVide() {
        assertThat(BtpDashboardCalculator.avancementParLot(List.of(), List.of(), List.of(), List.of()))
                .isEmpty();
    }

    @Test
    @DisplayName(
            "Given devis, situations et factures When reste a facturer Then devis valides, max cumule valide, factures non annulees et restes bornes a 0")
    void resteAFacturer() {
        List<BtpDevis> devis = List.of(
                devis(1, "VALIDE", "10000.00"), devis(2, "EN_COURS", "5000.00"), devis(3, "BROUILLON", "99999.00"));
        List<BtpSituationTravaux> situations = List.of(
                situation(1, "VALIDEE", "3000.00"),
                situation(2, "FACTUREE", "6000.00"),
                situation(3, "SOUMISE", "50000.00"));
        List<BtpFacture> factures = List.of(
                facture(1, "EMISE", "4000.00", "4720.00"),
                facture(2, "PAYEE", "3000.00", "3540.00"),
                facture(3, "ANNULEE", "8000.00", "9440.00"));

        ResteAFacturer r = BtpDashboardCalculator.resteAFacturer(7L, devis, situations, factures);

        assertThat(r.getProjetId()).isEqualTo(7L);
        assertThat(r.getMontantDevisValideHt()).isEqualByComparingTo("15000.00");
        assertThat(r.getMontantTravauxValidesHt()).isEqualByComparingTo("6000.00");
        assertThat(r.getMontantFactureHt()).isEqualByComparingTo("7000.00");
        assertThat(r.getResteAFacturerSurDevisHt()).isEqualByComparingTo("8000.00");
        assertThat(r.getResteAFacturerSurTravauxHt()).isEqualByComparingTo("0"); // 6000 - 7000 borne a 0
    }

    @Test
    @DisplayName("Given un projet vierge When reste a facturer Then tout vaut 0")
    void resteAFacturerProjetVierge() {
        ResteAFacturer r = BtpDashboardCalculator.resteAFacturer(1L, List.of(), List.of(), List.of());

        assertThat(r.getMontantDevisValideHt()).isEqualByComparingTo("0");
        assertThat(r.getMontantTravauxValidesHt()).isEqualByComparingTo("0");
        assertThat(r.getResteAFacturerSurDevisHt()).isEqualByComparingTo("0");
        assertThat(r.getResteAFacturerSurTravauxHt()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName(
            "Given une facture annulee payee et deux factures valides When solde client Then annulee exclue de la facture et de l'encaisse")
    void soldeClient() {
        List<BtpFacture> factures = List.of(
                facture(1, "PARTIELLEMENT_PAYEE", "1000.00", "1180.00"),
                facture(2, "EMISE", "2000.00", "2360.00"),
                facture(3, "ANNULEE", "5000.00", "5900.00"));
        List<BtpPaiement> paiements = List.of(paiement(1, "500.00"), paiement(1, "180.00"), paiement(3, "5900.00"));

        SoldeClient s = BtpDashboardCalculator.soldeClient(9L, factures, paiements);

        assertThat(s.getProjetId()).isEqualTo(9L);
        assertThat(s.getTotalFactureTtc()).isEqualByComparingTo("3540.00");
        assertThat(s.getTotalEncaisse()).isEqualByComparingTo("680.00");
        assertThat(s.getSoldeClientAPayer()).isEqualByComparingTo("2860.00");
    }

    @Test
    @DisplayName("Given un client ayant trop paye When solde client Then le solde est negatif (avoir)")
    void soldeClientTropPercu() {
        SoldeClient s = BtpDashboardCalculator.soldeClient(
                1L, List.of(facture(1, "PAYEE", "100.00", "118.00")), List.of(paiement(1, "150.00")));

        assertThat(s.getSoldeClientAPayer()).isEqualByComparingTo("-32.00");
    }
}
