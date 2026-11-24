--liquibase formatted sql

--changeset syao:2026-11-24_00 context:customize labels:customize
alter table immobilier.btp_projet add column societe_id bigint;
--changeset syao:2026-11-24_01 context:customize labels:customize
CREATE INDEX idx_btp_projet_societeid ON immobilier.btp_projet(societe_id);
--changeset syao:2026-11-24_02 context:customize labels:customize
update immobilier.btp_projet set societe_id = 5 where societe_id is null;

