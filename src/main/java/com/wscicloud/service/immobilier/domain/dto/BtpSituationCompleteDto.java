package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Situation de travaux avec le detail par ligne et le recapitulatif par poste. */
@Getter
@Setter
@NoArgsConstructor
public class BtpSituationCompleteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private BtpSituationTravauxDto situation;
    private List<LigneDetail> lignes = new ArrayList<>();
    private List<RecapPoste> recapitulatif = new ArrayList<>();
    private BigDecimal totalGrosOeuvre = BigDecimal.ZERO;
    private BigDecimal totalSecondOeuvre = BigDecimal.ZERO;
    private boolean alerteAvancement;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class LigneDetail implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long situationLigneId;
        private Long ligneDevisId;
        private String codeLot;
        private String nomPoste;
        private String sousEnsemble;
        private String designation;
        private String unite;
        private BigDecimal quantite;
        private BigDecimal prixUnitaire;
        private BigDecimal montantMarcheHt;
        private BigDecimal avancementCumulPct;
        private BigDecimal montantCumulHt;
        private BigDecimal avancementPrecedentPct;
        private BigDecimal montantPrecedentHt;
        private BigDecimal avancementPeriodePct;
        private BigDecimal montantPeriodeHt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class RecapPoste implements Serializable {
        private static final long serialVersionUID = 1L;
        private String codeLot;
        private String nomPoste;
        private BigDecimal montantMarcheHt = BigDecimal.ZERO;
        private BigDecimal montantCumulHt = BigDecimal.ZERO;
        private BigDecimal montantPrecedentHt = BigDecimal.ZERO;
        private BigDecimal montantPeriodeHt = BigDecimal.ZERO;
        private BigDecimal avancementPct = BigDecimal.ZERO;
    }
}
