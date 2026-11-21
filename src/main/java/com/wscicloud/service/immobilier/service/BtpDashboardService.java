package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.AvancementLot;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.ResteAFacturer;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.SoldeClient;

public interface BtpDashboardService {

    BtpDashboardDto tableauDeBord(Long projetId);

    List<AvancementLot> avancementParLot(Long projetId);

    ResteAFacturer resteAFacturer(Long projetId);

    SoldeClient soldeClient(Long projetId);
}
