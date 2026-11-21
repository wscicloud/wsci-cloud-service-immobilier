package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

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
public class BtpProjetDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long societeId;

    @NotBlank private String nom;

    private String numeroOperation;

    private String typeOuvrage;

    private String adresseChantier;

    private String villePays;

    private LocalDate dateEtablissement;

    private String client;

    private String moAdresse;

    private String moTelEmail;

    private String maitreOeuvre;

    private String bet;

    private String bureauControle;

    private String entrepriseMandataire;

    private LocalDate dateDebut;

    private Integer dureeMois;

    private BigDecimal margeNetteObjectif;

    private Integer delaiRealisationCibleMois;

    private BigDecimal tauxAvancementSeuilAlerte;

    private BigDecimal margeBruteMinAcceptable;

    private BigDecimal avanceTaux;

    private Integer delaiPaiementJours;

    private BigDecimal savTaux;

    private BigDecimal tauxAleas;

    private BigDecimal tvaTaux;

    private BigDecimal retenueGarantieTaux;

    private BigDecimal fraisGenerauxTaux;

    private BigDecimal margeTaux;

    private String devisNum;

    private LocalDate devisDate;

    private String devisTitre;

    private Boolean devisAfficherTva;

    private Boolean devisExonerationTva;

    private BigDecimal remiseMontant;

    private String statutDevis;
}
