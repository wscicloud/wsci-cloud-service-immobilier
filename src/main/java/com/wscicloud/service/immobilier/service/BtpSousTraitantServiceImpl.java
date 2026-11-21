package com.wscicloud.service.immobilier.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpSoustraitantDto;
import com.wscicloud.service.immobilier.domain.mapper.BtpSoustraitantMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpSoustraitantRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Getter
@Setter
@Service
@Slf4j
public class BtpSousTraitantServiceImpl implements BtpSousTraitantService {

    @Autowired
    private BtpSoustraitantRepository soustraitantRepository;

    @Autowired
    private BtpSoustraitantMapper soustraitantMapper;

    @Override
    public BtpSoustraitantDto save(BtpSoustraitantDto dto) {
        return soustraitantMapper.modeleToDto(soustraitantRepository.save(soustraitantMapper.dtoToModele(dto)));
    }

    @Override
    public BtpSoustraitantDto findById(Long id) {
        return soustraitantRepository
                .findById(id)
                .map(soustraitantMapper::modeleToDto)
                .orElse(null);
    }

    @Override
    public List<BtpSoustraitantDto> findByProjet(Long projetId) {
        return soustraitantMapper.modeleToDto(soustraitantRepository.findByProjetId(projetId));
    }

    @Override
    public void delete(Long id) {
        soustraitantRepository.deleteById(id);
    }
}
