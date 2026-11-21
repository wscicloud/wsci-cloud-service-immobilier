package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Devis complet d'un projet : arbre Lot -> Poste -> Lignes avec totaux. */
@Getter
@Setter
@NoArgsConstructor
public class BtpDevisDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private BtpProjetDto projet;
    private List<Lot> lots = new ArrayList<>();
    private BigDecimal totalHt = BigDecimal.ZERO;
    private BigDecimal remise = BigDecimal.ZERO;
    private BigDecimal totalApresRemise = BigDecimal.ZERO;
    private BigDecimal tauxTva = BigDecimal.ZERO;
    private BigDecimal montantTva = BigDecimal.ZERO;
    private BigDecimal totalTtc = BigDecimal.ZERO;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Lot implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String codeLot;
        private String nomLot;
        private Integer ordre;
        private BigDecimal totalHt = BigDecimal.ZERO;
        private List<Poste> postes = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Poste implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String nomPoste;
        private Integer ordre;
        private BigDecimal totalHt = BigDecimal.ZERO;
        private List<BtpLigneDevisDto> lignes = new ArrayList<>();
    }
}
