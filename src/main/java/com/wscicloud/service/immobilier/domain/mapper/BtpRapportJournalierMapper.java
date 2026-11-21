package com.wscicloud.service.immobilier.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.wscicloud.service.immobilier.domain.dto.BtpRapportJournalierDto;
import com.wscicloud.service.immobilier.domain.entities.BtpRapportJournalier;

@Mapper(componentModel = "spring")
public interface BtpRapportJournalierMapper {

    BtpRapportJournalierDto modeleToDto(final BtpRapportJournalier source);

    List<BtpRapportJournalierDto> modeleToDto(final List<BtpRapportJournalier> sources);

    BtpRapportJournalier dtoToModele(final BtpRapportJournalierDto dto);

    List<BtpRapportJournalier> dtoToModele(final List<BtpRapportJournalierDto> dtos);
}
