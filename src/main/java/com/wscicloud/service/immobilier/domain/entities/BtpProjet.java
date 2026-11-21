package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "btp_projet", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpProjet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "gen_btp_projet", sequenceName = "seq_btp_projet", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_projet")
    private Long id;

    @Column(name = "societe_id")
    private Long societeId;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "numero_operation")
    private String numeroOperation;

    @Column(name = "type_ouvrage")
    private String typeOuvrage;

    @Column(name = "adresse_chantier")
    private String adresseChantier;

    @Column(name = "ville_pays")
    private String villePays;

    @Column(name = "date_etablissement")
    private LocalDate dateEtablissement;

    @Column(name = "client")
    private String client;

    @Column(name = "mo_adresse")
    private String moAdresse;

    @Column(name = "mo_tel_email")
    private String moTelEmail;

    @Column(name = "maitre_oeuvre")
    private String maitreOeuvre;

    @Column(name = "bet")
    private String bet;

    @Column(name = "bureau_controle")
    private String bureauControle;

    @Column(name = "entreprise_mandataire")
    private String entrepriseMandataire;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "duree_mois")
    private Integer dureeMois;

    @Column(name = "marge_nette_objectif")
    private BigDecimal margeNetteObjectif;

    @Column(name = "delai_realisation_cible_mois")
    private Integer delaiRealisationCibleMois;

    @Column(name = "taux_avancement_seuil_alerte")
    private BigDecimal tauxAvancementSeuilAlerte;

    @Column(name = "marge_brute_min_acceptable")
    private BigDecimal margeBruteMinAcceptable;

    @Column(name = "avance_taux")
    private BigDecimal avanceTaux;

    @Column(name = "delai_paiement_jours")
    private Integer delaiPaiementJours;

    @Column(name = "sav_taux")
    private BigDecimal savTaux;

    @Column(name = "taux_aleas")
    private BigDecimal tauxAleas;

    @Column(name = "tva_taux")
    private BigDecimal tvaTaux;

    @Column(name = "retenue_garantie_taux")
    private BigDecimal retenueGarantieTaux;

    @Column(name = "frais_generaux_taux")
    private BigDecimal fraisGenerauxTaux;

    @Column(name = "marge_taux")
    private BigDecimal margeTaux;

    @Column(name = "devis_num")
    private String devisNum;

    @Column(name = "devis_date")
    private LocalDate devisDate;

    @Column(name = "devis_titre")
    private String devisTitre;

    @Column(name = "devis_afficher_tva")
    private Boolean devisAfficherTva;

    @Column(name = "devis_exoneration_tva")
    private Boolean devisExonerationTva;

    @Column(name = "remise_montant")
    private BigDecimal remiseMontant;

    @Column(name = "statut_devis")
    private String statutDevis;
}
