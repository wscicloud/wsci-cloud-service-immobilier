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
@Table(name = "btp_facture")
@EqualsAndHashCode(of = "id")
public class BtpFacture implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "gen_btp_facture", sequenceName = "seq_btp_facture", initialValue = 1, allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_facture")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "situation_id")
    private Long situationId;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "date_facture", nullable = false)
    private LocalDate dateFacture;

    @Column(name = "date_echeance")
    private LocalDate dateEcheance;

    @Column(name = "objet")
    private String objet;

    @Column(name = "montant_ht")
    private BigDecimal montantHt;

    @Column(name = "tva_taux")
    private BigDecimal tvaTaux;

    @Column(name = "montant_tva")
    private BigDecimal montantTva;

    @Column(name = "retenue_garantie_taux")
    private BigDecimal retenueGarantieTaux;

    @Column(name = "retenue_garantie_montant")
    private BigDecimal retenueGarantieMontant;

    @Column(name = "avance_taux")
    private BigDecimal avanceTaux;

    @Column(name = "avance_montant")
    private BigDecimal avanceMontant;

    @Column(name = "montant_ttc")
    private BigDecimal montantTtc;

    @Column(name = "net_a_payer")
    private BigDecimal netAPayer;

    @Column(name = "montant_paye")
    private BigDecimal montantPaye;

    @Column(name = "statut")
    private String statut;
}
