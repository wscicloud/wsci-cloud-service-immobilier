--liquibase formatted sql

--changeset syao:2026-06-01-immobilier-schema context:btp-init labels:btp-init
CREATE SCHEMA IF NOT EXISTS immobilier;

-- Module BTP (immobilier) : structure et donnees initiales (PostgreSQL)

--changeset syao:2026-06-01-btp-projet context:btp-init labels:btp-init
CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_projet START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_projet (
    id BIGINT PRIMARY KEY,
    societe_id BIGINT REFERENCES public.sys_societe(id),
    nom VARCHAR(255) NOT NULL,
    numero_operation VARCHAR(100),
    type_ouvrage VARCHAR(150),
    adresse_chantier TEXT,
    ville_pays VARCHAR(150),
    date_etablissement DATE,
    client VARCHAR(255),
    mo_adresse TEXT,
    mo_tel_email VARCHAR(255),
    maitre_oeuvre VARCHAR(255),
    bet VARCHAR(255),
    bureau_controle VARCHAR(255),
    entreprise_mandataire VARCHAR(255),
    date_debut DATE,
    duree_mois INT DEFAULT 12,
    marge_nette_objectif NUMERIC(5,4) DEFAULT 0.0800,
    delai_realisation_cible_mois INT DEFAULT 12,
    taux_avancement_seuil_alerte NUMERIC(5,4) DEFAULT 0.8500,
    marge_brute_min_acceptable NUMERIC(5,4) DEFAULT 0.0500,
    avance_taux NUMERIC(5,4) DEFAULT 0.0400,
    delai_paiement_jours INT DEFAULT 30,
    sav_taux NUMERIC(5,4) DEFAULT 0.0314,
    taux_aleas NUMERIC(5,4) DEFAULT 0.0260,
    tva_taux NUMERIC(5,4) DEFAULT 0.1800,
    retenue_garantie_taux NUMERIC(5,4) DEFAULT 0.0500,
    frais_generaux_taux NUMERIC(5,4) DEFAULT 0.0444,
    marge_taux NUMERIC(5,4) DEFAULT 0.0800,
    devis_num VARCHAR(100),
    devis_date DATE,
    devis_titre TEXT DEFAULT 'DEVIS QUANTITATIF ET ESTIMATIF DES TRAVAUX',
    devis_afficher_tva BOOLEAN DEFAULT FALSE,
    devis_exoneration_tva BOOLEAN DEFAULT FALSE,
    remise_montant NUMERIC(15,2) DEFAULT 0.00,
    statut_devis VARCHAR(20) DEFAULT 'BROUILLON',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_btp_projet_societe ON immobilier.btp_projet(societe_id);

--changeset syao:2026-06-01-btp-devis-structure context:btp-init labels:btp-init
CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_lot_devis START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_lot_devis (
    id BIGINT PRIMARY KEY,
    projet_id BIGINT NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    code_lot VARCHAR(50) NOT NULL,
    nom_lot VARCHAR(255) NOT NULL,
    ordre INT DEFAULT 0,
    CONSTRAINT uk_btp_lot_devis UNIQUE (projet_id, code_lot)
);
CREATE INDEX IF NOT EXISTS idx_btp_lot_devis_projet ON immobilier.btp_lot_devis(projet_id);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_poste_devis START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_poste_devis (
    id BIGINT PRIMARY KEY,
    lot_id BIGINT NOT NULL REFERENCES immobilier.btp_lot_devis(id) ON DELETE CASCADE,
    nom_poste VARCHAR(255) NOT NULL,
    ordre INT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_btp_poste_devis_lot ON immobilier.btp_poste_devis(lot_id);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_ligne_devis START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_ligne_devis (
    id BIGINT PRIMARY KEY,
    poste_id BIGINT NOT NULL REFERENCES immobilier.btp_poste_devis(id) ON DELETE CASCADE,
    sous_ensemble VARCHAR(255),
    designation TEXT NOT NULL,
    unite VARCHAR(20) NOT NULL,
    quantite NUMERIC(15,4) NOT NULL DEFAULT 0,
    prix_unitaire NUMERIC(15,2) NOT NULL DEFAULT 0,
    montant_ht NUMERIC(15,2) NOT NULL DEFAULT 0,
    niveau VARCHAR(100) DEFAULT 'Tous niveaux',
    ordre INT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_btp_ligne_devis_poste ON immobilier.btp_ligne_devis(poste_id);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_sous_detail_prix START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_sous_detail_prix (
    id BIGINT PRIMARY KEY,
    ligne_id BIGINT NOT NULL UNIQUE REFERENCES immobilier.btp_ligne_devis(id) ON DELETE CASCADE,
    code_ouvrage VARCHAR(50),
    debours_sec_unitaire NUMERIC(15,2) DEFAULT 0.00,
    cout_materiaux NUMERIC(15,2) DEFAULT 0.00,
    cout_materiel NUMERIC(15,2) DEFAULT 0.00,
    cout_main_oeuvre NUMERIC(15,2) DEFAULT 0.00,
    frais_generaux NUMERIC(15,2) DEFAULT 0.00,
    marge NUMERIC(15,2) DEFAULT 0.00,
    prix_vente_calcule NUMERIC(15,2) DEFAULT 0.00
);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_devis_version START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_devis_version (
    id BIGINT PRIMARY KEY,
    projet_id BIGINT NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    numero_version INT NOT NULL,
    date_version DATE NOT NULL DEFAULT CURRENT_DATE,
    statut VARCHAR(20) DEFAULT 'BROUILLON',
    commentaire TEXT,
    montant_total_ht NUMERIC(15,2) DEFAULT 0.00,
    snapshot_json TEXT,
    CONSTRAINT uk_btp_devis_version UNIQUE (projet_id, numero_version)
);
CREATE INDEX IF NOT EXISTS idx_btp_devis_version_projet ON immobilier.btp_devis_version(projet_id);

--changeset syao:2026-06-01-btp-suivi context:btp-init labels:btp-init
CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_soustraitant START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_soustraitant (
    id BIGINT PRIMARY KEY,
    projet_id BIGINT NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    nom VARCHAR(255) NOT NULL,
    metier VARCHAR(150),
    lot VARCHAR(50),
    marche_montant NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    avancement_pct NUMERIC(5,4) DEFAULT 0.0000,
    retenue_garantie_pct NUMERIC(5,4) DEFAULT 0.0500,
    nature_contrat VARCHAR(100) DEFAULT 'Main d''oeuvre uniquement',
    fournitures_entreprise NUMERIC(15,2) DEFAULT 0.00,
    taux_risque NUMERIC(5,4) DEFAULT 0.0200,
    taux_sav NUMERIC(5,4) DEFAULT 0.0100
);
CREATE INDEX IF NOT EXISTS idx_btp_soustraitant_projet ON immobilier.btp_soustraitant(projet_id);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_depense_reelle START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_depense_reelle (
    id BIGINT PRIMARY KEY,
    projet_id BIGINT NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    societe_id BIGINT REFERENCES public.sys_societe(id),
    date_depense DATE NOT NULL,
    categorie VARCHAR(100) NOT NULL,
    nature VARCHAR(150) NOT NULL,
    fournisseur_beneficiaire VARCHAR(255),
    designation TEXT NOT NULL,
    montant_ht NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    tva NUMERIC(15,2) DEFAULT 0.00,
    montant_ttc NUMERIC(15,2) DEFAULT 0.00,
    mode_paiement VARCHAR(50),
    reference_piece VARCHAR(100),
    soustraitant_id BIGINT REFERENCES immobilier.btp_soustraitant(id) ON DELETE SET NULL,
    ligne_devis_id BIGINT REFERENCES immobilier.btp_ligne_devis(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_btp_depense_projet_date ON immobilier.btp_depense_reelle(projet_id, date_depense);
CREATE INDEX IF NOT EXISTS idx_btp_depense_societe ON immobilier.btp_depense_reelle(societe_id);
CREATE INDEX IF NOT EXISTS idx_btp_depense_soustraitant ON immobilier.btp_depense_reelle(soustraitant_id);
CREATE INDEX IF NOT EXISTS idx_btp_depense_ligne ON immobilier.btp_depense_reelle(ligne_devis_id);

--changeset syao:2026-06-01-btp-situation context:btp-init labels:btp-init
CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_situation_travaux START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_situation_travaux (
    id BIGINT PRIMARY KEY,
    projet_id BIGINT NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    numero INT NOT NULL DEFAULT 1,
    mois INT NOT NULL,
    annee INT NOT NULL,
    date_situation DATE NOT NULL,
    montant_marche_ht NUMERIC(15,2) DEFAULT 0.00,
    montant_cumule_ht NUMERIC(15,2) DEFAULT 0.00,
    montant_precedent_ht NUMERIC(15,2) DEFAULT 0.00,
    montant_periode_ht NUMERIC(15,2) DEFAULT 0.00,
    remise_cumul NUMERIC(15,2) DEFAULT 0.00,
    remise_precedent NUMERIC(15,2) DEFAULT 0.00,
    remise_periode NUMERIC(15,2) DEFAULT 0.00,
    montant_apres_remise NUMERIC(15,2) DEFAULT 0.00,
    avancement_global_pct NUMERIC(5,4) DEFAULT 0.0000,
    statut VARCHAR(20) DEFAULT 'BROUILLON'
);
CREATE INDEX IF NOT EXISTS idx_btp_situation_projet ON immobilier.btp_situation_travaux(projet_id, annee, mois);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_situation_ligne START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_situation_ligne (
    id BIGINT PRIMARY KEY,
    situation_id BIGINT NOT NULL REFERENCES immobilier.btp_situation_travaux(id) ON DELETE CASCADE,
    ligne_devis_id BIGINT NOT NULL REFERENCES immobilier.btp_ligne_devis(id) ON DELETE CASCADE,
    avancement_cumul_pct NUMERIC(7,4) DEFAULT 0.0000,
    montant_cumul_ht NUMERIC(15,2) DEFAULT 0.00,
    avancement_precedent_pct NUMERIC(7,4) DEFAULT 0.0000,
    montant_precedent_ht NUMERIC(15,2) DEFAULT 0.00,
    avancement_periode_pct NUMERIC(7,4) DEFAULT 0.0000,
    montant_periode_ht NUMERIC(15,2) DEFAULT 0.00,
    CONSTRAINT uk_btp_situation_ligne UNIQUE (situation_id, ligne_devis_id)
);
CREATE INDEX IF NOT EXISTS idx_btp_situation_ligne_situation ON immobilier.btp_situation_ligne(situation_id);
CREATE INDEX IF NOT EXISTS idx_btp_situation_ligne_ligne ON immobilier.btp_situation_ligne(ligne_devis_id);

--changeset syao:2026-06-01-btp-facture context:btp-init labels:btp-init
CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_facture START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_facture (
    id BIGINT PRIMARY KEY,
    projet_id BIGINT NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    situation_id BIGINT REFERENCES immobilier.btp_situation_travaux(id) ON DELETE SET NULL,
    numero VARCHAR(100) NOT NULL,
    date_facture DATE NOT NULL,
    date_echeance DATE,
    objet TEXT,
    montant_ht NUMERIC(15,2) DEFAULT 0.00,
    tva_taux NUMERIC(5,4) DEFAULT 0.1800,
    montant_tva NUMERIC(15,2) DEFAULT 0.00,
    retenue_garantie_taux NUMERIC(5,4) DEFAULT 0.0500,
    retenue_garantie_montant NUMERIC(15,2) DEFAULT 0.00,
    avance_taux NUMERIC(5,4) DEFAULT 0.0400,
    avance_montant NUMERIC(15,2) DEFAULT 0.00,
    montant_ttc NUMERIC(15,2) DEFAULT 0.00,
    net_a_payer NUMERIC(15,2) DEFAULT 0.00,
    montant_paye NUMERIC(15,2) DEFAULT 0.00,
    statut VARCHAR(20) DEFAULT 'BROUILLON'
);
CREATE INDEX IF NOT EXISTS idx_btp_facture_projet ON immobilier.btp_facture(projet_id);
CREATE INDEX IF NOT EXISTS idx_btp_facture_situation ON immobilier.btp_facture(situation_id);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_facture_ligne START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_facture_ligne (
    id BIGINT PRIMARY KEY,
    facture_id BIGINT NOT NULL REFERENCES immobilier.btp_facture(id) ON DELETE CASCADE,
    ligne_devis_id BIGINT REFERENCES immobilier.btp_ligne_devis(id) ON DELETE SET NULL,
    designation TEXT NOT NULL,
    unite VARCHAR(20),
    quantite NUMERIC(15,4) DEFAULT 0,
    prix_unitaire NUMERIC(15,2) DEFAULT 0.00,
    montant_ht NUMERIC(15,2) DEFAULT 0.00,
    ordre INT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_btp_facture_ligne_facture ON immobilier.btp_facture_ligne(facture_id);
CREATE INDEX IF NOT EXISTS idx_btp_facture_ligne_ligne ON immobilier.btp_facture_ligne(ligne_devis_id);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_facture_paiement START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_facture_paiement (
    id BIGINT PRIMARY KEY,
    facture_id BIGINT NOT NULL REFERENCES immobilier.btp_facture(id) ON DELETE CASCADE,
    date_paiement DATE NOT NULL,
    montant NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    mode_paiement VARCHAR(50),
    reference VARCHAR(100)
);
CREATE INDEX IF NOT EXISTS idx_btp_facture_paiement_facture ON immobilier.btp_facture_paiement(facture_id);

--changeset syao:2026-06-01-btp-planning context:btp-init labels:btp-init
CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_tache_planning START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_tache_planning (
    id BIGINT PRIMARY KEY,
    projet_id BIGINT NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    code_tache VARCHAR(50),
    nom_tache VARCHAR(255) NOT NULL,
    lot VARCHAR(100),
    date_debut DATE NOT NULL,
    duree_jours INT NOT NULL DEFAULT 1,
    date_fin DATE NOT NULL,
    avancement_pct NUMERIC(5,4) DEFAULT 0.0000,
    predecesseurs JSONB DEFAULT '[]'::jsonb
);
CREATE INDEX IF NOT EXISTS idx_btp_tache_planning_projet ON immobilier.btp_tache_planning(projet_id);

CREATE SEQUENCE IF NOT EXISTS immobilier.seq_btp_rapport_journalier START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.btp_rapport_journalier (
    id BIGINT PRIMARY KEY,
    projet_id BIGINT NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    date_rapport DATE NOT NULL,
    effectif_presence INT DEFAULT 0,
    meteo VARCHAR(100),
    avancement_synthese TEXT,
    incidents_remarques TEXT,
    redacteur VARCHAR(150),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_btp_rapport_projet_date ON immobilier.btp_rapport_journalier(projet_id, date_rapport);

--changeset syao:2026-06-01-btp-biblio context:btp-init labels:btp-init
CREATE SEQUENCE IF NOT EXISTS immobilier.seq_refm_btp_tache_biblio START WITH 1 INCREMENT BY 1;
CREATE TABLE IF NOT EXISTS immobilier.refm_btp_tache_biblio (
    id BIGINT PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    tache VARCHAR(255) NOT NULL,
    lot VARCHAR(100),
    categorie VARCHAR(100),
    unite VARCHAR(20),
    rendement NUMERIC(10,2),
    chef INT DEFAULT 0,
    compagnon INT DEFAULT 0,
    manoeuvres INT DEFAULT 0,
    specialiste INT DEFAULT 0,
    couleur_hex VARCHAR(10) DEFAULT '3B82F6'
);

