package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "refm_btp_tache_biblio")
@EqualsAndHashCode(of = "id")
public class RefmBtpTacheBiblio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_refm_btp_tache_biblio",
            sequenceName = "seq_refm_btp_tache_biblio",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_refm_btp_tache_biblio")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "tache", nullable = false)
    private String tache;

    @Column(name = "lot")
    private String lot;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "unite")
    private String unite;

    @Column(name = "rendement")
    private BigDecimal rendement;

    @Column(name = "chef")
    private Integer chef;

    @Column(name = "compagnon")
    private Integer compagnon;

    @Column(name = "manoeuvres")
    private Integer manoeuvres;

    @Column(name = "specialiste")
    private Integer specialiste;

    @Column(name = "couleur_hex")
    private String couleurHex;
}
