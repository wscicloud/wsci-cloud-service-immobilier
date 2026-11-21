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
@Table(name = "btp_devis_version", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpDevisVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_devis_version",
            sequenceName = "seq_btp_devis_version",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_devis_version")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    @Column(name = "date_version")
    private LocalDate dateVersion;

    @Column(name = "statut")
    private String statut;

    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "montant_total_ht")
    private BigDecimal montantTotalHt;

    @Column(name = "snapshot_json")
    private String snapshotJson;
}
