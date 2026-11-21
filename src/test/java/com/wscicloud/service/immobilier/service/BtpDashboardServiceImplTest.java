package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.AvancementLot;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpPaiement;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpLot;
import com.wscicloud.service.immobilier.domain.repository.BtpDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpPaiementRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationTravauxRepository;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpLotRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BtpDashboardServiceImplTest {

    @Mock
    private BtpProjetRepository projetRepository;

    @Mock
    private BtpDevisRepository devisRepository;

    @Mock
    private BtpLigneDevisRepository ligneDevisRepository;

    @Mock
    private BtpSituationTravauxRepository situationRepository;

    @Mock
    private BtpSituationLigneRepository situationLigneRepository;

    @Mock
    private BtpFactureRepository factureRepository;

    @Mock
    private BtpPaiementRepository paiementRepository;

    @Mock
    private RefmBtpLotRepository lotRepository;

    @InjectMocks
    private BtpDashboardServiceImpl service;

    @Test
    @DisplayName("Given un projet inconnu When je demande le tableau de bord Then IllegalArgumentException")
    void projetInconnu() {
        when(projetRepository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> service.tableauDeBord(9L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Projet introuvable");
    }

    @Test
    @DisplayName(
            "Given un projet avec devis valide, situation validee, facture et paiement When je construis le tableau de bord Then les 3 vues sont agregees avec le libelle du lot")
    void tableauDeBordComplet() {
        Long projet = 1L;
        when(projetRepository.existsById(projet)).thenReturn(true);

        BtpDevis devis = new BtpDevis();
        devis.setId(100L);
        devis.setProjetId(projet);
        devis.setStatut("VALIDE");
        devis.setMontantHt(new BigDecimal("1000.00"));
        BtpLigneDevis ld = new BtpLigneDevis();
        ld.setId(10L);
        ld.setDevisId(100L);
        ld.setLotId(7L);
        ld.setMontantHt(new BigDecimal("1000.00"));
        BtpSituationTravaux situation = new BtpSituationTravaux();
        situation.setId(20L);
        situation.setProjetId(projet);
        situation.setStatut("VALIDEE");
        situation.setMontantCumuleHt(new BigDecimal("400.00"));
        BtpSituationLigne sl = new BtpSituationLigne();
        sl.setSituationId(20L);
        sl.setLigneDevisId(10L);
        sl.setMontantPeriodeHt(new BigDecimal("400.00"));
        BtpFacture facture = new BtpFacture();
        facture.setId(30L);
        facture.setProjetId(projet);
        facture.setStatut("EMISE");
        facture.setMontantHt(new BigDecimal("400.00"));
        facture.setMontantTtc(new BigDecimal("472.00"));
        BtpPaiement paiement = new BtpPaiement();
        paiement.setFactureId(30L);
        paiement.setMontant(new BigDecimal("200.00"));
        RefmBtpLot lot = new RefmBtpLot();
        lot.setId(7L);
        lot.setCode("GO");
        lot.setLibelle("Gros oeuvre");

        when(devisRepository.findByProjetIdOrderByDateDevisDescIdDesc(projet)).thenReturn(List.of(devis));
        when(ligneDevisRepository.findByDevisIdIn(List.of(100L))).thenReturn(List.of(ld));
        when(situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(projet))
                .thenReturn(List.of(situation));
        when(situationLigneRepository.findBySituationIdIn(List.of(20L))).thenReturn(List.of(sl));
        when(factureRepository.findByProjetIdOrderByDateEmissionDescIdDesc(projet))
                .thenReturn(List.of(facture));
        when(paiementRepository.findByFactureIdIn(List.of(30L))).thenReturn(List.of(paiement));
        when(lotRepository.findAll()).thenReturn(List.of(lot));

        BtpDashboardDto dto = service.tableauDeBord(projet);

        assertThat(dto.getAvancementParLot()).hasSize(1);
        AvancementLot al = dto.getAvancementParLot().get(0);
        assertThat(al.getLotCode()).isEqualTo("GO");
        assertThat(al.getLotLibelle()).isEqualTo("Gros oeuvre");
        assertThat(al.getMontantDevisHt()).isEqualByComparingTo("1000.00");
        assertThat(al.getMontantExecuteHt()).isEqualByComparingTo("400.00");
        assertThat(al.getResteAExecuterHt()).isEqualByComparingTo("600.00");
        assertThat(al.getTauxAvancementFinancierPct()).isEqualByComparingTo("40.00");
        assertThat(dto.getResteAFacturer().getResteAFacturerSurDevisHt()).isEqualByComparingTo("600.00");
        assertThat(dto.getResteAFacturer().getResteAFacturerSurTravauxHt()).isEqualByComparingTo("0.00");
        assertThat(dto.getSoldeClient().getTotalFactureTtc()).isEqualByComparingTo("472.00");
        assertThat(dto.getSoldeClient().getTotalEncaisse()).isEqualByComparingTo("200.00");
        assertThat(dto.getSoldeClient().getSoldeClientAPayer()).isEqualByComparingTo("272.00");
    }

    @Test
    @DisplayName("Given un projet sans aucune donnee When je demande le solde client Then tous les montants valent 0")
    void projetVide() {
        when(projetRepository.existsById(1L)).thenReturn(true);
        when(factureRepository.findByProjetIdOrderByDateEmissionDescIdDesc(1L)).thenReturn(List.of());

        var solde = service.soldeClient(1L);

        assertThat(solde.getTotalFactureTtc()).isEqualByComparingTo("0");
        assertThat(solde.getTotalEncaisse()).isEqualByComparingTo("0");
        assertThat(solde.getSoldeClientAPayer()).isEqualByComparingTo("0");
    }
}
