package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpTacheBiblioDto;

public interface BtpBibliothequeService {

    RefmBtpTacheBiblioDto save(RefmBtpTacheBiblioDto dto);

    RefmBtpTacheBiblioDto findById(Long id);

    List<RefmBtpTacheBiblioDto> findAll();

    void delete(Long id);
}
