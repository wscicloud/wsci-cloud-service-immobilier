package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;

@Mapper(componentModel = "spring")
public interface BtpDevisMapper {

    /** Les lignes ne sont pas portees par l'entite : elles sont chargees par le service. */
    @Mapping(target = "lignes", ignore = true)
    BtpDevisDto modeleToDto(final BtpDevis source);

    List<BtpDevisDto> modeleToDto(final List<BtpDevis> sources);

    BtpDevis dtoToModele(final BtpDevisDto dto);

    List<BtpDevis> dtoToModele(final List<BtpDevisDto> dtos);
}
