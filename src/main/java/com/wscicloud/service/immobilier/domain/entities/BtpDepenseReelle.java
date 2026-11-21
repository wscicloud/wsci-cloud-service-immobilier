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
@Table(name = "btp_depense_reelle", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpDepenseReelle implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_depense_reelle",
            sequenceName = "seq_btp_depense_reelle",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_depense_reelle")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "societe_id")
    private Long societeId;

    @Column(name = "date_depense", nullable = false)
    private LocalDate dateDepense;

    @Column(name = "categorie", nullable = false)
    private String categorie;

    @Column(name = "nature", nullable = false)
    private String nature;

    @Column(name = "fournisseur_beneficiaire")
    private String fournisseurBeneficiaire;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "montant_ht")
    private BigDecimal montantHt;

    @Column(name = "tva")
    private BigDecimal tva;

    @Column(name = "montant_ttc")
    private BigDecimal montantTtc;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @Column(name = "reference_piece")
    private String referencePiece;

    @Column(name = "soustraitant_id")
    private Long soustraitantId;

    @Column(name = "ligne_devis_id")
    private Long ligneDevisId;
}
