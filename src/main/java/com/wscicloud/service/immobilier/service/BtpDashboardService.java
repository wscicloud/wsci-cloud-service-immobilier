package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto;

public interface BtpDashboardService {

    BtpDashboardDto findByProjet(Long projetId);

    List<BtpDashboardDto> findBySociete(Long societeId);
}
