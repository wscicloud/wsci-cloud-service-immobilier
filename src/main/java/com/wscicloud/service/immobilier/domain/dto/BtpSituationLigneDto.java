package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class BtpSituationLigneDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long situationId;

    @NotNull private Long ligneDevisId;

    private BigDecimal avancementCumulPct;

    private BigDecimal montantCumulHt;

    private BigDecimal avancementPrecedentPct;

    private BigDecimal montantPrecedentHt;

    private BigDecimal avancementPeriodePct;

    private BigDecimal montantPeriodeHt;
}
