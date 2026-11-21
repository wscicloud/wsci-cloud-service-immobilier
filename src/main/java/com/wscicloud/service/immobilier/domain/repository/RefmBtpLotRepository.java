package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.RefmBtpLot;

@Repository
public interface RefmBtpLotRepository extends JpaRepository<RefmBtpLot, Long> {

    List<RefmBtpLot> findAllByOrderByIdAsc();
}
