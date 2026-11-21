package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpSoustraitantDto;
import com.wscicloud.service.immobilier.domain.entities.BtpSoustraitant;

@Mapper(componentModel = "spring")
public interface BtpSoustraitantMapper {

    BtpSoustraitantDto modeleToDto(final BtpSoustraitant source);

    List<BtpSoustraitantDto> modeleToDto(final List<BtpSoustraitant> sources);

    BtpSoustraitant dtoToModele(final BtpSoustraitantDto dto);

    List<BtpSoustraitant> dtoToModele(final List<BtpSoustraitantDto> dtos);
}
