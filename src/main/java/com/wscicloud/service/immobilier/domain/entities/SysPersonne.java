package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Lecture seule de la table partagee sys_personne. */
@Entity
@Immutable
@Setter
@Getter
@NoArgsConstructor
@Table(name = "sys_personne", schema = "public")
@EqualsAndHashCode(of = "id")
public class SysPersonne implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(name = "societe_id")
    private Long societeId;

    private String login;

    private String nom;

    private String prenoms;

    private String libelle;
}
