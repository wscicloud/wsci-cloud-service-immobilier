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
@Table(name = "btp_projet", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpProjet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_projet",
            sequenceName = "immobilier.seq_btp_projet",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_btp_projet")
    private Long id;

    @Column(name = "societe_id", nullable = false)
    private Long societeId;

    @Column(name = "code_projet", nullable = false)
    private String codeProjet;

    @Column(name = "nom_projet", nullable = false)
    private String nomProjet;

    @Column(name = "client_nom", nullable = false)
    private String clientNom;

    @Column(name = "localisation")
    private String localisation;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin_prevue")
    private LocalDate dateFinPrevue;

    @Column(name = "date_fin_reelle")
    private LocalDate dateFinReelle;

    @Column(name = "budget_prevu_ht", nullable = false)
    private BigDecimal budgetPrevuHt;

    @Column(name = "budget_prevu_ttc", nullable = false)
    private BigDecimal budgetPrevuTtc;

    @Column(name = "budget_reel_ht", nullable = false)
    private BigDecimal budgetReelHt;

    @Column(name = "budget_reel_ttc", nullable = false)
    private BigDecimal budgetReelTtc;

    @Column(name = "marge_prevu_ttc", nullable = false)
    private BigDecimal margePrevuTtc;

    @Column(name = "marge_reel_ttc", nullable = false)
    private BigDecimal margeReelTtc;

    @Column(name = "devise", nullable = false)
    private String devise;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "devise_id")
    private Long deviseId;

    @Column(name = "devise_libelle")
    private String deviseLibelle;

    @Column(name = "statut_id")
    private Long statutId;

    @Column(name = "statut_valeur")
    private String statutValeur;

    @Column(name = "statut_libelle")
    private String statutLibelle;

    @Column(name = "cree_le", nullable = false)
    private OffsetDateTime creeLe;
}
