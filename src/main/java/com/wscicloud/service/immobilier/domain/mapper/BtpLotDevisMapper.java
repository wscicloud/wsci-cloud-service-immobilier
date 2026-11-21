package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpLotDevisDto;
import com.wscicloud.service.immobilier.domain.entities.BtpLotDevis;

@Mapper(componentModel = "spring")
public interface BtpLotDevisMapper {

    BtpLotDevisDto modeleToDto(final BtpLotDevis source);

    List<BtpLotDevisDto> modeleToDto(final List<BtpLotDevis> sources);

    BtpLotDevis dtoToModele(final BtpLotDevisDto dto);

    List<BtpLotDevis> dtoToModele(final List<BtpLotDevisDto> dtos);
}
