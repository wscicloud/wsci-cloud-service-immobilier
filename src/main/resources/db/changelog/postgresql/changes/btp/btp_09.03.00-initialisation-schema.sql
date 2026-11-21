--liquibase formatted sql

--changeset syao:2026-11-21-btp-00-schema context:btp-init labels:btp-init
-- Schema du module immobilier / BTP (PostgreSQL). Aucun trigger ni vue : les calculs
-- (totaux de situation, avancement par lot, reste a facturer, solde client) sont portes par les services Java.
DROP SCHEMA IF EXISTS immobilier CASCADE;
CREATE SCHEMA immobilier;

--changeset syao:2026-11-21-btp-00b-sequences context:btp-init labels:btp-init
CREATE SEQUENCE immobilier.seq_refm_btp_lot INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_refm_btp_poste INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_refm_btp_tache INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_refm_btp_sous_tache INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_btp_projet INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_btp_devis INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_btp_ligne_devis INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_btp_situation_travaux INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_btp_situation_ligne INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_btp_facture INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_btp_facture_ligne INCREMENT 1 START 1 MINVALUE 1;
CREATE SEQUENCE immobilier.seq_btp_paiement INCREMENT 1 START 1 MINVALUE 1;

--changeset syao:2026-11-21-btp-01-referentiel context:btp-init labels:btp-init
CREATE TABLE immobilier.refm_btp_lot (
    id          BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_refm_btp_lot'),
    code        VARCHAR(20)  NOT NULL UNIQUE,
    libelle     VARCHAR(150) NOT NULL,
    description TEXT
);

CREATE TABLE immobilier.refm_btp_poste (
    id          BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_refm_btp_poste'),
    lot_id      BIGINT       NOT NULL REFERENCES immobilier.refm_btp_lot(id) ON DELETE CASCADE,
    code        VARCHAR(40)  NOT NULL,
    libelle     VARCHAR(180) NOT NULL,
    description TEXT,
    UNIQUE (lot_id, code)
);

CREATE TABLE immobilier.refm_btp_tache (
    id           BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_refm_btp_tache'),
    poste_id     BIGINT       NOT NULL REFERENCES immobilier.refm_btp_poste(id) ON DELETE CASCADE,
    code         VARCHAR(50)  NOT NULL,
    libelle      VARCHAR(220) NOT NULL,
    unite_defaut VARCHAR(20),
    UNIQUE (poste_id, code)
);

CREATE TABLE immobilier.refm_btp_sous_tache (
    id           BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_refm_btp_sous_tache'),
    tache_id     BIGINT       NOT NULL REFERENCES immobilier.refm_btp_tache(id) ON DELETE CASCADE,
    code         VARCHAR(60)  NOT NULL,
    libelle      VARCHAR(255) NOT NULL,
    unite_defaut VARCHAR(20),
    UNIQUE (tache_id, code)
);

--changeset syao:2026-11-21-btp-02-projet-devis context:btp-init labels:btp-init
CREATE TABLE immobilier.btp_projet (
    id               BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_btp_projet'),
    code_projet      VARCHAR(50)    NOT NULL UNIQUE,
    nom_projet       VARCHAR(220)   NOT NULL,
    client_nom       VARCHAR(200)   NOT NULL,
    localisation     VARCHAR(200),
    date_debut       DATE,
    date_fin_prevue  DATE,
    date_fin_reelle  DATE,
    budget_prevu_ht  NUMERIC(15, 2) NOT NULL DEFAULT 0,
    budget_prevu_ttc NUMERIC(15, 2) NOT NULL DEFAULT 0,
    budget_reel_ht   NUMERIC(15, 2) NOT NULL DEFAULT 0,
    budget_reel_ttc  NUMERIC(15, 2) NOT NULL DEFAULT 0,
    marge_prevu_ttc  NUMERIC(15, 2) NOT NULL DEFAULT 0,
    marge_reel_ttc   NUMERIC(15, 2) NOT NULL DEFAULT 0,
    devise           VARCHAR(10)    NOT NULL DEFAULT 'FCFA',
    statut           VARCHAR(30)    NOT NULL DEFAULT 'EN_COURS',
    cree_le          TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE immobilier.btp_devis (
    id           BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_btp_devis'),
    projet_id    BIGINT         NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    numero_devis VARCHAR(60)    NOT NULL UNIQUE,
    date_devis   DATE           NOT NULL DEFAULT CURRENT_DATE,
    montant_ht   NUMERIC(15, 2) NOT NULL DEFAULT 0,
    taux_tva     NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    montant_ttc  NUMERIC(15, 2) NOT NULL DEFAULT 0,
    statut       VARCHAR(30)    NOT NULL DEFAULT 'BROUILLON',
    notes        TEXT,
    cree_le      TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE immobilier.btp_ligne_devis (
    id               BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_btp_ligne_devis'),
    devis_id         BIGINT         NOT NULL REFERENCES immobilier.btp_devis(id) ON DELETE CASCADE,
    lot_id           BIGINT         REFERENCES immobilier.refm_btp_lot(id) ON DELETE SET NULL,
    poste_id         BIGINT         REFERENCES immobilier.refm_btp_poste(id) ON DELETE SET NULL,
    tache_id         BIGINT         REFERENCES immobilier.refm_btp_tache(id) ON DELETE SET NULL,
    sous_tache_id    BIGINT         REFERENCES immobilier.refm_btp_sous_tache(id) ON DELETE SET NULL,
    ordre_affichage  INT            NOT NULL DEFAULT 1,
    designation      VARCHAR(255)   NOT NULL,
    unite            VARCHAR(20),
    quantite         NUMERIC(12, 3) NOT NULL DEFAULT 0,
    prix_unitaire_ht NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_ht       NUMERIC(15, 2) NOT NULL DEFAULT 0,
    cree_le          TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

--changeset syao:2026-11-21-btp-03-situation context:btp-init labels:btp-init
CREATE TABLE immobilier.btp_situation_travaux (
    id                  BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_btp_situation_travaux'),
    projet_id           BIGINT         NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    numero_situation    VARCHAR(50)    NOT NULL,
    date_situation      DATE           NOT NULL DEFAULT CURRENT_DATE,
    periode_debut       DATE,
    periode_fin         DATE,
    taux_avancement_pct NUMERIC(6, 2)  NOT NULL DEFAULT 0,
    montant_periode_ht  NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_cumule_ht   NUMERIC(15, 2) NOT NULL DEFAULT 0,
    statut              VARCHAR(30)    NOT NULL DEFAULT 'BROUILLON',
    observations        TEXT,
    cree_le             TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    UNIQUE (projet_id, numero_situation)
);

CREATE TABLE immobilier.btp_situation_ligne (
    id                  BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_btp_situation_ligne'),
    situation_id        BIGINT         NOT NULL REFERENCES immobilier.btp_situation_travaux(id) ON DELETE CASCADE,
    ligne_devis_id      BIGINT         NOT NULL REFERENCES immobilier.btp_ligne_devis(id) ON DELETE RESTRICT,
    quantite_precedente NUMERIC(12, 3) NOT NULL DEFAULT 0,
    quantite_periode    NUMERIC(12, 3) NOT NULL DEFAULT 0,
    quantite_cumulee    NUMERIC(12, 3) NOT NULL DEFAULT 0,
    montant_periode_ht  NUMERIC(15, 2) NOT NULL DEFAULT 0,
    cree_le             TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    UNIQUE (situation_id, ligne_devis_id)
);

--changeset syao:2026-11-21-btp-04-facturation context:btp-init labels:btp-init
CREATE TABLE immobilier.btp_facture (
    id             BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_btp_facture'),
    projet_id      BIGINT         NOT NULL REFERENCES immobilier.btp_projet(id) ON DELETE CASCADE,
    situation_id   BIGINT         REFERENCES immobilier.btp_situation_travaux(id) ON DELETE SET NULL,
    numero_facture VARCHAR(60)    NOT NULL UNIQUE,
    type_facture   VARCHAR(30)    NOT NULL DEFAULT 'SITUATION',
    date_emission  DATE           NOT NULL DEFAULT CURRENT_DATE,
    date_echeance  DATE,
    montant_ht     NUMERIC(15, 2) NOT NULL DEFAULT 0,
    taux_tva       NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    montant_ttc    NUMERIC(15, 2) NOT NULL DEFAULT 0,
    statut         VARCHAR(30)    NOT NULL DEFAULT 'EMISE',
    cree_le        TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE immobilier.btp_facture_ligne (
    id                 BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_btp_facture_ligne'),
    facture_id         BIGINT         NOT NULL REFERENCES immobilier.btp_facture(id) ON DELETE CASCADE,
    ligne_devis_id     BIGINT         REFERENCES immobilier.btp_ligne_devis(id) ON DELETE SET NULL,
    situation_ligne_id BIGINT         REFERENCES immobilier.btp_situation_ligne(id) ON DELETE SET NULL,
    designation        VARCHAR(255)   NOT NULL,
    unite              VARCHAR(20),
    quantite           NUMERIC(12, 3) NOT NULL DEFAULT 0,
    prix_unitaire_ht   NUMERIC(15, 2) NOT NULL DEFAULT 0,
    montant_ht         NUMERIC(15, 2) NOT NULL DEFAULT 0,
    cree_le            TIMESTAMPTZ    NOT NULL DEFAULT NOW()
);

CREATE TABLE immobilier.btp_paiement (
    id                    BIGINT PRIMARY KEY DEFAULT nextval('immobilier.seq_btp_paiement'),
    facture_id            BIGINT         NOT NULL REFERENCES immobilier.btp_facture(id) ON DELETE RESTRICT,
    date_paiement         TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    montant               NUMERIC(15, 2) NOT NULL DEFAULT 0,
    mode_paiement         VARCHAR(40)    NOT NULL DEFAULT 'VIREMENT',
    reference_transaction VARCHAR(100),
    commentaire           TEXT
);

--changeset syao:2026-11-21-btp-05-index context:btp-init labels:btp-init
CREATE INDEX idx_btp_devis_projet ON immobilier.btp_devis(projet_id);
CREATE INDEX idx_btp_ligne_devis_devis ON immobilier.btp_ligne_devis(devis_id);
CREATE INDEX idx_btp_ligne_devis_lot ON immobilier.btp_ligne_devis(lot_id);
CREATE INDEX idx_btp_ligne_devis_poste ON immobilier.btp_ligne_devis(poste_id);
CREATE INDEX idx_btp_situation_projet ON immobilier.btp_situation_travaux(projet_id);
CREATE INDEX idx_btp_situation_ligne_situation ON immobilier.btp_situation_ligne(situation_id);
CREATE INDEX idx_btp_situation_ligne_ligne ON immobilier.btp_situation_ligne(ligne_devis_id);
CREATE INDEX idx_btp_facture_projet ON immobilier.btp_facture(projet_id);
CREATE INDEX idx_btp_facture_ligne_facture ON immobilier.btp_facture_ligne(facture_id);
CREATE INDEX idx_btp_facture_ligne_ligne ON immobilier.btp_facture_ligne(ligne_devis_id);
CREATE INDEX idx_btp_paiement_facture ON immobilier.btp_paiement(facture_id);
