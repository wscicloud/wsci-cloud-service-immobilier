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
@Table(name = "btp_soustraitant")
@EqualsAndHashCode(of = "id")
public class BtpSoustraitant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_soustraitant",
            sequenceName = "seq_btp_soustraitant",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_soustraitant")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "metier")
    private String metier;

    @Column(name = "lot")
    private String lot;

    @Column(name = "marche_montant")
    private BigDecimal marcheMontant;

    @Column(name = "avancement_pct")
    private BigDecimal avancementPct;

    @Column(name = "retenue_garantie_pct")
    private BigDecimal retenueGarantiePct;

    @Column(name = "nature_contrat")
    private String natureContrat;

    @Column(name = "fournitures_entreprise")
    private BigDecimal fournituresEntreprise;

    @Column(name = "taux_risque")
    private BigDecimal tauxRisque;

    @Column(name = "taux_sav")
    private BigDecimal tauxSav;
}
