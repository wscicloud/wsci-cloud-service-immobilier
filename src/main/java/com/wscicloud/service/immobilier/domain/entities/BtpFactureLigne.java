package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "btp_facture_ligne", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpFactureLigne implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_facture_ligne",
            sequenceName = "seq_btp_facture_ligne",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_facture_ligne")
    private Long id;

    @Column(name = "facture_id", nullable = false)
    private Long factureId;

    @Column(name = "ligne_devis_id")
    private Long ligneDevisId;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "unite")
    private String unite;

    @Column(name = "quantite")
    private BigDecimal quantite;

    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column(name = "montant_ht")
    private BigDecimal montantHt;

    @Column(name = "ordre")
    private Integer ordre;
}
