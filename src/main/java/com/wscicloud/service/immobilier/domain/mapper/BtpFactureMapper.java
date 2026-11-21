package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureDto;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;

@Mapper(componentModel = "spring")
public interface BtpFactureMapper {

    BtpFactureDto modeleToDto(final BtpFacture source);

    List<BtpFactureDto> modeleToDto(final List<BtpFacture> sources);

    BtpFacture dtoToModele(final BtpFactureDto dto);

    List<BtpFacture> dtoToModele(final List<BtpFactureDto> dtos);
}
