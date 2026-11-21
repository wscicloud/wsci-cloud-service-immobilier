package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpPaiementDto;
import com.wscicloud.service.immobilier.domain.entities.BtpPaiement;

@Mapper(componentModel = "spring")
public interface BtpPaiementMapper {

    BtpPaiementDto modeleToDto(final BtpPaiement source);

    List<BtpPaiementDto> modeleToDto(final List<BtpPaiement> sources);

    BtpPaiement dtoToModele(final BtpPaiementDto dto);

    List<BtpPaiement> dtoToModele(final List<BtpPaiementDto> dtos);
}
