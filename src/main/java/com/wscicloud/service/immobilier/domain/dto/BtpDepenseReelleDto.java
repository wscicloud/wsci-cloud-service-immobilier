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
public class BtpDepenseReelleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long projetId;

    private Long societeId;

    @NotNull private LocalDate dateDepense;

    @NotBlank private String categorie;

    @NotBlank private String nature;

    private String fournisseurBeneficiaire;

    @NotBlank private String designation;

    private BigDecimal montantHt;

    private BigDecimal tva;

    private BigDecimal montantTtc;

    private String modePaiement;

    private String referencePiece;

    private Long soustraitantId;

    private Long ligneDevisId;
}
