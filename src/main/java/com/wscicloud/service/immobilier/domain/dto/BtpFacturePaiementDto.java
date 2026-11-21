package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
public class BtpFacturePaiementDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long factureId;

    @NotNull private LocalDate datePaiement;

    @NotNull private BigDecimal montant;

    private String modePaiement;

    private String reference;
}
