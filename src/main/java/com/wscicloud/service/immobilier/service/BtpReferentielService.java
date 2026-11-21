package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpLotDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpPosteDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpSousTacheDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpTacheDto;

/** Lecture du referentiel BTP (lots, postes, taches, sous-taches). */
public interface BtpReferentielService {

    List<RefmBtpLotDto> listerLots();

    List<RefmBtpPosteDto> listerPostes(Long lotId);

    List<RefmBtpTacheDto> listerTaches(Long posteId);

    List<RefmBtpSousTacheDto> listerSousTaches(Long tacheId);
}
