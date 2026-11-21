package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationLigneDto;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;

@Mapper(componentModel = "spring")
public interface BtpSituationLigneMapper {

    BtpSituationLigneDto modeleToDto(final BtpSituationLigne source);

    List<BtpSituationLigneDto> modeleToDto(final List<BtpSituationLigne> sources);

    BtpSituationLigne dtoToModele(final BtpSituationLigneDto dto);

    List<BtpSituationLigne> dtoToModele(final List<BtpSituationLigneDto> dtos);
}
