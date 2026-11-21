package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.RefmBtpPoste;

@Repository
public interface RefmBtpPosteRepository extends JpaRepository<RefmBtpPoste, Long> {

    List<RefmBtpPoste> findByLotIdOrderByIdAsc(Long lotId);
}
