package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Ecart budget / realise d'un projet. */
@Getter
@Setter
@NoArgsConstructor
public class BtpEcartDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long projetId;
    private BigDecimal budgetHt = BigDecimal.ZERO;
    private BigDecimal realiseHt = BigDecimal.ZERO;
    private BigDecimal ecart = BigDecimal.ZERO;
    private BigDecimal margeBrute = BigDecimal.ZERO;
    private BigDecimal margeBrutePct = BigDecimal.ZERO;
    private BigDecimal margeBruteMinAcceptable = BigDecimal.ZERO;
    private boolean alerteMarge;
    private List<Categorie> parCategorie = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Categorie implements Serializable {
        private static final long serialVersionUID = 1L;
        private String categorie;
        private BigDecimal montantHt = BigDecimal.ZERO;
    }
}
