package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpSoustraitantDto;

public interface BtpSousTraitantService {

    BtpSoustraitantDto save(BtpSoustraitantDto dto);

    BtpSoustraitantDto findById(Long id);

    List<BtpSoustraitantDto> findByProjet(Long projetId);

    void delete(Long id);
}
