package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;

public interface BtpProjetService {

    BtpProjetDto creer(BtpProjetDto dto);

    BtpProjetDto modifier(BtpProjetDto dto);

    BtpProjetDto lire(Long id);

    List<BtpProjetDto> lister();

    List<BtpProjetDto> rechercher(String q);

    void supprimer(Long id);
}
