package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationTravauxDto;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;

@Mapper(componentModel = "spring")
public interface BtpSituationTravauxMapper {

    BtpSituationTravauxDto modeleToDto(final BtpSituationTravaux source);

    List<BtpSituationTravauxDto> modeleToDto(final List<BtpSituationTravaux> sources);

    BtpSituationTravaux dtoToModele(final BtpSituationTravauxDto dto);

    List<BtpSituationTravaux> dtoToModele(final List<BtpSituationTravauxDto> dtos);
}
