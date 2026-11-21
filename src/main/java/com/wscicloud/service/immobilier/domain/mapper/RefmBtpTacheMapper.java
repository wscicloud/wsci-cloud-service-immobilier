package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpTacheDto;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpTache;

@Mapper(componentModel = "spring")
public interface RefmBtpTacheMapper {

    RefmBtpTacheDto modeleToDto(final RefmBtpTache source);

    List<RefmBtpTacheDto> modeleToDto(final List<RefmBtpTache> sources);

    RefmBtpTache dtoToModele(final RefmBtpTacheDto dto);

    List<RefmBtpTache> dtoToModele(final List<RefmBtpTacheDto> dtos);
}
