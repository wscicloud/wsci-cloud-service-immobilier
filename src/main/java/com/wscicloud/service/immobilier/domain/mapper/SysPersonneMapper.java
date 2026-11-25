package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.PersonneDto;
import com.wscicloud.service.immobilier.domain.entities.SysPersonne;

@Mapper(componentModel = "spring")
public interface SysPersonneMapper {

    PersonneDto modeleToDto(final SysPersonne source);

    List<PersonneDto> modeleToDto(final List<SysPersonne> sources);
}
