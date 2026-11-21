package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "btp_facture_ligne", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpFactureLigne implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facture_id", nullable = false)
    private Long factureId;

    @Column(name = "ligne_devis_id")
    private Long ligneDevisId;

    @Column(name = "situation_ligne_id")
    private Long situationLigneId;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "unite")
    private String unite;

    @Column(name = "quantite", nullable = false)
    private BigDecimal quantite;

    @Column(name = "prix_unitaire_ht", nullable = false)
    private BigDecimal prixUnitaireHt;

    @Column(name = "montant_ht", nullable = false)
    private BigDecimal montantHt;

    @Column(name = "cree_le", nullable = false)
    private OffsetDateTime creeLe;
}
