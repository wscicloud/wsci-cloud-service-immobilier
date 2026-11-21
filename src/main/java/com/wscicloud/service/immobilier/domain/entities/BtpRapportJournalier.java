package com.wscicloud.service.immobilier.domain.entities;

import java.io.Serializable;
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
@Table(name = "btp_rapport_journalier", schema = "immobilier")
@EqualsAndHashCode(of = "id")
public class BtpRapportJournalier implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(
            name = "gen_btp_rapport_journalier",
            sequenceName = "seq_btp_rapport_journalier",
            initialValue = 1,
            allocationSize = 1)
    @GeneratedValue(generator = "gen_btp_rapport_journalier")
    private Long id;

    @Column(name = "projet_id", nullable = false)
    private Long projetId;

    @Column(name = "date_rapport", nullable = false)
    private LocalDate dateRapport;

    @Column(name = "effectif_presence")
    private Integer effectifPresence;

    @Column(name = "meteo")
    private String meteo;

    @Column(name = "avancement_synthese")
    private String avancementSynthese;

    @Column(name = "incidents_remarques")
    private String incidentsRemarques;

    @Column(name = "redacteur")
    private String redacteur;
}
