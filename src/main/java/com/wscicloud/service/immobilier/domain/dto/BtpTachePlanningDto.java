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
public class BtpTachePlanningDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long projetId;

    private String codeTache;

    @NotBlank private String nomTache;

    private String lot;

    @NotNull private LocalDate dateDebut;

    @NotNull private Integer dureeJours;

    private LocalDate dateFin;

    private BigDecimal avancementPct;

    private String predecesseurs;
}
