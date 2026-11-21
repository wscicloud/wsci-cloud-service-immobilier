package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.RefmBtpTacheBiblio;

@Repository
public interface RefmBtpTacheBiblioRepository extends JpaRepository<RefmBtpTacheBiblio, Long> {

    List<RefmBtpTacheBiblio> findAllByOrderByCode();

    Optional<RefmBtpTacheBiblio> findByCode(String code);
}
