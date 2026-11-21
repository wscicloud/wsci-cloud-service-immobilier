package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank private String numeroSituation;

    private LocalDate dateSituation;

    private LocalDate periodeDebut;

    private LocalDate periodeFin;

    private BigDecimal tauxAvancementPct;

    private BigDecimal montantPeriodeHt;

    private BigDecimal montantCumuleHt;

    private String statut;

    private String observations;

    private OffsetDateTime creeLe;
}
