package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Devis d'un projet ; {@code lignes} n'est renseigne que pour la lecture unitaire et la creation. */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class BtpDevisDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long projetId;

    @NotBlank private String numeroDevis;

    private LocalDate dateDevis;

    private BigDecimal montantHt;

    /** Taux de TVA en pourcentage (ex : 18.00). */
    private BigDecimal tauxTva;

    private BigDecimal montantTtc;

    private String statut;

    private String notes;

    private OffsetDateTime creeLe;

    private List<BtpLigneDevisDto> lignes = new ArrayList<>();
}
