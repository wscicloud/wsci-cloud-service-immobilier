package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpTacheBiblioDto;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpTacheBiblio;

@Mapper(componentModel = "spring")
public interface RefmBtpTacheBiblioMapper {

    RefmBtpTacheBiblioDto modeleToDto(final RefmBtpTacheBiblio source);

    List<RefmBtpTacheBiblioDto> modeleToDto(final List<RefmBtpTacheBiblio> sources);

    RefmBtpTacheBiblio dtoToModele(final RefmBtpTacheBiblioDto dto);

    List<RefmBtpTacheBiblio> dtoToModele(final List<RefmBtpTacheBiblioDto> dtos);
}
