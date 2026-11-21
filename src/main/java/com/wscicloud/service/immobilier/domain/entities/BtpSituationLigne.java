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
@Table(name = "btp_situation_ligne", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpSituationLigne implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_situation_ligne",
            sequenceName = "seq_btp_situation_ligne",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_situation_ligne")
    private Long id;

    @Column(name = "situation_id", nullable = false)
    private Long situationId;

    @Column(name = "ligne_devis_id", nullable = false)
    private Long ligneDevisId;

    @Column(name = "avancement_cumul_pct")
    private BigDecimal avancementCumulPct;

    @Column(name = "montant_cumul_ht")
    private BigDecimal montantCumulHt;

    @Column(name = "avancement_precedent_pct")
    private BigDecimal avancementPrecedentPct;

    @Column(name = "montant_precedent_ht")
    private BigDecimal montantPrecedentHt;

    @Column(name = "avancement_periode_pct")
    private BigDecimal avancementPeriodePct;

    @Column(name = "montant_periode_ht")
    private BigDecimal montantPeriodeHt;
}
