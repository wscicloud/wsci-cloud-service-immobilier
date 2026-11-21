package com.wscicloud.service.immobilier.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpRapportJournalierDto;
import com.wscicloud.service.immobilier.domain.dto.BtpTachePlanningDto;
import com.wscicloud.service.immobilier.domain.mapper.BtpRapportJournalierMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpTachePlanningMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpRapportJournalierRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpTachePlanningRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Getter
@Setter
@Service
@Slf4j
public class BtpPlanningServiceImpl implements BtpPlanningService {

    @Autowired
    private BtpTachePlanningRepository tacheRepository;

    @Autowired
    private BtpTachePlanningMapper tacheMapper;

    @Autowired
    private BtpRapportJournalierRepository rapportRepository;

    @Autowired
    private BtpRapportJournalierMapper rapportMapper;

    @Override
    public BtpTachePlanningDto saveTache(BtpTachePlanningDto dto) {
        if (dto.getDateDebut() == null || dto.getDureeJours() == null || dto.getDureeJours() < 1) {
            throw new IllegalArgumentException("Date de debut et duree (>= 1 jour) obligatoires");
        }
        dto.setDateFin(calculerDateFin(dto.getDateDebut(), dto.getDureeJours()));
        if (dto.getPredecesseurs() == null || dto.getPredecesseurs().isBlank()) {
            dto.setPredecesseurs("[]");
        }
        return tacheMapper.modeleToDto(tacheRepository.save(tacheMapper.dtoToModele(dto)));
    }

    public static java.time.LocalDate calculerDateFin(java.time.LocalDate dateDebut, int dureeJours) {
        return dateDebut.plusDays(dureeJours - 1L);
    }

    @Override
    public List<BtpTachePlanningDto> findTaches(Long projetId) {
        return tacheMapper.modeleToDto(tacheRepository.findByProjetIdOrderByDateDebut(projetId));
    }

    @Override
    public void deleteTache(Long id) {
        tacheRepository.deleteById(id);
    }

    @Override
    public BtpRapportJournalierDto saveRapport(BtpRapportJournalierDto dto) {
        return rapportMapper.modeleToDto(rapportRepository.save(rapportMapper.dtoToModele(dto)));
    }

    @Override
    public List<BtpRapportJournalierDto> findRapports(Long projetId) {
        return rapportMapper.modeleToDto(rapportRepository.findByProjetIdOrderByDateRapportDesc(projetId));
    }

    @Override
    public void deleteRapport(Long id) {
        rapportRepository.deleteById(id);
    }
}
