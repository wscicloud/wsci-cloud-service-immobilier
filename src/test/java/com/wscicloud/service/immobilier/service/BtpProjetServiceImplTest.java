package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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

import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;
import com.wscicloud.service.immobilier.domain.mapper.BtpProjetMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BtpProjetServiceImplTest {

    @Mock
    private BtpProjetRepository projetRepository;

    @Spy
    private BtpProjetMapper projetMapper = Mappers.getMapper(BtpProjetMapper.class);

    @InjectMocks
    private BtpProjetServiceImpl service;

    private static BtpProjetDto dto(String code) {
        BtpProjetDto dto = new BtpProjetDto();
        dto.setCodeProjet(code);
        dto.setNomProjet("Residence");
        dto.setClientNom("Client");
        return dto;
    }

    @Test
    @DisplayName(
            "Given un projet sans statut ni devise ni budgets When je le cree Then EN_COURS, FCFA, budgets a 0 et date de creation renseignee")
    void creerAppliqueLesDefauts() {
        when(projetRepository.existsByCodeProjet("P1")).thenReturn(false);
        when(projetRepository.save(any(BtpProjet.class))).thenAnswer(i -> i.getArgument(0));

        BtpProjetDto resultat = service.creer(dto("P1"));

        ArgumentCaptor<BtpProjet> captor = ArgumentCaptor.forClass(BtpProjet.class);
        verify(projetRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getCreeLe()).isNotNull();
        assertThat(resultat.getStatut()).isEqualTo("EN_COURS");
        assertThat(resultat.getDevise()).isEqualTo("FCFA");
        assertThat(resultat.getBudgetPrevuHt()).isEqualByComparingTo("0");
        assertThat(resultat.getMargeReelTtc()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName(
            "Given un code projet deja utilise When je cree Then IllegalArgumentException et rien n'est sauvegarde")
    void creerCodeEnDoublon() {
        when(projetRepository.existsByCodeProjet("P1")).thenReturn(true);

        assertThatThrownBy(() -> service.creer(dto("P1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("code projet");
        verify(projetRepository, never()).save(any(BtpProjet.class));
    }

    @Test
    @DisplayName(
            "Given un projet existant When je le modifie sans statut ni devise Then la date de creation, le statut et la devise existants sont conserves")
    void modifierConserveLesValeursExistantes() {
        BtpProjet existant = new BtpProjet();
        existant.setId(4L);
        existant.setCreeLe(OffsetDateTime.parse("2025-01-01T10:00:00Z"));
        existant.setStatut("TERMINE");
        existant.setDevise("EUR");
        when(projetRepository.findById(4L)).thenReturn(Optional.of(existant));
        when(projetRepository.save(any(BtpProjet.class))).thenAnswer(i -> i.getArgument(0));
        BtpProjetDto dto = dto("P1");
        dto.setId(4L);
        dto.setNomProjet("Nouveau nom");
        dto.setBudgetPrevuHt(new BigDecimal("5000.00"));

        BtpProjetDto resultat = service.modifier(dto);

        assertThat(resultat.getNomProjet()).isEqualTo("Nouveau nom");
        assertThat(resultat.getBudgetPrevuHt()).isEqualByComparingTo("5000.00");
        assertThat(resultat.getStatut()).isEqualTo("TERMINE");
        assertThat(resultat.getDevise()).isEqualTo("EUR");
        assertThat(resultat.getCreeLe()).isEqualTo(existant.getCreeLe());
    }

    @Test
    @DisplayName("Given un projet sans id When je le modifie Then IllegalArgumentException")
    void modifierSansId() {
        assertThatThrownBy(() -> service.modifier(dto("P1"))).isInstanceOf(IllegalArgumentException.class);
        verify(projetRepository, never()).save(any(BtpProjet.class));
    }

    @Test
    @DisplayName("Given un projet inconnu When je le lis ou le supprime Then IllegalArgumentException")
    void projetIntrouvable() {
        when(projetRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.lire(404L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.supprimer(404L)).isInstanceOf(IllegalArgumentException.class);
        verify(projetRepository, never()).delete(any(BtpProjet.class));
    }

    @Test
    @DisplayName(
            "Given une recherche vide When je recherche Then tous les projets sont retournes ; avec un terme, le terme est trim avant la requete")
    void rechercher() {
        BtpProjet p = new BtpProjet();
        p.setId(1L);
        p.setCodeProjet("P1");
        when(projetRepository.findAllBySocieteIdOrderByCreeLeDesc(7L)).thenReturn(List.of(p));
        when(projetRepository.rechercher(7L, "villa")).thenReturn(List.of(p));

        assertThat(service.rechercher(7L, "  "))
                .extracting(BtpProjetDto::getCodeProjet)
                .containsExactly("P1");
        assertThat(service.rechercher(7L, " villa "))
                .extracting(BtpProjetDto::getId)
                .containsExactly(1L);
        verify(projetRepository).rechercher(7L, "villa");
    }
}
