package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
            sequenceName = "seq_btp_situation_travaux",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_situation_travaux")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "mois", nullable = false)
    private Integer mois;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "date_situation", nullable = false)
    private LocalDate dateSituation;

    @Column(name = "montant_marche_ht")
    private BigDecimal montantMarcheHt;

    @Column(name = "montant_cumule_ht")
    private BigDecimal montantCumuleHt;

    @Column(name = "montant_precedent_ht")
    private BigDecimal montantPrecedentHt;

    @Column(name = "montant_periode_ht")
    private BigDecimal montantPeriodeHt;

    @Column(name = "remise_cumul")
    private BigDecimal remiseCumul;

    @Column(name = "remise_precedent")
    private BigDecimal remisePrecedent;

    @Column(name = "remise_periode")
    private BigDecimal remisePeriode;

    @Column(name = "montant_apres_remise")
    private BigDecimal montantApresRemise;

    @Column(name = "avancement_global_pct")
    private BigDecimal avancementGlobalPct;

    @Column(name = "statut")
    private String statut;
}
