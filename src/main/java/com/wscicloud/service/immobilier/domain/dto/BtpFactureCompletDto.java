package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Facture avec ses lignes, ses paiements et son reste a payer. */
@Getter
@Setter
@NoArgsConstructor
public class BtpFactureCompletDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid private BtpFactureDto facture;

    @Valid private List<BtpFactureLigneDto> lignes = new ArrayList<>();

    private List<BtpPaiementDto> paiements = new ArrayList<>();

    private BigDecimal totalPaye = BigDecimal.ZERO;

    private BigDecimal resteAPayer = BigDecimal.ZERO;
}
