package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpTachePlanningDto;
import com.wscicloud.service.immobilier.domain.entities.BtpTachePlanning;

@Mapper(componentModel = "spring")
public interface BtpTachePlanningMapper {

    BtpTachePlanningDto modeleToDto(final BtpTachePlanning source);

    List<BtpTachePlanningDto> modeleToDto(final List<BtpTachePlanning> sources);

    BtpTachePlanning dtoToModele(final BtpTachePlanningDto dto);

    List<BtpTachePlanning> dtoToModele(final List<BtpTachePlanningDto> dtos);
}
