package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpFacturePaiementDto;
import com.wscicloud.service.immobilier.domain.entities.BtpFacturePaiement;

@Mapper(componentModel = "spring")
public interface BtpFacturePaiementMapper {

    BtpFacturePaiementDto modeleToDto(final BtpFacturePaiement source);

    List<BtpFacturePaiementDto> modeleToDto(final List<BtpFacturePaiement> sources);

    BtpFacturePaiement dtoToModele(final BtpFacturePaiementDto dto);

    List<BtpFacturePaiement> dtoToModele(final List<BtpFacturePaiementDto> dtos);
}
