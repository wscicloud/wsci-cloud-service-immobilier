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

import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.mapper.BtpDevisMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpLigneDevisMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BtpDevisServiceImplTest {

    @Mock
    private BtpDevisRepository devisRepository;

    @Mock
    private BtpLigneDevisRepository ligneRepository;

    @Mock
    private BtpProjetRepository projetRepository;

    @Spy
    private BtpDevisMapper devisMapper = Mappers.getMapper(BtpDevisMapper.class);

    @Spy
    private BtpLigneDevisMapper ligneMapper = Mappers.getMapper(BtpLigneDevisMapper.class);

    @InjectMocks
    private BtpDevisServiceImpl service;

    private static BtpDevis devisExistant(long id, String tva) {
        BtpDevis d = new BtpDevis();
        d.setId(id);
        d.setProjetId(1L);
        d.setNumeroDevis("DV-1");
        d.setTauxTva(new BigDecimal(tva));
        d.setStatut("BROUILLON");
        d.setMontantHt(BigDecimal.ZERO);
        d.setMontantTtc(BigDecimal.ZERO);
        return d;
    }

    private static BtpLigneDevis ligne(long id, long devisId, String montant) {
        BtpLigneDevis l = new BtpLigneDevis();
        l.setId(id);
        l.setDevisId(devisId);
        l.setMontantHt(new BigDecimal(montant));
        return l;
    }

    private static BtpLigneDevisDto ligneDto(String quantite, String prix) {
        BtpLigneDevisDto dto = new BtpLigneDevisDto();
        dto.setDesignation("Beton");
        dto.setQuantite(new BigDecimal(quantite));
        dto.setPrixUnitaireHt(new BigDecimal(prix));
        return dto;
    }

    @Test
    @DisplayName(
            "Given un devis TVA 18% When j'ajoute une ligne 3.333 x 10.005 Then la ligne est persistee a 33.35 et le devis passe a HT 133.35 / TTC 157.35")
    void ajouterLigneRecalculeLesTotaux() {
        BtpDevis devis = devisExistant(5L, "18.00");
        when(devisRepository.findById(5L)).thenReturn(Optional.of(devis));
        when(ligneRepository.save(any(BtpLigneDevis.class))).thenAnswer(i -> i.getArgument(0));
        when(ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(5L))
                .thenReturn(List.of(ligne(1, 5, "100.00"), ligne(2, 5, "33.35")));
        when(devisRepository.save(any(BtpDevis.class))).thenAnswer(i -> i.getArgument(0));

        BtpDevisDto resultat = service.ajouterLigne(5L, ligneDto("3.333", "10.005"));

        ArgumentCaptor<BtpLigneDevis> ligneCaptor = ArgumentCaptor.forClass(BtpLigneDevis.class);
        verify(ligneRepository).save(ligneCaptor.capture());
        assertThat(ligneCaptor.getValue().getDevisId()).isEqualTo(5L);
        assertThat(ligneCaptor.getValue().getMontantHt()).isEqualByComparingTo("33.35");

        ArgumentCaptor<BtpDevis> devisCaptor = ArgumentCaptor.forClass(BtpDevis.class);
        verify(devisRepository).save(devisCaptor.capture());
        assertThat(devisCaptor.getValue().getMontantHt()).isEqualByComparingTo("133.35");
        assertThat(devisCaptor.getValue().getMontantTtc()).isEqualByComparingTo("157.35");
        assertThat(resultat.getLignes()).hasSize(2);
        assertThat(resultat.getMontantTtc()).isEqualByComparingTo("157.35");
    }

    @Test
    @DisplayName(
            "Given un devis de deux lignes When je supprime une ligne Then le devis est recalcule sur la ligne restante")
    void supprimerLigneRecalculeLesTotaux() {
        BtpDevis devis = devisExistant(5L, "10.00");
        BtpLigneDevis aSupprimer = ligne(2, 5, "40.00");
        when(devisRepository.findById(5L)).thenReturn(Optional.of(devis));
        when(ligneRepository.findById(2L)).thenReturn(Optional.of(aSupprimer));
        when(ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(5L)).thenReturn(List.of(ligne(1, 5, "60.00")));
        when(devisRepository.save(any(BtpDevis.class))).thenAnswer(i -> i.getArgument(0));

        service.supprimerLigne(5L, 2L);

        verify(ligneRepository).delete(aSupprimer);
        ArgumentCaptor<BtpDevis> captor = ArgumentCaptor.forClass(BtpDevis.class);
        verify(devisRepository).save(captor.capture());
        assertThat(captor.getValue().getMontantHt()).isEqualByComparingTo("60.00");
        assertThat(captor.getValue().getMontantTtc()).isEqualByComparingTo("66.00");
    }

    @Test
    @DisplayName(
            "Given une ligne appartenant a un autre devis When je la supprime Then IllegalArgumentException et rien n'est supprime")
    void supprimerLigneAutreDevis() {
        when(devisRepository.findById(5L)).thenReturn(Optional.of(devisExistant(5L, "0")));
        when(ligneRepository.findById(2L)).thenReturn(Optional.of(ligne(2, 99, "40.00")));

        assertThatThrownBy(() -> service.supprimerLigne(5L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("n'appartient pas");
        verify(ligneRepository, never()).delete(any(BtpLigneDevis.class));
    }

    @Test
    @DisplayName(
            "Given une ligne existante When je la modifie Then l'id et la date de creation sont conserves et le montant recalcule")
    void modifierLigne() {
        BtpDevis devis = devisExistant(5L, "0");
        BtpLigneDevis existante = ligne(2, 5, "40.00");
        existante.setCreeLe(java.time.OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        when(devisRepository.findById(5L)).thenReturn(Optional.of(devis));
        when(ligneRepository.findById(2L)).thenReturn(Optional.of(existante));
        when(ligneRepository.save(any(BtpLigneDevis.class))).thenAnswer(i -> i.getArgument(0));
        when(ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(5L)).thenReturn(List.of());
        when(devisRepository.save(any(BtpDevis.class))).thenAnswer(i -> i.getArgument(0));

        service.modifierLigne(5L, 2L, ligneDto("2", "12.50"));

        ArgumentCaptor<BtpLigneDevis> captor = ArgumentCaptor.forClass(BtpLigneDevis.class);
        verify(ligneRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(2L);
        assertThat(captor.getValue().getCreeLe()).isEqualTo(existante.getCreeLe());
        assertThat(captor.getValue().getMontantHt()).isEqualByComparingTo("25.00");
    }

    @Test
    @DisplayName(
            "Given une quantite negative When j'ajoute la ligne Then IllegalArgumentException et aucune ligne persistee")
    void quantiteNegativeRejetee() {
        when(devisRepository.findById(5L)).thenReturn(Optional.of(devisExistant(5L, "0")));

        assertThatThrownBy(() -> service.ajouterLigne(5L, ligneDto("-1", "10")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
        verify(ligneRepository, never()).save(any(BtpLigneDevis.class));
    }

    @Test
    @DisplayName("Given un devis inconnu When j'ajoute une ligne Then IllegalArgumentException")
    void devisIntrouvable() {
        when(devisRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ajouterLigne(404L, ligneDto("1", "1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Devis introuvable");
    }

    @Test
    @DisplayName("Given un projet inconnu When je cree un devis Then IllegalArgumentException")
    void creerProjetInconnu() {
        BtpDevisDto dto = new BtpDevisDto();
        dto.setProjetId(42L);
        dto.setNumeroDevis("DV-X");
        when(projetRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> service.creer(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Projet introuvable");
        verify(devisRepository, never()).save(any(BtpDevis.class));
    }

    @Test
    @DisplayName("Given un numero de devis deja utilise When je cree Then IllegalArgumentException")
    void creerNumeroDoublon() {
        BtpDevisDto dto = new BtpDevisDto();
        dto.setProjetId(1L);
        dto.setNumeroDevis("DV-1");
        when(projetRepository.existsById(1L)).thenReturn(true);
        when(devisRepository.existsByNumeroDevis("DV-1")).thenReturn(true);

        assertThatThrownBy(() -> service.creer(dto)).isInstanceOf(IllegalArgumentException.class);
        verify(devisRepository, never()).save(any(BtpDevis.class));
    }

    @Test
    @DisplayName(
            "Given un devis avec deux lignes et TVA 20% When je le cree Then statut BROUILLON, lignes liees au devis et totaux HT 300 / TTC 360")
    void creerAvecLignes() {
        BtpDevisDto dto = new BtpDevisDto();
        dto.setProjetId(1L);
        dto.setNumeroDevis("DV-2");
        dto.setTauxTva(new BigDecimal("20.00"));
        dto.setMontantHt(new BigDecimal("999999")); // doit etre ignore : recalcule
        dto.setLignes(List.of(ligneDto("2", "100"), ligneDto("1", "100")));
        when(projetRepository.existsById(1L)).thenReturn(true);
        when(devisRepository.existsByNumeroDevis("DV-2")).thenReturn(false);
        when(devisRepository.save(any(BtpDevis.class))).thenAnswer(i -> {
            BtpDevis d = i.getArgument(0);
            d.setId(7L);
            return d;
        });
        when(ligneRepository.save(any(BtpLigneDevis.class))).thenAnswer(i -> i.getArgument(0));
        when(ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(7L))
                .thenReturn(List.of(ligne(1, 7, "200.00"), ligne(2, 7, "100.00")));

        BtpDevisDto resultat = service.creer(dto);

        ArgumentCaptor<BtpLigneDevis> lignes = ArgumentCaptor.forClass(BtpLigneDevis.class);
        verify(ligneRepository, org.mockito.Mockito.times(2)).save(lignes.capture());
        assertThat(lignes.getAllValues()).extracting(BtpLigneDevis::getDevisId).containsOnly(7L);
        assertThat(resultat.getStatut()).isEqualTo("BROUILLON");
        assertThat(resultat.getMontantHt()).isEqualByComparingTo("300.00");
        assertThat(resultat.getMontantTtc()).isEqualByComparingTo("360.00");
        assertThat(resultat.getDateDevis()).isNotNull();
    }

    @Test
    @DisplayName(
            "Given un statut inconnu When je change le statut Then IllegalArgumentException et le devis n'est pas sauvegarde")
    void changerStatutInvalide() {
        assertThatThrownBy(() -> service.changerStatut(5L, "NIMPORTEQUOI"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Statut de devis invalide");
        verify(devisRepository, never()).save(any(BtpDevis.class));
    }

    @Test
    @DisplayName("Given un devis brouillon When je le passe a VALIDE Then le statut persiste est VALIDE")
    void changerStatutValide() {
        BtpDevis devis = devisExistant(5L, "0");
        when(devisRepository.findById(5L)).thenReturn(Optional.of(devis));
        when(devisRepository.save(any(BtpDevis.class))).thenAnswer(i -> i.getArgument(0));
        when(ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(5L)).thenReturn(List.of());

        BtpDevisDto resultat = service.changerStatut(5L, "VALIDE");

        ArgumentCaptor<BtpDevis> captor = ArgumentCaptor.forClass(BtpDevis.class);
        verify(devisRepository).save(captor.capture());
        assertThat(captor.getValue().getStatut()).isEqualTo("VALIDE");
        assertThat(resultat.getStatut()).isEqualTo("VALIDE");
    }

    @Test
    @DisplayName("Given un devis When je le supprime Then ses lignes sont supprimees avant le devis")
    void supprimerDevis() {
        BtpDevis devis = devisExistant(5L, "0");
        when(devisRepository.findById(5L)).thenReturn(Optional.of(devis));

        service.supprimer(5L);

        org.mockito.InOrder ordre = org.mockito.Mockito.inOrder(ligneRepository, devisRepository);
        ordre.verify(ligneRepository).deleteByDevisId(5L);
        ordre.verify(devisRepository).delete(devis);
    }

    @Test
    @DisplayName(
            "Given un changement de TVA de 0 a 18 When je modifie l'entete Then le TTC est recalcule depuis les lignes")
    void modifierEnteteRecalculeLeTtc() {
        BtpDevis devis = devisExistant(5L, "0");
        when(devisRepository.findById(5L)).thenReturn(Optional.of(devis));
        when(ligneRepository.findByDevisIdOrderByOrdreAffichageAscIdAsc(5L))
                .thenReturn(List.of(ligne(1, 5, "1000.00")));
        when(devisRepository.save(any(BtpDevis.class))).thenAnswer(i -> i.getArgument(0));
        BtpDevisDto dto = new BtpDevisDto();
        dto.setId(5L);
        dto.setNumeroDevis("DV-1");
        dto.setTauxTva(new BigDecimal("18.00"));

        BtpDevisDto resultat = service.modifier(dto);

        assertThat(resultat.getMontantHt()).isEqualByComparingTo("1000.00");
        assertThat(resultat.getMontantTtc()).isEqualByComparingTo("1180.00");
    }
}
