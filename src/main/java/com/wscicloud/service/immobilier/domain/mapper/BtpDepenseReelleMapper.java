package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpDepenseReelleDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDepenseReelle;

@Mapper(componentModel = "spring")
public interface BtpDepenseReelleMapper {

    BtpDepenseReelleDto modeleToDto(final BtpDepenseReelle source);

    List<BtpDepenseReelleDto> modeleToDto(final List<BtpDepenseReelle> sources);

    BtpDepenseReelle dtoToModele(final BtpDepenseReelleDto dto);

    List<BtpDepenseReelle> dtoToModele(final List<BtpDepenseReelleDto> dtos);
}
