package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "btp_facture", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpFacture implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "situation_id")
    private Long situationId;

    @Column(name = "numero_facture", nullable = false)
    private String numeroFacture;

    @Column(name = "type_facture", nullable = false)
    private String typeFacture;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "montant_ht", nullable = false)
    private BigDecimal montantHt;

    /** Taux de TVA en pourcentage (ex : 18.00). */
    @Column(name = "taux_tva", nullable = false)
    private BigDecimal tauxTva;

    @Column(name = "montant_ttc", nullable = false)
    private BigDecimal montantTtc;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "cree_le", nullable = false)
    private OffsetDateTime creeLe;
}
