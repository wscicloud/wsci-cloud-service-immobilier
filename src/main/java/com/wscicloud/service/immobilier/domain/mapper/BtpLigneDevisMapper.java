package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;

@Mapper(componentModel = "spring")
public interface BtpLigneDevisMapper {

    BtpLigneDevisDto modeleToDto(final BtpLigneDevis source);

    List<BtpLigneDevisDto> modeleToDto(final List<BtpLigneDevis> sources);

    BtpLigneDevis dtoToModele(final BtpLigneDevisDto dto);

    List<BtpLigneDevis> dtoToModele(final List<BtpLigneDevisDto> dtos);
}
