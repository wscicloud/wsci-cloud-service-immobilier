package com.wscicloud.service.immobilier.controller;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto;
import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureCompletDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpPaiementDto;
import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpLotDto;
import com.wscicloud.service.immobilier.service.BtpDashboardService;
import com.wscicloud.service.immobilier.service.BtpDevisService;
import com.wscicloud.service.immobilier.service.BtpFactureService;
import com.wscicloud.service.immobilier.service.BtpProjetService;
import com.wscicloud.service.immobilier.service.BtpReferentielService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BtpControllersTest {

    @Mock
    private BtpProjetService projetService;

    @Mock
    private BtpDevisService devisService;

    @Mock
    private BtpFactureService factureService;

    @Mock
    private BtpDashboardService dashboardService;

    @Mock
    private BtpReferentielService referentielService;

    @InjectMocks
    private BtpProjetController projetController;

    @InjectMocks
    private BtpDevisController devisController;

    @InjectMocks
    private BtpFactureController factureController;

    @InjectMocks
    private BtpDashboardController dashboardController;

    @InjectMocks
    private BtpReferentielController referentielController;

    private MockMvc mvc;

    @BeforeEach
    void init() {
        mvc = MockMvcBuilders.standaloneSetup(
                        projetController,
                        devisController,
                        factureController,
                        dashboardController,
                        referentielController)
                .build();
    }

    @Test
    @DisplayName(
            "Given un projet valide When POST btp/projet Then 200, le service recoit le DTO et le JSON retourne l'id attribue")
    void creerProjet() throws Exception {
        BtpProjetDto cree = new BtpProjetDto();
        cree.setId(5L);
        cree.setCodeProjet("P1");
        cree.setStatut("EN_COURS");
        when(projetService.creer(any(BtpProjetDto.class))).thenReturn(cree);

        mvc.perform(post("/btp/projet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codeProjet\":\"P1\",\"nomProjet\":\"Villa\",\"clientNom\":\"Client\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.statut").value("EN_COURS"));

        ArgumentCaptor<BtpProjetDto> captor = ArgumentCaptor.forClass(BtpProjetDto.class);
        verify(projetService).creer(captor.capture());
        assertThat(captor.getValue().getNomProjet()).isEqualTo("Villa");
    }

    @Test
    @DisplayName("Given un projet sans code ni nom When POST btp/projet Then 400 et le service n'est pas appele")
    void creerProjetInvalide() throws Exception {
        mvc.perform(post("/btp/projet").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(projetService);
    }

    @Test
    @DisplayName("Given deux projets When GET btp/projet/all Then 200 et la liste JSON")
    void listerProjets() throws Exception {
        BtpProjetDto a = new BtpProjetDto();
        a.setId(1L);
        BtpProjetDto b = new BtpProjetDto();
        b.setId(2L);
        when(projetService.lister()).thenReturn(List.of(a, b));

        mvc.perform(get("/btp/projet/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("Given un projet When DELETE btp/projet/3 Then 204 et suppression deleguee avec l'id 3")
    void supprimerProjet() throws Exception {
        mvc.perform(delete("/btp/projet/3")).andExpect(status().isNoContent());
        verify(projetService).supprimer(3L);
    }

    @Test
    @DisplayName("Given un devis avec lignes When GET btp/devis/8 Then 200 et le JSON contient totaux et lignes")
    void lireDevis() throws Exception {
        BtpDevisDto devis = new BtpDevisDto();
        devis.setId(8L);
        devis.setMontantHt(new BigDecimal("100.00"));
        devis.setMontantTtc(new BigDecimal("118.00"));
        devis.setLignes(List.of(new BtpLigneDevisDto()));
        when(devisService.lire(8L)).thenReturn(devis);

        mvc.perform(get("/btp/devis/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantTtc").value(118.00))
                .andExpect(jsonPath("$.lignes.length()").value(1));
    }

    @Test
    @DisplayName(
            "Given un statut When PUT btp/devis/8/statut/VALIDE Then le service est appele avec (8, VALIDE) et le statut retourne")
    void changerStatutDevis() throws Exception {
        BtpDevisDto devis = new BtpDevisDto();
        devis.setStatut("VALIDE");
        when(devisService.changerStatut(8L, "VALIDE")).thenReturn(devis);

        mvc.perform(put("/btp/devis/8/statut/VALIDE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("VALIDE"));
    }

    @Test
    @DisplayName(
            "Given un paiement de 400 When POST btp/facture/9/paiement Then 200 et la facture complete (statut, reste a payer) est retournee")
    void paiementFacture() throws Exception {
        BtpFactureDto facture = new BtpFactureDto();
        facture.setId(9L);
        facture.setStatut("PARTIELLEMENT_PAYEE");
        BtpFactureCompletDto complet = new BtpFactureCompletDto();
        complet.setFacture(facture);
        complet.setTotalPaye(new BigDecimal("400.00"));
        complet.setResteAPayer(new BigDecimal("600.00"));
        when(factureService.enregistrerPaiement(eq(9L), any(BtpPaiementDto.class)))
                .thenReturn(complet);

        mvc.perform(post("/btp/facture/9/paiement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"montant\":400.00,\"modePaiement\":\"VIREMENT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.facture.statut").value("PARTIELLEMENT_PAYEE"))
                .andExpect(jsonPath("$.resteAPayer").value(600.00));

        ArgumentCaptor<BtpPaiementDto> captor = ArgumentCaptor.forClass(BtpPaiementDto.class);
        verify(factureService).enregistrerPaiement(eq(9L), captor.capture());
        assertThat(captor.getValue().getMontant()).isEqualByComparingTo("400.00");
    }

    @Test
    @DisplayName(
            "Given une situation When POST btp/facture/situation/20 sans tauxTva Then TVA 0 par defaut transmise au service")
    void factureDepuisSituation() throws Exception {
        when(factureService.creerDepuisSituation(eq(20L), eq("F-S1"), any(BigDecimal.class)))
                .thenReturn(new BtpFactureCompletDto());

        mvc.perform(post("/btp/facture/situation/20").param("numero", "F-S1")).andExpect(status().isOk());

        ArgumentCaptor<BigDecimal> tva = ArgumentCaptor.forClass(BigDecimal.class);
        verify(factureService).creerDepuisSituation(eq(20L), eq("F-S1"), tva.capture());
        assertThat(tva.getValue()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName(
            "Given un reste a payer When GET btp/facture/9/reste-a-payer Then le montant est retourne en corps JSON")
    void resteAPayer() throws Exception {
        when(factureService.resteAPayer(9L)).thenReturn(new BigDecimal("880.00"));

        mvc.perform(get("/btp/facture/9/reste-a-payer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(880.00));
    }

    @Test
    @DisplayName("Given un tableau de bord When GET btp/dashboard/projet/1 Then 200 et les vues sont dans le JSON")
    void dashboard() throws Exception {
        BtpDashboardDto dto = new BtpDashboardDto();
        dto.setProjetId(1L);
        BtpDashboardDto.SoldeClient solde = new BtpDashboardDto.SoldeClient();
        solde.setSoldeClientAPayer(new BigDecimal("272.00"));
        dto.setSoldeClient(solde);
        BtpDashboardDto.AvancementLot lot = new BtpDashboardDto.AvancementLot();
        lot.setLotCode("GO");
        dto.setAvancementParLot(List.of(lot));
        when(dashboardService.tableauDeBord(1L)).thenReturn(dto);

        mvc.perform(get("/btp/dashboard/projet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projetId").value(1))
                .andExpect(jsonPath("$.avancementParLot[0].lotCode").value("GO"))
                .andExpect(jsonPath("$.soldeClient.soldeClientAPayer").value(272.00));
    }

    @Test
    @DisplayName("Given un lot When GET btp/referentiel/lots Then 200 et la liste des lots")
    void referentielLots() throws Exception {
        RefmBtpLotDto lot = new RefmBtpLotDto();
        lot.setId(1L);
        lot.setCode("GO");
        when(referentielService.listerLots()).thenReturn(List.of(lot));

        mvc.perform(get("/btp/referentiel/lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("GO"));
    }
}
