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
public class BtpLigneDevisDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long posteId;

    private String sousEnsemble;

    @NotBlank private String designation;

    @NotBlank private String unite;

    private BigDecimal quantite;

    private BigDecimal prixUnitaire;

    private BigDecimal montantHt;

    private String niveau;

    private Integer ordre;
}
