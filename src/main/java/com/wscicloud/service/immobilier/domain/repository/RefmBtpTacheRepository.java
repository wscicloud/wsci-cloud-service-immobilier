package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.RefmBtpTache;

@Repository
public interface RefmBtpTacheRepository extends JpaRepository<RefmBtpTache, Long> {

    List<RefmBtpTache> findByPosteIdOrderByIdAsc(Long posteId);
}
