package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpSousTacheDto;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpSousTache;

@Mapper(componentModel = "spring")
public interface RefmBtpSousTacheMapper {

    RefmBtpSousTacheDto modeleToDto(final RefmBtpSousTache source);

    List<RefmBtpSousTacheDto> modeleToDto(final List<RefmBtpSousTache> sources);

    RefmBtpSousTache dtoToModele(final RefmBtpSousTacheDto dto);

    List<RefmBtpSousTache> dtoToModele(final List<RefmBtpSousTacheDto> dtos);
}
