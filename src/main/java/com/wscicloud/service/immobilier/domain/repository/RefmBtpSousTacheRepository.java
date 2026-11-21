package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.RefmBtpSousTache;

@Repository
public interface RefmBtpSousTacheRepository extends JpaRepository<RefmBtpSousTache, Long> {

    List<RefmBtpSousTache> findByTacheIdOrderByIdAsc(Long tacheId);
}
