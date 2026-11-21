package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
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
public class BtpProjetDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank private String codeProjet;

    @NotBlank private String nomProjet;

    @NotBlank private String clientNom;

    private String localisation;

    private LocalDate dateDebut;

    private LocalDate dateFinPrevue;

    private LocalDate dateFinReelle;

    private BigDecimal budgetPrevuHt;

    private BigDecimal budgetPrevuTtc;

    private BigDecimal budgetReelHt;

    private BigDecimal budgetReelTtc;

    private BigDecimal margePrevuTtc;

    private BigDecimal margeReelTtc;

    private String devise;

    private String statut;

    private OffsetDateTime creeLe;
}
