package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "btp_tache_planning", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpTachePlanning implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_tache_planning",
            sequenceName = "seq_btp_tache_planning",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_tache_planning")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "code_tache")
    private String codeTache;

    @Column(name = "nom_tache", nullable = false)
    private String nomTache;

    @Column(name = "lot")
    private String lot;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "duree_jours", nullable = false)
    private Integer dureeJours;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "avancement_pct")
    private BigDecimal avancementPct;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "predecesseurs")
    private String predecesseurs;
}
