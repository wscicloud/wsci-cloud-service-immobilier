package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class BtpSousDetailPrixDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long ligneId;

    private String codeOuvrage;

    private BigDecimal deboursSecUnitaire;

    private BigDecimal coutMateriaux;

    private BigDecimal coutMateriel;

    private BigDecimal coutMainOeuvre;

    private BigDecimal fraisGeneraux;

    private BigDecimal marge;

    private BigDecimal prixVenteCalcule;
}
