package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;

@Mapper(componentModel = "spring")
public interface BtpProjetMapper {

    BtpProjetDto modeleToDto(final BtpProjet source);

    List<BtpProjetDto> modeleToDto(final List<BtpProjet> sources);

    BtpProjet dtoToModele(final BtpProjetDto dto);

    List<BtpProjet> dtoToModele(final List<BtpProjetDto> dtos);
}
