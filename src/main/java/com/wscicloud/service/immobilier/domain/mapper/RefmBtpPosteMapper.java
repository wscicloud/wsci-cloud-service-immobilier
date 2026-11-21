package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpPosteDto;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpPoste;

@Mapper(componentModel = "spring")
public interface RefmBtpPosteMapper {

    RefmBtpPosteDto modeleToDto(final RefmBtpPoste source);

    List<RefmBtpPosteDto> modeleToDto(final List<RefmBtpPoste> sources);

    RefmBtpPoste dtoToModele(final RefmBtpPosteDto dto);

    List<RefmBtpPoste> dtoToModele(final List<RefmBtpPosteDto> dtos);
}
