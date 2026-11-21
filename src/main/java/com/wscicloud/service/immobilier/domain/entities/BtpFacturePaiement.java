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
@Table(name = "btp_facture_paiement")
@EqualsAndHashCode(of = "id")
public class BtpFacturePaiement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_facture_paiement",
            sequenceName = "seq_btp_facture_paiement",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_facture_paiement")
    private Long id;

    @Column(name = "facture_id", nullable = false)
    private Long factureId;

    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Column(name = "montant", nullable = false)
    private BigDecimal montant;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @Column(name = "reference")
    private String reference;
}
