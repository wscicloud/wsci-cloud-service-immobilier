package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureCompletDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFactureLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpPaiementDto;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;
import com.wscicloud.service.immobilier.domain.entities.BtpFactureLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpPaiement;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.mapper.BtpFactureLigneMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpFactureMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpPaiementMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpPaiementRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationTravauxRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BtpFactureServiceImplTest {

    @Mock
    private BtpFactureRepository factureRepository;

    @Mock
    private BtpFactureLigneRepository ligneRepository;

    @Mock
    private BtpPaiementRepository paiementRepository;

    @Mock
    private BtpProjetRepository projetRepository;

    @Mock
    private BtpSituationTravauxRepository situationRepository;

    @Mock
    private BtpSituationLigneRepository situationLigneRepository;

    @Mock
    private BtpLigneDevisRepository ligneDevisRepository;

    @Spy
    private BtpFactureMapper factureMapper = Mappers.getMapper(BtpFactureMapper.class);

    @Spy
    private BtpFactureLigneMapper ligneMapper = Mappers.getMapper(BtpFactureLigneMapper.class);

    @Spy
    private BtpPaiementMapper paiementMapper = Mappers.getMapper(BtpPaiementMapper.class);

    @InjectMocks
    private BtpFactureServiceImpl service;

    private static BtpFacture facture(String ttc, String statut) {
        BtpFacture f = new BtpFacture();
        f.setId(9L);
        f.setProjetId(1L);
        f.setNumeroFacture("F-1");
        f.setMontantHt(new BigDecimal(ttc));
        f.setMontantTtc(new BigDecimal(ttc));
        f.setStatut(statut);
        return f;
    }

    private static BtpPaiement paiement(String montant) {
        BtpPaiement p = new BtpPaiement();
        p.setFactureId(9L);
        p.setMontant(new BigDecimal(montant));
        return p;
    }

    private static BtpPaiementDto paiementDto(String montant) {
        BtpPaiementDto dto = new BtpPaiementDto();
        dto.setMontant(new BigDecimal(montant));
        return dto;
    }

    private BtpFacture paiementSurFacture(BtpFacture f, String montantDto, List<BtpPaiement> apres) {
        when(factureRepository.findById(9L)).thenReturn(Optional.of(f));
        when(paiementRepository.findByFactureIdOrderByDatePaiementAscIdAsc(9L)).thenReturn(apres);
        when(factureRepository.save(any(BtpFacture.class))).thenAnswer(i -> i.getArgument(0));
        return f;
    }

    @Test
    @DisplayName(
            "Given une facture de 1000 TTC When un paiement partiel de 400 est enregistre Then statut PARTIELLEMENT_PAYEE, reste 600, mode VIREMENT par defaut")
    void paiementPartiel() {
        paiementSurFacture(facture("1000.00", "EMISE"), "400", List.of(paiement("400.00")));

        BtpFactureCompletDto resultat = service.enregistrerPaiement(9L, paiementDto("400"));

        ArgumentCaptor<BtpPaiement> captor = ArgumentCaptor.forClass(BtpPaiement.class);
        verify(paiementRepository).save(captor.capture());
        assertThat(captor.getValue().getFactureId()).isEqualTo(9L);
        assertThat(captor.getValue().getModePaiement()).isEqualTo("VIREMENT");
        assertThat(captor.getValue().getDatePaiement()).isNotNull();
        assertThat(resultat.getFacture().getStatut()).isEqualTo("PARTIELLEMENT_PAYEE");
        assertThat(resultat.getTotalPaye()).isEqualByComparingTo("400.00");
        assertThat(resultat.getResteAPayer()).isEqualByComparingTo("600.00");
    }

    @Test
    @DisplayName("Given 400 deja payes When un paiement de 600 solde la facture Then statut PAYEE et reste 0")
    void paiementSolde() {
        paiementSurFacture(
                facture("1000.00", "PARTIELLEMENT_PAYEE"), "600", List.of(paiement("400.00"), paiement("600.00")));

        BtpFactureCompletDto resultat = service.enregistrerPaiement(9L, paiementDto("600"));

        assertThat(resultat.getFacture().getStatut()).isEqualTo("PAYEE");
        assertThat(resultat.getResteAPayer()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName(
            "Given une facture de 1000 When le cumul des paiements atteint 1200 Then statut PAYEE et reste a payer plafonne a 0")
    void surpaiement() {
        paiementSurFacture(facture("1000.00", "EMISE"), "1200", List.of(paiement("1200.00")));

        BtpFactureCompletDto resultat = service.enregistrerPaiement(9L, paiementDto("1200"));

        assertThat(resultat.getFacture().getStatut()).isEqualTo("PAYEE");
        assertThat(resultat.getTotalPaye()).isEqualByComparingTo("1200.00");
        assertThat(resultat.getResteAPayer()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Given une facture PAYEE When son unique paiement est supprime Then elle redevient EMISE")
    void suppressionPaiementRecalculeStatut() {
        BtpFacture f = facture("1000.00", "PAYEE");
        BtpPaiement p = paiement("1000.00");
        p.setId(3L);
        when(paiementRepository.findById(3L)).thenReturn(Optional.of(p));
        paiementSurFacture(f, "0", List.of());

        BtpFactureCompletDto resultat = service.supprimerPaiement(3L);

        verify(paiementRepository).delete(p);
        assertThat(resultat.getFacture().getStatut()).isEqualTo("EMISE");
        assertThat(resultat.getResteAPayer()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName(
            "Given une facture ANNULEE When je enregistre un paiement Then IllegalArgumentException et rien n'est persiste")
    void paiementSurFactureAnnulee() {
        when(factureRepository.findById(9L)).thenReturn(Optional.of(facture("1000.00", "ANNULEE")));

        assertThatThrownBy(() -> service.enregistrerPaiement(9L, paiementDto("100")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("annulee");
        verify(paiementRepository, never()).save(any(BtpPaiement.class));
    }

    @Test
    @DisplayName("Given un montant de paiement nul When je l'enregistre Then IllegalArgumentException")
    void paiementMontantNul() {
        when(factureRepository.findById(9L)).thenReturn(Optional.of(facture("1000.00", "EMISE")));

        assertThatThrownBy(() -> service.enregistrerPaiement(9L, paiementDto("0")))
                .isInstanceOf(IllegalArgumentException.class);
        verify(paiementRepository, never()).save(any(BtpPaiement.class));
    }

    @Test
    @DisplayName("Given une facture inconnue When j'enregistre un paiement Then IllegalArgumentException")
    void factureIntrouvable() {
        when(factureRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enregistrerPaiement(404L, paiementDto("10")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Facture introuvable");
    }

    @Test
    @DisplayName("Given une facture de 1180 TTC et 300 payes When je demande le reste a payer Then 880")
    void resteAPayer() {
        when(factureRepository.findById(9L)).thenReturn(Optional.of(facture("1180.00", "PARTIELLEMENT_PAYEE")));
        when(paiementRepository.findByFactureIdOrderByDatePaiementAscIdAsc(9L)).thenReturn(List.of(paiement("300.00")));

        assertThat(service.resteAPayer(9L)).isEqualByComparingTo("880.00");
    }

    @Test
    @DisplayName("Given une facture payee When je l'annule Then son statut persiste est ANNULEE")
    void annuler() {
        when(factureRepository.findById(9L)).thenReturn(Optional.of(facture("1000.00", "EMISE")));
        when(factureRepository.save(any(BtpFacture.class))).thenAnswer(i -> i.getArgument(0));

        BtpFactureCompletDto resultat = service.annuler(9L);

        ArgumentCaptor<BtpFacture> captor = ArgumentCaptor.forClass(BtpFacture.class);
        verify(factureRepository).save(captor.capture());
        assertThat(captor.getValue().getStatut()).isEqualTo("ANNULEE");
        assertThat(resultat.getFacture().getStatut()).isEqualTo("ANNULEE");
    }

    @Test
    @DisplayName(
            "Given deux lignes 2 x 100 et 1 x 50.005 et TVA 18% When je cree la facture Then HT 250.01 (ligne 50.01), TTC 295.01, statut EMISE, lignes liees")
    void creerAvecLignes() {
        BtpFactureDto entete = new BtpFactureDto();
        entete.setProjetId(1L);
        entete.setNumeroFacture("F-NEW");
        entete.setTauxTva(new BigDecimal("18.00"));
        BtpFactureLigneDto l1 = new BtpFactureLigneDto();
        l1.setDesignation("A");
        l1.setQuantite(new BigDecimal("2"));
        l1.setPrixUnitaireHt(new BigDecimal("100"));
        BtpFactureLigneDto l2 = new BtpFactureLigneDto();
        l2.setDesignation("B");
        l2.setQuantite(BigDecimal.ONE);
        l2.setPrixUnitaireHt(new BigDecimal("50.005"));
        BtpFactureCompletDto demande = new BtpFactureCompletDto();
        demande.setFacture(entete);
        demande.setLignes(List.of(l1, l2));
        when(projetRepository.existsById(1L)).thenReturn(true);
        when(factureRepository.existsByNumeroFacture("F-NEW")).thenReturn(false);
        when(factureRepository.save(any(BtpFacture.class))).thenAnswer(i -> {
            BtpFacture f = i.getArgument(0);
            f.setId(9L);
            return f;
        });
        when(ligneRepository.save(any(BtpFactureLigne.class))).thenAnswer(i -> i.getArgument(0));

        BtpFactureCompletDto resultat = service.creer(demande);

        ArgumentCaptor<BtpFacture> captor = ArgumentCaptor.forClass(BtpFacture.class);
        verify(factureRepository).save(captor.capture());
        assertThat(captor.getValue().getMontantHt()).isEqualByComparingTo("250.01");
        assertThat(captor.getValue().getMontantTtc()).isEqualByComparingTo("295.01");
        assertThat(captor.getValue().getStatut()).isEqualTo("EMISE");
        ArgumentCaptor<BtpFactureLigne> lignes = ArgumentCaptor.forClass(BtpFactureLigne.class);
        verify(ligneRepository, org.mockito.Mockito.times(2)).save(lignes.capture());
        assertThat(lignes.getAllValues())
                .extracting(BtpFactureLigne::getFactureId)
                .containsOnly(9L);
        assertThat(resultat.getFacture().getStatut()).isEqualTo("EMISE");
    }

    @Test
    @DisplayName("Given un numero de facture deja utilise When je cree Then IllegalArgumentException")
    void creerNumeroEnDoublon() {
        BtpFactureDto entete = new BtpFactureDto();
        entete.setProjetId(1L);
        entete.setNumeroFacture("F-1");
        BtpFactureCompletDto demande = new BtpFactureCompletDto();
        demande.setFacture(entete);
        when(projetRepository.existsById(1L)).thenReturn(true);
        when(factureRepository.existsByNumeroFacture("F-1")).thenReturn(true);

        assertThatThrownBy(() -> service.creer(demande)).isInstanceOf(IllegalArgumentException.class);
        verify(factureRepository, never()).save(any(BtpFacture.class));
    }

    @Test
    @DisplayName("Given un projet inconnu When je cree une facture Then IllegalArgumentException")
    void creerProjetInconnu() {
        BtpFactureDto entete = new BtpFactureDto();
        entete.setProjetId(42L);
        BtpFactureCompletDto demande = new BtpFactureCompletDto();
        demande.setFacture(entete);
        when(projetRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> service.creer(demande))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Projet introuvable");
    }

    @Test
    @DisplayName(
            "Given une situation VALIDEE de 1000 HT When je la facture avec TVA 18% Then facture SITUATION de TTC 1180 reprenant la ligne et situation FACTUREE")
    void creerDepuisSituationValidee() {
        BtpSituationTravaux situation = new BtpSituationTravaux();
        situation.setId(20L);
        situation.setProjetId(1L);
        situation.setStatut("VALIDEE");
        BtpSituationLigne sl = new BtpSituationLigne();
        sl.setId(5L);
        sl.setSituationId(20L);
        sl.setLigneDevisId(10L);
        sl.setQuantitePeriode(new BigDecimal("10"));
        sl.setMontantPeriodeHt(new BigDecimal("1000.00"));
        BtpLigneDevis ld = new BtpLigneDevis();
        ld.setId(10L);
        ld.setDesignation("Maconnerie");
        ld.setUnite("m2");
        ld.setPrixUnitaireHt(new BigDecimal("100.00"));
        when(situationRepository.findById(20L)).thenReturn(Optional.of(situation));
        when(situationLigneRepository.findBySituationIdOrderByIdAsc(20L)).thenReturn(List.of(sl));
        when(ligneDevisRepository.findAllById(List.of(10L))).thenReturn(List.of(ld));
        when(projetRepository.existsById(1L)).thenReturn(true);
        when(factureRepository.existsByNumeroFacture("F-S1")).thenReturn(false);
        when(factureRepository.save(any(BtpFacture.class))).thenAnswer(i -> {
            BtpFacture f = i.getArgument(0);
            f.setId(9L);
            return f;
        });
        when(ligneRepository.save(any(BtpFactureLigne.class))).thenAnswer(i -> i.getArgument(0));

        service.creerDepuisSituation(20L, "F-S1", new BigDecimal("18.00"));

        ArgumentCaptor<BtpFacture> factureCaptor = ArgumentCaptor.forClass(BtpFacture.class);
        verify(factureRepository).save(factureCaptor.capture());
        assertThat(factureCaptor.getValue().getSituationId()).isEqualTo(20L);
        assertThat(factureCaptor.getValue().getTypeFacture()).isEqualTo("SITUATION");
        assertThat(factureCaptor.getValue().getMontantHt()).isEqualByComparingTo("1000.00");
        assertThat(factureCaptor.getValue().getMontantTtc()).isEqualByComparingTo("1180.00");
        ArgumentCaptor<BtpFactureLigne> ligneCaptor = ArgumentCaptor.forClass(BtpFactureLigne.class);
        verify(ligneRepository).save(ligneCaptor.capture());
        assertThat(ligneCaptor.getValue().getDesignation()).isEqualTo("Maconnerie");
        assertThat(ligneCaptor.getValue().getSituationLigneId()).isEqualTo(5L);
        assertThat(ligneCaptor.getValue().getMontantHt()).isEqualByComparingTo("1000.00");
        assertThat(situation.getStatut()).isEqualTo("FACTUREE");
        verify(situationRepository).save(situation);
    }

    @Test
    @DisplayName(
            "Given une situation encore en BROUILLON When je la facture Then IllegalArgumentException et aucune facture")
    void creerDepuisSituationNonValidee() {
        BtpSituationTravaux situation = new BtpSituationTravaux();
        situation.setId(20L);
        situation.setStatut("BROUILLON");
        when(situationRepository.findById(20L)).thenReturn(Optional.of(situation));

        assertThatThrownBy(() -> service.creerDepuisSituation(20L, "F-S1", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("VALIDEE");
        verify(factureRepository, never()).save(any(BtpFacture.class));
    }

    @Test
    @DisplayName("Given une facture When je la supprime Then ses lignes sont supprimees puis la facture")
    void supprimer() {
        BtpFacture f = facture("100.00", "EMISE");
        when(factureRepository.findById(9L)).thenReturn(Optional.of(f));

        service.supprimer(9L);

        org.mockito.InOrder ordre = org.mockito.Mockito.inOrder(ligneRepository, factureRepository);
        ordre.verify(ligneRepository).deleteByFactureId(9L);
        ordre.verify(factureRepository).delete(f);
    }
}
