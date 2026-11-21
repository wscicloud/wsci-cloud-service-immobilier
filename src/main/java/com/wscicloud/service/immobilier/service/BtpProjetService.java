package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;

public interface BtpProjetService {

    BtpProjetDto create(BtpProjetDto dto);

    BtpProjetDto update(BtpProjetDto dto);

    BtpProjetDto findById(Long id);

    List<BtpProjetDto> findAllBySocieteId(Long societeId);

    List<BtpProjetDto> findAll();

    void delete(Long id);
}
