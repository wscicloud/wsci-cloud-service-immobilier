--liquibase formatted sql

--changeset syao:2026-11-25_00 context:customize labels:customize
alter table immobilier.btp_projet add column devise_id bigint;
alter table immobilier.btp_projet add column devise_libelle varchar(100);

alter table immobilier.btp_projet add column statut_id bigint;
alter table immobilier.btp_projet add column statut_valeur varchar(100);
alter table immobilier.btp_projet add column statut_libelle varchar(100);
