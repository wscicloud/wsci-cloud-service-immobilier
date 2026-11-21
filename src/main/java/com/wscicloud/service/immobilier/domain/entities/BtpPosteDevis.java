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
@Table(name = "btp_poste_devis", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpPosteDevis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_poste_devis",
            sequenceName = "seq_btp_poste_devis",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_poste_devis")
    private Long id;

    @Column(name = "lot_id", nullable = false)
    private Long lotId;

    @Column(name = "nom_poste", nullable = false)
    private String nomPoste;

    @Column(name = "ordre")
    private Integer ordre;
}
