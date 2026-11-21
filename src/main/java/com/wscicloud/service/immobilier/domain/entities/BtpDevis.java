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
@Table(name = "btp_devis", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpDevis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_devis",
            sequenceName = "immobilier.seq_btp_devis",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_btp_devis")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "numero_devis", nullable = false)
    private String numeroDevis;

    @Column(name = "date_devis", nullable = false)
    private LocalDate dateDevis;

    @Column(name = "montant_ht", nullable = false)
    private BigDecimal montantHt;

    /** Taux de TVA en pourcentage (ex : 18.00). */
    @Column(name = "taux_tva", nullable = false)
    private BigDecimal tauxTva;

    @Column(name = "montant_ttc", nullable = false)
    private BigDecimal montantTtc;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "notes")
    private String notes;

    @Column(name = "cree_le", nullable = false)
    private OffsetDateTime creeLe;
}
