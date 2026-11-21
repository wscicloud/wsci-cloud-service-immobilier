package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
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
public class RefmBtpTacheBiblioDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank private String code;

    @NotBlank private String tache;

    private String lot;

    private String categorie;

    private String unite;

    private BigDecimal rendement;

    private Integer chef;

    private Integer compagnon;

    private Integer manoeuvres;

    private Integer specialiste;

    private String couleurHex;
}
