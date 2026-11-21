package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpPosteDevisDto;
import com.wscicloud.service.immobilier.domain.entities.BtpPosteDevis;

@Mapper(componentModel = "spring")
public interface BtpPosteDevisMapper {

    BtpPosteDevisDto modeleToDto(final BtpPosteDevis source);

    List<BtpPosteDevisDto> modeleToDto(final List<BtpPosteDevis> sources);

    BtpPosteDevis dtoToModele(final BtpPosteDevisDto dto);

    List<BtpPosteDevis> dtoToModele(final List<BtpPosteDevisDto> dtos);
}
