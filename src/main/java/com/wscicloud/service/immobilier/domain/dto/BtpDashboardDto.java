package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tableau de bord d'un projet : avancement par lot, reste a facturer et solde client. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BtpDashboardDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long projetId;

    private List<AvancementLot> avancementParLot = new ArrayList<>();

    private ResteAFacturer resteAFacturer;

    private SoldeClient soldeClient;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvancementLot implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long lotId;
        private String lotCode;
        private String lotLibelle;
        private BigDecimal montantDevisHt;
        private BigDecimal montantExecuteHt;
        private BigDecimal resteAExecuterHt;
        private BigDecimal tauxAvancementFinancierPct;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResteAFacturer implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long projetId;
        private BigDecimal montantDevisValideHt;
        private BigDecimal montantTravauxValidesHt;
        private BigDecimal montantFactureHt;
        private BigDecimal resteAFacturerSurDevisHt;
        private BigDecimal resteAFacturerSurTravauxHt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SoldeClient implements Serializable {
        private static final long serialVersionUID = 1L;
        private Long projetId;
        private BigDecimal totalFactureTtc;
        private BigDecimal totalEncaisse;
        private BigDecimal soldeClientAPayer;
    }
}
