package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpDepenseReelleDto;
import com.wscicloud.service.immobilier.domain.dto.BtpEcartDto;

public interface BtpDepenseService {

    BtpDepenseReelleDto save(BtpDepenseReelleDto dto);

    BtpDepenseReelleDto findById(Long id);

    List<BtpDepenseReelleDto> findByProjet(Long projetId);

    List<BtpDepenseReelleDto> findBySociete(Long societeId);

    void delete(Long id);

    /** Ecart budget / realise, marge brute et alerte si marge < marge_brute_min_acceptable. */
    BtpEcartDto calculerEcart(Long projetId);
}
