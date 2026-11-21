package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "refm_btp_sous_tache", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class RefmBtpSousTache implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tache_id", nullable = false)
    private Long tacheId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "unite_defaut")
    private String uniteDefaut;
}
