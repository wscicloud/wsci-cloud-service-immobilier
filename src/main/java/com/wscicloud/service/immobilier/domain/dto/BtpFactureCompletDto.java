package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Facture avec ses lignes et ses paiements. */
@Getter
@Setter
@NoArgsConstructor
public class BtpFactureCompletDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private BtpFactureDto facture;
    private List<BtpFactureLigneDto> lignes = new ArrayList<>();
    private List<BtpFacturePaiementDto> paiements = new ArrayList<>();
}
