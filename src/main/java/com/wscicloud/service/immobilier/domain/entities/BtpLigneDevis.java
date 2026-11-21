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
@Table(name = "btp_ligne_devis")
@EqualsAndHashCode(of = "id")
public class BtpLigneDevis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_ligne_devis",
            sequenceName = "seq_btp_ligne_devis",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_ligne_devis")
    private Long id;

    @Column(name = "poste_id", nullable = false)
    private Long posteId;

    @Column(name = "sous_ensemble")
    private String sousEnsemble;

    @Column(name = "designation", nullable = false)
    private String designation;

    @Column(name = "unite", nullable = false)
    private String unite;

    @Column(name = "quantite")
    private BigDecimal quantite;

    @Column(name = "prix_unitaire")
    private BigDecimal prixUnitaire;

    @Column(name = "montant_ht")
    private BigDecimal montantHt;

    @Column(name = "niveau")
    private String niveau;

    @Column(name = "ordre")
    private Integer ordre;
}
