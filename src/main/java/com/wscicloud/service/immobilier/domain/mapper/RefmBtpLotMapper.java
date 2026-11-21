package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpLotDto;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpLot;

@Mapper(componentModel = "spring")
public interface RefmBtpLotMapper {

    RefmBtpLotDto modeleToDto(final RefmBtpLot source);

    List<RefmBtpLotDto> modeleToDto(final List<RefmBtpLot> sources);

    RefmBtpLot dtoToModele(final RefmBtpLotDto dto);

    List<RefmBtpLot> dtoToModele(final List<RefmBtpLotDto> dtos);
}
