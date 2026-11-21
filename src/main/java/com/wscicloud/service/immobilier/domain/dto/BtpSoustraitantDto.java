package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class BtpSoustraitantDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long projetId;

    @NotBlank private String nom;

    private String metier;

    private String lot;

    private BigDecimal marcheMontant;

    private BigDecimal avancementPct;

    private BigDecimal retenueGarantiePct;

    private String natureContrat;

    private BigDecimal fournituresEntreprise;

    private BigDecimal tauxRisque;

    private BigDecimal tauxSav;
}
