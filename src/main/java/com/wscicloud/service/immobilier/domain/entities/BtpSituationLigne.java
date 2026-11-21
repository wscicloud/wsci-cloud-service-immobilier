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
@Table(name = "btp_situation_ligne", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpSituationLigne implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "situation_id", nullable = false)
    private Long situationId;

    @Column(name = "ligne_devis_id", nullable = false)
    private Long ligneDevisId;

    @Column(name = "quantite_precedente", nullable = false)
    private BigDecimal quantitePrecedente;

    @Column(name = "quantite_periode", nullable = false)
    private BigDecimal quantitePeriode;

    @Column(name = "quantite_cumulee", nullable = false)
    private BigDecimal quantiteCumulee;

    @Column(name = "montant_periode_ht", nullable = false)
    private BigDecimal montantPeriodeHt;

    @Column(name = "cree_le", nullable = false)
    private OffsetDateTime creeLe;
}
