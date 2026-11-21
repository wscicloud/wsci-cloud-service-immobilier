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
public class BtpFactureLigneDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long factureId;

    private Long ligneDevisId;

    @NotBlank private String designation;

    private String unite;

    private BigDecimal quantite;

    private BigDecimal prixUnitaire;

    private BigDecimal montantHt;

    private Integer ordre;
}
