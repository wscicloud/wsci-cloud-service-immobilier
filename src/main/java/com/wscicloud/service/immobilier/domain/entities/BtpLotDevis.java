package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "btp_lot_devis")
@EqualsAndHashCode(of = "id")
public class BtpLotDevis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_lot_devis",
            sequenceName = "seq_btp_lot_devis",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_lot_devis")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "code_lot", nullable = false)
    private String codeLot;

    @Column(name = "nom_lot", nullable = false)
    private String nomLot;

    @Column(name = "ordre")
    private Integer ordre;
}
