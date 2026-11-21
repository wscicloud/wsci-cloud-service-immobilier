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
@Table(name = "btp_paiement", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpPaiement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_paiement",
            sequenceName = "immobilier.seq_btp_paiement",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen_btp_paiement")
    private Long id;

    @Column(name = "facture_id", nullable = false)
    private Long factureId;

    @Column(name = "date_paiement", nullable = false)
    private OffsetDateTime datePaiement;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "mode_paiement", nullable = false)
    private String modePaiement;

    @Column(name = "reference_transaction")
    private String referenceTransaction;

    @Column(name = "commentaire")
    private String commentaire;
}
