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
@Table(name = "btp_sous_detail_prix")
@EqualsAndHashCode(of = "id")
public class BtpSousDetailPrix implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_sous_detail_prix",
            sequenceName = "seq_btp_sous_detail_prix",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_sous_detail_prix")
    private Long id;

    @Column(name = "ligne_id", nullable = false)
    private Long ligneId;

    @Column(name = "code_ouvrage")
    private String codeOuvrage;

    @Column(name = "debours_sec_unitaire")
    private BigDecimal deboursSecUnitaire;

    @Column(name = "cout_materiaux")
    private BigDecimal coutMateriaux;

    @Column(name = "cout_materiel")
    private BigDecimal coutMateriel;

    @Column(name = "cout_main_oeuvre")
    private BigDecimal coutMainOeuvre;

    @Column(name = "frais_generaux")
    private BigDecimal fraisGeneraux;

    @Column(name = "marge")
    private BigDecimal marge;

    @Column(name = "prix_vente_calcule")
    private BigDecimal prixVenteCalcule;
}
