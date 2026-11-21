package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
public class BtpPaiementDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long factureId;

    private OffsetDateTime datePaiement;

    @NotNull private BigDecimal montant;

    private String modePaiement;

    private String referenceTransaction;

    private String commentaire;
}
