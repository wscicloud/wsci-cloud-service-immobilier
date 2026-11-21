package com.wscicloud.service.immobilier.domain.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;

@Repository
public interface BtpLigneDevisRepository extends JpaRepository<BtpLigneDevis, Long> {

    List<BtpLigneDevis> findByDevisIdOrderByOrdreAffichageAscIdAsc(Long devisId);

    List<BtpLigneDevis> findByDevisIdIn(Collection<Long> devisIds);

    void deleteByDevisId(Long devisId);
}
