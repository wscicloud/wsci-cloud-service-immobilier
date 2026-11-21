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
@Table(name = "btp_ligne_devis", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpLigneDevis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_ligne_devis",
            sequenceName = "immobilier.seq_btp_ligne_devis",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_btp_ligne_devis")
    private Long id;

    @Column(name = "devis_id", nullable = false)
    private Long devisId;

    @Column(name = "lot_id")
    private Long lotId;

    @Column(name = "poste_id")
    private Long posteId;

    @Column(name = "tache_id")
    private Long tacheId;

    @Column(name = "sous_tache_id")
    private Long sousTacheId;

    @Column(name = "ordre_affichage", nullable = false)
    private Integer ordreAffichage;

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
