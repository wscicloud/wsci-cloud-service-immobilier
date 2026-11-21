package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class BtpSituationTravauxDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long projetId;

    @NotNull private Integer numero;

    @NotNull private Integer mois;

    @NotNull private Integer annee;

    @NotNull private LocalDate dateSituation;

    private BigDecimal montantMarcheHt;

    private BigDecimal montantCumuleHt;

    private BigDecimal montantPrecedentHt;

    private BigDecimal montantPeriodeHt;

    private BigDecimal remiseCumul;

    private BigDecimal remisePrecedent;

    private BigDecimal remisePeriode;

    private BigDecimal montantApresRemise;

    private BigDecimal avancementGlobalPct;

    private String statut;
}
