package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;
import com.wscicloud.service.immobilier.domain.mapper.BtpProjetMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Getter
@Setter
@Service
@Slf4j
public class BtpProjetServiceImpl implements BtpProjetService {

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpProjetMapper projetMapper;

    @Override
    public BtpProjetDto create(BtpProjetDto dto) {
        dto.setId(null);
        if (dto.getStatutDevis() == null) {
            dto.setStatutDevis("BROUILLON");
        }
        if (dto.getRemiseMontant() == null) {
            dto.setRemiseMontant(BigDecimal.ZERO);
        }
        return projetMapper.modeleToDto(projetRepository.save(projetMapper.dtoToModele(dto)));
    }

    @Override
    public BtpProjetDto update(BtpProjetDto dto) {
        if (dto.getId() == null || !projetRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Projet BTP introuvable : " + dto.getId());
        }
        BtpProjet saved = projetRepository.save(projetMapper.dtoToModele(dto));
        return projetMapper.modeleToDto(saved);
    }

    @Override
    public BtpProjetDto findById(Long id) {
        return projetRepository.findById(id).map(projetMapper::modeleToDto).orElse(null);
    }

    @Override
    public List<BtpProjetDto> findAllBySocieteId(Long societeId) {
        return projetMapper.modeleToDto(projetRepository.findBySocieteIdOrderByNom(societeId));
    }

    @Override
    public List<BtpProjetDto> findAll() {
        return projetMapper.modeleToDto(projetRepository.findAll());
    }

    @Override
    public void delete(Long id) {
        projetRepository.deleteById(id);
    }
}
