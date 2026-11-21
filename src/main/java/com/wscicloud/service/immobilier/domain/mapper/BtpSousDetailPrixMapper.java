package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpSousDetailPrixDto;
import com.wscicloud.service.immobilier.domain.entities.BtpSousDetailPrix;

@Mapper(componentModel = "spring")
public interface BtpSousDetailPrixMapper {

    BtpSousDetailPrixDto modeleToDto(final BtpSousDetailPrix source);

    List<BtpSousDetailPrixDto> modeleToDto(final List<BtpSousDetailPrix> sources);

    BtpSousDetailPrix dtoToModele(final BtpSousDetailPrixDto dto);

    List<BtpSousDetailPrix> dtoToModele(final List<BtpSousDetailPrixDto> dtos);
}
