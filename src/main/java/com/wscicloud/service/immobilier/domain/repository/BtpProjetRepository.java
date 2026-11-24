package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.BtpProjet;

@Repository
public interface BtpProjetRepository extends JpaRepository<BtpProjet, Long> {

    boolean existsByCodeProjet(String codeProjet);

    List<BtpProjet> findAllBySocieteIdOrderByCreeLeDesc(Long societeId);

    @Query("select p from BtpProjet p where p.societeId = :societeId "
            + " and (lower(p.codeProjet) like lower(concat('%', :q, '%'))"
            + " or lower(p.nomProjet) like lower(concat('%', :q, '%'))"
            + " or lower(p.clientNom) like lower(concat('%', :q, '%'))"
            + " or lower(p.localisation) like lower(concat('%', :q, '%'))) order by p.creeLe desc")
    List<BtpProjet> rechercher(@Param("societeId") Long societeId, @Param("q") String q);
}
