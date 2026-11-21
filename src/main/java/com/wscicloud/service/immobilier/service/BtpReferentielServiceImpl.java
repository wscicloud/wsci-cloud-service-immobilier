package com.wscicloud.service.immobilier.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpLotDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpPosteDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpSousTacheDto;
import com.wscicloud.service.immobilier.domain.dto.RefmBtpTacheDto;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpLotMapper;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpPosteMapper;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpSousTacheMapper;
import com.wscicloud.service.immobilier.domain.mapper.RefmBtpTacheMapper;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpLotRepository;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpPosteRepository;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpSousTacheRepository;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpTacheRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class BtpReferentielServiceImpl implements BtpReferentielService {

    @Autowired
    private RefmBtpLotRepository lotRepository;

    @Autowired
    private RefmBtpPosteRepository posteRepository;

    @Autowired
    private RefmBtpTacheRepository tacheRepository;

    @Autowired
    private RefmBtpSousTacheRepository sousTacheRepository;

    @Autowired
    private RefmBtpLotMapper lotMapper;

    @Autowired
    private RefmBtpPosteMapper posteMapper;

    @Autowired
    private RefmBtpTacheMapper tacheMapper;

    @Autowired
    private RefmBtpSousTacheMapper sousTacheMapper;

    @Override
    public List<RefmBtpLotDto> listerLots() {
        return lotMapper.modeleToDto(lotRepository.findAllByOrderByIdAsc());
    }

    @Override
    public List<RefmBtpPosteDto> listerPostes(Long lotId) {
        return posteMapper.modeleToDto(posteRepository.findByLotIdOrderByIdAsc(lotId));
    }

    @Override
    public List<RefmBtpTacheDto> listerTaches(Long posteId) {
        return tacheMapper.modeleToDto(tacheRepository.findByPosteIdOrderByIdAsc(posteId));
    }

    @Override
    public List<RefmBtpSousTacheDto> listerSousTaches(Long tacheId) {
        return sousTacheMapper.modeleToDto(sousTacheRepository.findByTacheIdOrderByIdAsc(tacheId));
    }
}
