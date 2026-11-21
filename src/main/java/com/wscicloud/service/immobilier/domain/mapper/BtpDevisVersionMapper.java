package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpDevisVersionDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDevisVersion;

@Mapper(componentModel = "spring")
public interface BtpDevisVersionMapper {

    BtpDevisVersionDto modeleToDto(final BtpDevisVersion source);

    List<BtpDevisVersionDto> modeleToDto(final List<BtpDevisVersion> sources);

    BtpDevisVersion dtoToModele(final BtpDevisVersionDto dto);

    List<BtpDevisVersion> dtoToModele(final List<BtpDevisVersionDto> dtos);
}
