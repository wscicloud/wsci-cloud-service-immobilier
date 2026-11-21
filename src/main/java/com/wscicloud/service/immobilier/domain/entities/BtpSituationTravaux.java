package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "btp_situation_travaux", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpSituationTravaux implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_situation_travaux",
            sequenceName = "immobilier.seq_btp_situation_travaux",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_btp_situation_travaux")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "numero_situation", nullable = false)
    private String numeroSituation;

    @Column(name = "date_situation", nullable = false)
    private LocalDate dateSituation;

    @Column(name = "periode_debut")
    private LocalDate periodeDebut;

    @Column(name = "periode_fin")
    private LocalDate periodeFin;

    @Column(name = "taux_avancement_pct", nullable = false)
    private BigDecimal tauxAvancementPct;

    @Column(name = "montant_periode_ht", nullable = false)
    private BigDecimal montantPeriodeHt;

    @Column(name = "montant_cumule_ht", nullable = false)
    private BigDecimal montantCumuleHt;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "observations")
    private String observations;

    @Column(name = "cree_le", nullable = false)
    private OffsetDateTime creeLe;
}
