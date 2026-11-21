package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class BtpLigneDevisDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long devisId;

    private Long lotId;

    private Long posteId;

    private Long tacheId;

    private Long sousTacheId;

    private Integer ordreAffichage;

    @NotBlank private String designation;

    private String unite;

    private BigDecimal quantite;

    private BigDecimal prixUnitaireHt;

    private BigDecimal montantHt;

    private OffsetDateTime creeLe;
}
