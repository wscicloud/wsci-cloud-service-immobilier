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
public class BtpDevisVersionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long projetId;

    @NotNull private Integer numeroVersion;

    private LocalDate dateVersion;

    private String statut;

    private String commentaire;

    private BigDecimal montantTotalHt;

    private String snapshotJson;
}
