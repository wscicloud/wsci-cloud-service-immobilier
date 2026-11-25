package com.wscicloud.service.immobilier.domain.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wscicloud.service.immobilier.domain.entities.SysPersonne;

@Repository
public interface SysPersonneRepository extends JpaRepository<SysPersonne, Long> {

    @Query("select p from SysPersonne p where p.societeId = :societeId"
            + " and (lower(p.nom) like lower(concat('%', :q, '%'))"
            + " or lower(p.prenoms) like lower(concat('%', :q, '%'))"
            + " or lower(p.login) like lower(concat('%', :q, '%'))"
            + " or lower(p.libelle) like lower(concat('%', :q, '%')))"
            + " order by p.nom, p.prenoms")
    List<SysPersonne> rechercher(@Param("societeId") Long societeId, @Param("q") String q, Pageable pageable);
}
