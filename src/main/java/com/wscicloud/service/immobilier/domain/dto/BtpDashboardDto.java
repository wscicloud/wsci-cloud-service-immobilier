package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Indicateurs du tableau de bord d'un projet. */
@Getter
@Setter
@NoArgsConstructor
public class BtpDashboardDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long projetId;
    private String nomProjet;
    private BigDecimal budgetHt = BigDecimal.ZERO;
    private BigDecimal budgetApresRemise = BigDecimal.ZERO;
    private BigDecimal realiseHt = BigDecimal.ZERO;
    private BigDecimal ecart = BigDecimal.ZERO;
    private BigDecimal margeBrute = BigDecimal.ZERO;
    private BigDecimal margeBrutePct = BigDecimal.ZERO;
    private BigDecimal avancementTravauxPct = BigDecimal.ZERO;
    private BigDecimal avancementPlanningPct = BigDecimal.ZERO;
    private BigDecimal montantFactureTtc = BigDecimal.ZERO;
    private BigDecimal montantPaye = BigDecimal.ZERO;
    private BigDecimal montantSousTraitance = BigDecimal.ZERO;
    private Integer nombreSituations = 0;
    private List<String> alertes = new ArrayList<>();
}
