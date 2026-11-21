package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureLigneDto;
import com.wscicloud.service.immobilier.domain.entities.BtpFactureLigne;

@Mapper(componentModel = "spring")
public interface BtpFactureLigneMapper {

    BtpFactureLigneDto modeleToDto(final BtpFactureLigne source);

    List<BtpFactureLigneDto> modeleToDto(final List<BtpFactureLigne> sources);

    BtpFactureLigne dtoToModele(final BtpFactureLigneDto dto);

    List<BtpFactureLigne> dtoToModele(final List<BtpFactureLigneDto> dtos);
}
