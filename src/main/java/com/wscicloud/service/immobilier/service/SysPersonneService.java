package com.wscicloud.service.immobilier.service;

import java.util.List;

import com.wscicloud.service.immobilier.domain.dto.PersonneDto;

public interface SysPersonneService {

    List<PersonneDto> rechercher(Long societeId, String q);
}
