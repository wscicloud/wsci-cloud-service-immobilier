package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationCompleteDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationTravauxDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.mapper.BtpSituationLigneMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpSituationTravauxMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
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
class BtpSituationServiceImplTest {

    private static final Long PROJET = 1L;
    private static final Long SITUATION = 20L;

    @Mock
    private BtpSituationTravauxRepository situationRepository;

    @Mock
    private BtpSituationLigneRepository ligneRepository;

    @Mock
    private BtpLigneDevisRepository ligneDevisRepository;

    @Mock
    private BtpDevisRepository devisRepository;

    @Mock
    private BtpProjetRepository projetRepository;

    @Spy
    private BtpSituationTravauxMapper situationMapper = Mappers.getMapper(BtpSituationTravauxMapper.class);

    @Spy
    private BtpSituationLigneMapper ligneMapper = Mappers.getMapper(BtpSituationLigneMapper.class);

    @InjectMocks
    private BtpSituationServiceImpl service;

    private BtpSituationTravaux courante;

    @BeforeEach
    void init() {
        courante = situation(SITUATION, "2025-03-31", "0.00");
        courante.setNumeroSituation("S3");
    }

    private static BtpSituationTravaux situation(Long id, String date, String montantPeriode) {
        BtpSituationTravaux s = new BtpSituationTravaux();
        s.setId(id);
        s.setProjetId(PROJET);
        s.setNumeroSituation("S" + id);
        s.setDateSituation(LocalDate.parse(date));
        s.setMontantPeriodeHt(new BigDecimal(montantPeriode));
        s.setStatut("BROUILLON");
        return s;
    }

    private static BtpSituationLigne ligne(
            Long id, Long situationId, Long ligneDevisId, String quantitePeriode, String montant) {
        BtpSituationLigne l = new BtpSituationLigne();
        l.setId(id);
        l.setSituationId(situationId);
        l.setLigneDevisId(ligneDevisId);
        l.setQuantitePeriode(new BigDecimal(quantitePeriode));
        l.setMontantPeriodeHt(new BigDecimal(montant));
        return l;
    }

    private static BtpSituationLigneDto ligneDto(Long ligneDevisId, String precedente, String periode) {
        BtpSituationLigneDto dto = new BtpSituationLigneDto();
        dto.setLigneDevisId(ligneDevisId);
        dto.setQuantitePrecedente(precedente == null ? null : new BigDecimal(precedente));
        dto.setQuantitePeriode(new BigDecimal(periode));
        return dto;
    }

    private void ligneDevisDuProjet(Long ligneDevisId, String prix) {
        BtpLigneDevis ld = new BtpLigneDevis();
        ld.setId(ligneDevisId);
        ld.setDevisId(500L);
        ld.setPrixUnitaireHt(new BigDecimal(prix));
        BtpDevis devis = new BtpDevis();
        devis.setId(500L);
        devis.setProjetId(PROJET);
        when(ligneDevisRepository.findById(ligneDevisId)).thenReturn(Optional.of(ld));
        when(devisRepository.findById(500L)).thenReturn(Optional.of(devis));
    }

    private void budgetProjet(String... montants) {
        List<BtpDevis> devis = new java.util.ArrayList<>();
        for (String m : montants) {
            BtpDevis d = new BtpDevis();
            d.setStatut("VALIDE");
            d.setMontantHt(new BigDecimal(m));
            devis.add(d);
        }
        when(devisRepository.findByProjetIdAndStatutIn(PROJET, Set.of("VALIDE", "ACCEPTE", "EN_COURS")))
                .thenReturn(devis);
    }

    @Test
    @DisplayName(
            "Given une situation precedente de 1500 et un budget de 10000 When j'ajoute une ligne 10 x 100 Then la situation passe a periode 1000, cumule 2500, avancement 25%")
    void ajouterLigneRecalculeLaSituation() {
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(ligneRepository.existsBySituationIdAndLigneDevisId(SITUATION, 10L)).thenReturn(false);
        ligneDevisDuProjet(10L, "100.00");
        when(situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(PROJET))
                .thenReturn(List.of(situation(19L, "2025-02-28", "1500.00"), courante));
        when(ligneRepository.save(any(BtpSituationLigne.class))).thenAnswer(i -> i.getArgument(0));
        when(ligneRepository.findBySituationIdIn(List.of(19L))).thenReturn(List.of());
        when(ligneRepository.findBySituationIdOrderByIdAsc(SITUATION))
                .thenReturn(List.of(ligne(1L, SITUATION, 10L, "10", "1000.00")));
        budgetProjet("10000.00");
        when(situationRepository.save(any(BtpSituationTravaux.class))).thenAnswer(i -> i.getArgument(0));

        BtpSituationCompleteDto resultat = service.ajouterLigne(SITUATION, ligneDto(10L, null, "10"));

        ArgumentCaptor<BtpSituationLigne> ligneCaptor = ArgumentCaptor.forClass(BtpSituationLigne.class);
        verify(ligneRepository).save(ligneCaptor.capture());
        assertThat(ligneCaptor.getValue().getMontantPeriodeHt()).isEqualByComparingTo("1000.00");
        assertThat(ligneCaptor.getValue().getQuantitePrecedente()).isEqualByComparingTo("0");
        assertThat(ligneCaptor.getValue().getQuantiteCumulee()).isEqualByComparingTo("10");

        ArgumentCaptor<BtpSituationTravaux> situationCaptor = ArgumentCaptor.forClass(BtpSituationTravaux.class);
        verify(situationRepository).save(situationCaptor.capture());
        assertThat(situationCaptor.getValue().getMontantPeriodeHt()).isEqualByComparingTo("1000.00");
        assertThat(situationCaptor.getValue().getMontantCumuleHt()).isEqualByComparingTo("2500.00");
        assertThat(situationCaptor.getValue().getTauxAvancementPct()).isEqualByComparingTo("25.00");
        assertThat(resultat.getLignes()).hasSize(1);
    }

    @Test
    @DisplayName(
            "Given une quantite precedente non fournie et 4 unites realisees dans une situation anterieure When j'ajoute 3 Then precedente = 4 et cumulee = 7")
    void quantitePrecedenteDeduiteDesSituationsAnterieures() {
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(ligneRepository.existsBySituationIdAndLigneDevisId(SITUATION, 10L)).thenReturn(false);
        ligneDevisDuProjet(10L, "50.00");
        when(situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(PROJET))
                .thenReturn(List.of(
                        situation(19L, "2025-02-28", "200.00"),
                        courante,
                        situation(21L, "2025-04-30", "999.00"))); // posterieure : ignoree
        when(ligneRepository.findBySituationIdIn(List.of(19L)))
                .thenReturn(List.of(ligne(1L, 19L, 10L, "4", "200.00"), ligne(2L, 19L, 11L, "9", "0")));
        when(ligneRepository.save(any(BtpSituationLigne.class))).thenAnswer(i -> i.getArgument(0));
        when(ligneRepository.findBySituationIdOrderByIdAsc(SITUATION)).thenReturn(List.of());
        budgetProjet("1000.00");
        when(situationRepository.save(any(BtpSituationTravaux.class))).thenAnswer(i -> i.getArgument(0));

        service.ajouterLigne(SITUATION, ligneDto(10L, null, "3"));

        ArgumentCaptor<BtpSituationLigne> captor = ArgumentCaptor.forClass(BtpSituationLigne.class);
        verify(ligneRepository).save(captor.capture());
        assertThat(captor.getValue().getQuantitePrecedente()).isEqualByComparingTo("4");
        assertThat(captor.getValue().getQuantiteCumulee()).isEqualByComparingTo("7");
        assertThat(captor.getValue().getMontantPeriodeHt()).isEqualByComparingTo("150.00");
    }

    @Test
    @DisplayName(
            "Given une situation de deux lignes When je supprime une ligne Then les totaux sont recalcules sur la ligne restante")
    void supprimerLigneRecalcule() {
        BtpSituationLigne restante = ligne(1L, SITUATION, 10L, "2", "300.00");
        BtpSituationLigne aSupprimer = ligne(2L, SITUATION, 11L, "5", "700.00");
        courante.setMontantPeriodeHt(new BigDecimal("1000.00"));
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(ligneRepository.findById(2L)).thenReturn(Optional.of(aSupprimer));
        when(ligneRepository.findBySituationIdOrderByIdAsc(SITUATION)).thenReturn(List.of(restante));
        when(situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(PROJET))
                .thenReturn(List.of(courante));
        budgetProjet("600.00");
        when(situationRepository.save(any(BtpSituationTravaux.class))).thenAnswer(i -> i.getArgument(0));

        service.supprimerLigne(SITUATION, 2L);

        verify(ligneRepository).delete(aSupprimer);
        ArgumentCaptor<BtpSituationTravaux> captor = ArgumentCaptor.forClass(BtpSituationTravaux.class);
        verify(situationRepository).save(captor.capture());
        assertThat(captor.getValue().getMontantPeriodeHt()).isEqualByComparingTo("300.00");
        assertThat(captor.getValue().getMontantCumuleHt()).isEqualByComparingTo("300.00");
        assertThat(captor.getValue().getTauxAvancementPct()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("Given aucun devis valide When je recalcule Then le taux d'avancement vaut 0 sans erreur")
    void budgetNulPasDeDivisionParZero() {
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(ligneRepository.findById(2L)).thenReturn(Optional.of(ligne(2L, SITUATION, 11L, "5", "700.00")));
        when(ligneRepository.findBySituationIdOrderByIdAsc(SITUATION))
                .thenReturn(List.of(ligne(1L, SITUATION, 10L, "1", "100.00")));
        when(situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(PROJET))
                .thenReturn(List.of(courante));
        budgetProjet();
        when(situationRepository.save(any(BtpSituationTravaux.class))).thenAnswer(i -> i.getArgument(0));

        service.supprimerLigne(SITUATION, 2L);

        ArgumentCaptor<BtpSituationTravaux> captor = ArgumentCaptor.forClass(BtpSituationTravaux.class);
        verify(situationRepository).save(captor.capture());
        assertThat(captor.getValue().getMontantCumuleHt()).isEqualByComparingTo("100.00");
        assertThat(captor.getValue().getTauxAvancementPct()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Given une ligne deja presente When je l'ajoute a nouveau Then IllegalArgumentException")
    void ligneEnDoublon() {
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(ligneRepository.existsBySituationIdAndLigneDevisId(SITUATION, 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.ajouterLigne(SITUATION, ligneDto(10L, null, "1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("deja presente");
        verify(ligneRepository, never()).save(any(BtpSituationLigne.class));
    }

    @Test
    @DisplayName("Given une ligne de devis d'un autre projet When je l'ajoute Then IllegalArgumentException")
    void ligneDevisAutreProjet() {
        BtpLigneDevis ld = new BtpLigneDevis();
        ld.setId(10L);
        ld.setDevisId(500L);
        BtpDevis devisAutreProjet = new BtpDevis();
        devisAutreProjet.setId(500L);
        devisAutreProjet.setProjetId(77L);
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(ligneRepository.existsBySituationIdAndLigneDevisId(SITUATION, 10L)).thenReturn(false);
        when(ligneDevisRepository.findById(10L)).thenReturn(Optional.of(ld));
        when(devisRepository.findById(500L)).thenReturn(Optional.of(devisAutreProjet));

        assertThatThrownBy(() -> service.ajouterLigne(SITUATION, ligneDto(10L, null, "1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("projet");
        verify(ligneRepository, never()).save(any(BtpSituationLigne.class));
    }

    @Test
    @DisplayName("Given une quantite de periode negative When j'ajoute la ligne Then IllegalArgumentException")
    void quantiteNegative() {
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(ligneRepository.existsBySituationIdAndLigneDevisId(SITUATION, 10L)).thenReturn(false);

        assertThatThrownBy(() -> service.ajouterLigne(SITUATION, ligneDto(10L, null, "-2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
        verify(ligneRepository, never()).save(any(BtpSituationLigne.class));
    }

    @Test
    @DisplayName("Given une ligne de devis inconnue When j'ajoute la ligne Then IllegalArgumentException")
    void ligneDevisInconnue() {
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(ligneRepository.existsBySituationIdAndLigneDevisId(SITUATION, 10L)).thenReturn(false);
        when(ligneDevisRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ajouterLigne(SITUATION, ligneDto(10L, null, "1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ligne de devis introuvable");
    }

    @Test
    @DisplayName("Given une situation inconnue When je la lis Then IllegalArgumentException")
    void situationIntrouvable() {
        when(situationRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.lire(404L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Situation introuvable");
    }

    @Test
    @DisplayName("Given un projet inconnu When je cree une situation Then IllegalArgumentException")
    void creerProjetInconnu() {
        BtpSituationTravauxDto dto = new BtpSituationTravauxDto();
        dto.setProjetId(42L);
        dto.setNumeroSituation("S1");
        when(projetRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> service.creer(dto)).isInstanceOf(IllegalArgumentException.class);
        verify(situationRepository, never()).save(any(BtpSituationTravaux.class));
    }

    @Test
    @DisplayName("Given un numero de situation deja utilise dans le projet When je cree Then IllegalArgumentException")
    void creerNumeroEnDoublon() {
        BtpSituationTravauxDto dto = new BtpSituationTravauxDto();
        dto.setProjetId(PROJET);
        dto.setNumeroSituation("S1");
        when(projetRepository.existsById(PROJET)).thenReturn(true);
        when(situationRepository.existsByProjetIdAndNumeroSituation(PROJET, "S1"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.creer(dto)).isInstanceOf(IllegalArgumentException.class);
        verify(situationRepository, never()).save(any(BtpSituationTravaux.class));
    }

    @Test
    @DisplayName(
            "Given une nouvelle situation When je la cree Then elle est en BROUILLON avec totaux a 0 et les montants fournis par le client sont ignores")
    void creerInitialiseLesTotaux() {
        BtpSituationTravauxDto dto = new BtpSituationTravauxDto();
        dto.setProjetId(PROJET);
        dto.setNumeroSituation("S1");
        dto.setMontantCumuleHt(new BigDecimal("123456"));
        when(projetRepository.existsById(PROJET)).thenReturn(true);
        when(situationRepository.existsByProjetIdAndNumeroSituation(PROJET, "S1"))
                .thenReturn(false);
        when(situationRepository.save(any(BtpSituationTravaux.class))).thenAnswer(i -> {
            BtpSituationTravaux s = i.getArgument(0);
            if (s.getId() == null) {
                s.setId(30L);
            }
            return s;
        });
        when(ligneRepository.findBySituationIdOrderByIdAsc(30L)).thenReturn(List.of());
        when(situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(PROJET))
                .thenReturn(List.of());
        budgetProjet("1000.00");

        BtpSituationTravauxDto resultat = service.creer(dto);

        assertThat(resultat.getStatut()).isEqualTo("BROUILLON");
        assertThat(resultat.getMontantCumuleHt()).isEqualByComparingTo("0");
        assertThat(resultat.getTauxAvancementPct()).isEqualByComparingTo("0");
        assertThat(resultat.getDateSituation()).isNotNull();
    }

    @Test
    @DisplayName(
            "Given un statut inconnu When je change le statut Then IllegalArgumentException ; VALIDEE est accepte et persiste")
    void changerStatut() {
        assertThatThrownBy(() -> service.changerStatut(SITUATION, "XXX")).isInstanceOf(IllegalArgumentException.class);
        verify(situationRepository, never()).save(any(BtpSituationTravaux.class));

        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));
        when(situationRepository.save(any(BtpSituationTravaux.class))).thenAnswer(i -> i.getArgument(0));

        BtpSituationTravauxDto resultat = service.changerStatut(SITUATION, "VALIDEE");

        assertThat(resultat.getStatut()).isEqualTo("VALIDEE");
        assertThat(courante.getStatut()).isEqualTo("VALIDEE");
    }

    @Test
    @DisplayName("Given une situation When je la supprime Then ses lignes sont supprimees avant elle")
    void supprimer() {
        when(situationRepository.findById(SITUATION)).thenReturn(Optional.of(courante));

        service.supprimer(SITUATION);

        InOrder ordre = Mockito.inOrder(ligneRepository, situationRepository);
        ordre.verify(ligneRepository).deleteBySituationId(SITUATION);
        ordre.verify(situationRepository).delete(courante);
    }
}
