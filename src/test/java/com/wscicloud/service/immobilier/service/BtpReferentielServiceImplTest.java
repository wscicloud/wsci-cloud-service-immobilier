package com.wscicloud.service.immobilier.service;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpLotDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpPosteDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpSousTacheDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpTacheDto;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpLot;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpPoste;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpSousTache;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpTache;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpLotMapper;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpPosteMapper;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpSousTacheMapper;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpTacheMapper;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpLotRepository;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpPosteRepository;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpSousTacheRepository;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpTacheRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BtpReferentielServiceImplTest {

    @Mock
    private RefmBtpLotRepository lotRepository;

    @Mock
    private RefmBtpPosteRepository posteRepository;

    @Mock
    private RefmBtpTacheRepository tacheRepository;

    @Mock
    private RefmBtpSousTacheRepository sousTacheRepository;

    @Spy
    private RefmBtpLotMapper lotMapper = Mappers.getMapper(RefmBtpLotMapper.class);

    @Spy
    private RefmBtpPosteMapper posteMapper = Mappers.getMapper(RefmBtpPosteMapper.class);

    @Spy
    private RefmBtpTacheMapper tacheMapper = Mappers.getMapper(RefmBtpTacheMapper.class);

    @Spy
    private RefmBtpSousTacheMapper sousTacheMapper = Mappers.getMapper(RefmBtpSousTacheMapper.class);

    @InjectMocks
    private BtpReferentielServiceImpl service;

    @Test
    @DisplayName(
            "Given des lots en base When je liste les lots Then tous les champs sont exposes dans l'ordre du repository")
    void listerLots() {
        RefmBtpLot lot = new RefmBtpLot();
        lot.setId(1L);
        lot.setCode("GO");
        lot.setLibelle("Gros oeuvre");
        lot.setDescription("Structure");
        when(lotRepository.findAllByOrderByIdAsc()).thenReturn(List.of(lot));

        List<RefmBtpLotDto> lots = service.listerLots();

        assertThat(lots).singleElement().satisfies(l -> {
            assertThat(l.getId()).isEqualTo(1L);
            assertThat(l.getCode()).isEqualTo("GO");
            assertThat(l.getLibelle()).isEqualTo("Gros oeuvre");
            assertThat(l.getDescription()).isEqualTo("Structure");
        });
    }

    @Test
    @DisplayName("Given un lot When je liste ses postes Then les postes du lot sont retournes avec leur lotId")
    void listerPostes() {
        RefmBtpPoste poste = new RefmBtpPoste();
        poste.setId(2L);
        poste.setLotId(1L);
        poste.setCode("FOND");
        when(posteRepository.findByLotIdOrderByIdAsc(1L)).thenReturn(List.of(poste));

        List<RefmBtpPosteDto> postes = service.listerPostes(1L);

        assertThat(postes)
                .extracting(RefmBtpPosteDto::getCode, RefmBtpPosteDto::getLotId)
                .containsExactly(org.assertj.core.groups.Tuple.tuple("FOND", 1L));
    }

    @Test
    @DisplayName(
            "Given un poste When je liste ses taches Then les taches avec leur unite par defaut sont retournees ; aucun resultat donne une liste vide")
    void listerTaches() {
        RefmBtpTache tache = new RefmBtpTache();
        tache.setId(3L);
        tache.setPosteId(2L);
        tache.setUniteDefaut("m3");
        when(tacheRepository.findByPosteIdOrderByIdAsc(2L)).thenReturn(List.of(tache));
        when(tacheRepository.findByPosteIdOrderByIdAsc(99L)).thenReturn(List.of());

        List<RefmBtpTacheDto> taches = service.listerTaches(2L);

        assertThat(taches).extracting(RefmBtpTacheDto::getUniteDefaut).containsExactly("m3");
        assertThat(service.listerTaches(99L)).isEmpty();
    }

    @Test
    @DisplayName("Given une tache When je liste ses sous-taches Then les sous-taches de la tache sont retournees")
    void listerSousTaches() {
        RefmBtpSousTache st = new RefmBtpSousTache();
        st.setId(4L);
        st.setTacheId(3L);
        st.setLibelle("Coffrage");
        when(sousTacheRepository.findByTacheIdOrderByIdAsc(3L)).thenReturn(List.of(st));

        List<RefmBtpSousTacheDto> sousTaches = service.listerSousTaches(3L);

        assertThat(sousTaches)
                .extracting(RefmBtpSousTacheDto::getTacheId, RefmBtpSousTacheDto::getLibelle)
                .containsExactly(org.assertj.core.groups.Tuple.tuple(3L, "Coffrage"));
    }
}
