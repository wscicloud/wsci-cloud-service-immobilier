package com.wscicloud.service.immobilier.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpTacheBiblioDto;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpTacheBiblioMapper;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpTacheBiblioRepository;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Getter
@Setter
@Service
@Slf4j
public class BtpBibliothequeServiceImpl implements BtpBibliothequeService {

    @Autowired
    private RefmBtpTacheBiblioRepository biblioRepository;

    @Autowired
    private RefmBtpTacheBiblioMapper biblioMapper;

    @Override
    public RefmBtpTacheBiblioDto save(RefmBtpTacheBiblioDto dto) {
        return biblioMapper.modeleToDto(biblioRepository.save(biblioMapper.dtoToModele(dto)));
    }

    @Override
    public RefmBtpTacheBiblioDto findById(Long id) {
        return biblioRepository.findById(id).map(biblioMapper::modeleToDto).orElse(null);
    }

    @Override
    public List<RefmBtpTacheBiblioDto> findAll() {
        return biblioMapper.modeleToDto(biblioRepository.findAllByOrderByCode());
    }

    @Override
    public void delete(Long id) {
        biblioRepository.deleteById(id);
    }
}
