package com.wscicloud.service.immobilier.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.PersonneDto;
import com.wscicloud.service.immobilier.domain.mapper.SysPersonneMapper;
import com.wscicloud.service.immobilier.domain.repository.SysPersonneRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class SysPersonneServiceImpl implements SysPersonneService {

    static final int MAX_RESULTATS = 20;

    @Autowired
    private SysPersonneRepository personneRepository;

    @Autowired
    private SysPersonneMapper personneMapper;

    @Override
    public List<PersonneDto> rechercher(Long societeId, String q) {
        String recherche = q == null ? "" : q.trim();
        return personneMapper.modeleToDto(
                personneRepository.rechercher(societeId, recherche, PageRequest.of(0, MAX_RESULTATS)));
    }
}
