package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    @NotBlank private String numero;

    @NotNull private LocalDate dateFacture;

    private LocalDate dateEcheance;

    private String objet;

    private BigDecimal montantHt;

    private BigDecimal tvaTaux;

    private BigDecimal montantTva;

    private BigDecimal retenueGarantieTaux;

    private BigDecimal retenueGarantieMontant;

    private BigDecimal avanceTaux;

    private BigDecimal avanceMontant;

    private BigDecimal montantTtc;

    private BigDecimal netAPayer;

    private BigDecimal montantPaye;

    private String statut;
}
