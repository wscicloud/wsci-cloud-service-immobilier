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
public class BtpFactureDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long projetId;

    private Long situationId;

    @NotBlank private String numeroFacture;

    private String typeFacture;

    private LocalDate dateEmission;

    private LocalDate dateEcheance;

    private BigDecimal montantHt;

    /** Taux de TVA en pourcentage (ex : 18.00). */
    private BigDecimal tauxTva;

    private BigDecimal montantTtc;

    private String statut;

    private OffsetDateTime creeLe;
}
