package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpSoustraitant;

@Repository
public interface BtpSoustraitantRepository extends JpaRepository<BtpSoustraitant, Long> {

    List<BtpSoustraitant> findByProjetId(Long projetId);
}
