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
@Table(name = "refm_btp_tache", schema = "referentiel_metier")
@EqualsAndHashCode(of = "id")
public class RefmBtpTache implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_refm_btp_tache",
            sequenceName = "referentiel_metier.seq_refm_btp_tache",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_refm_btp_tache")
    private Long id;

    @Column(name = "poste_id", nullable = false)
    private Long posteId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "unite_defaut")
    private String uniteDefaut;
}
