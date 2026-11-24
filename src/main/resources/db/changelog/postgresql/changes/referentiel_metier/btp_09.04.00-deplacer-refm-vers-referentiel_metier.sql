--liquibase formatted sql

--changeset syao:referentiel_metier_schema_immobilier_01 splitStatements:false
-- Deplace les tables refm_btp_* et leurs sequences du schema immobilier vers referentiel_metier.
create schema if not exists referentiel_metier;

alter table if exists immobilier.refm_btp_lot set schema referentiel_metier;
alter table if exists immobilier.refm_btp_poste set schema referentiel_metier;
alter table if exists immobilier.refm_btp_tache set schema referentiel_metier;
alter table if exists immobilier.refm_btp_sous_tache set schema referentiel_metier;

alter sequence if exists immobilier.seq_refm_btp_lot set schema referentiel_metier;
alter sequence if exists immobilier.seq_refm_btp_poste set schema referentiel_metier;
alter sequence if exists immobilier.seq_refm_btp_tache set schema referentiel_metier;
alter sequence if exists immobilier.seq_refm_btp_sous_tache set schema referentiel_metier;
