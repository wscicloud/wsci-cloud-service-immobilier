--liquibase formatted sql

--changeset syao:2026-06-01-btp-init-projets context:btp-init labels:btp-init
INSERT INTO immobilier.btp_projet (id, societe_id, nom, numero_operation, type_ouvrage, adresse_chantier, ville_pays, date_etablissement,
    client, date_debut, duree_mois, devis_num, devis_date, remise_montant, statut_devis)
SELECT nextval('immobilier.seq_btp_projet'), (SELECT MIN(id) FROM public.sys_societe), 'IMMEUBLE R+5 AMOKA', 'IAR5 02-02022025', 'B?timent R+5 - Logements',
    'BINGERVILLE - ANA GBONTCHUI', 'Abidjan - C?te d''Ivoire', DATE '2026-06-14', 'PRIVE', DATE '2025-09-08', 24,
    'IAR5 02-02022025', DATE '2026-06-14', 0, 'BROUILLON'
WHERE NOT EXISTS (SELECT 1 FROM immobilier.btp_projet WHERE nom = 'IMMEUBLE R+5 AMOKA');

INSERT INTO immobilier.btp_projet (id, societe_id, nom, numero_operation, type_ouvrage, adresse_chantier, ville_pays, date_etablissement,
    client, date_debut, duree_mois, devis_num, devis_date, remise_montant, statut_devis)
SELECT nextval('immobilier.seq_btp_projet'), (SELECT MIN(id) FROM public.sys_societe), 'TRIPLEX SIMEON 6 PI?CES', 'YKS05-12102023', 'Villa Triplex 6 pi?ces',
    'BINGERVILLE', 'Abidjan - C?te d''Ivoire', DATE '2023-10-12', 'Monsieur YAO KONAN SIMEON', DATE '2023-11-01', 12,
    'YKS05-12102023', DATE '2023-10-12', 3000000, 'VALIDE'
WHERE NOT EXISTS (SELECT 1 FROM immobilier.btp_projet WHERE nom = 'TRIPLEX SIMEON 6 PI?CES');

--changeset syao:2026-06-01-btp-init-lots-postes context:btp-init labels:btp-init
INSERT INTO immobilier.btp_lot_devis (id, projet_id, code_lot, nom_lot, ordre)
SELECT nextval('immobilier.seq_btp_lot_devis'), p.id, v.code, v.nom, v.ordre
FROM immobilier.btp_projet p
CROSS JOIN (VALUES ('GO', 'Gros ?uvre', 1), ('SO', 'Second ?uvre', 2), ('VRD', 'VRD', 3)) AS v(code, nom, ordre)
WHERE p.nom IN ('IMMEUBLE R+5 AMOKA', 'TRIPLEX SIMEON 6 PI?CES')
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_lot_devis l WHERE l.projet_id = p.id AND l.code_lot = v.code);

INSERT INTO immobilier.btp_poste_devis (id, lot_id, nom_poste, ordre)
SELECT nextval('immobilier.seq_btp_poste_devis'), l.id, v.nom, v.ordre
FROM immobilier.btp_lot_devis l JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES ('IMMEUBLE R+5 AMOKA', 'GO', 'A) TERRASSEMENTS', 1), ('IMMEUBLE R+5 AMOKA', 'GO', 'B) FONDATION', 2),
  ('IMMEUBLE R+5 AMOKA', 'GO', 'SUPERSTRUCTURE', 3), ('IMMEUBLE R+5 AMOKA', 'SO', 'APPAREILS SANITAIRES', 1),
  ('IMMEUBLE R+5 AMOKA', 'SO', 'DISTRIBUTION SECONDAIRE', 2),


  ('TRIPLEX SIMEON 6 PI?CES', 'GO', 'ETUDES ET INSTALLATION', 1),
  ('TRIPLEX SIMEON 6 PI?CES', 'GO', 'INFRASTRUCTURE', 2),
  ('TRIPLEX SIMEON 6 PI?CES', 'GO', 'ELEVATION RDC', 3),
  ('TRIPLEX SIMEON 6 PI?CES', 'GO', 'ELEVATION ETAGE 1', 4),
  ('TRIPLEX SIMEON 6 PI?CES', 'GO', 'ELEVATION ETAGE 2', 5),
  ('TRIPLEX SIMEON 6 PI?CES', 'SO', 'PLOMBERIE ET ASSAINISSEMENT', 1),
  ('TRIPLEX SIMEON 6 PI?CES', 'SO', 'ELECTRICITE - CLIMATISATION', 2),
  ('TRIPLEX SIMEON 6 PI?CES', 'SO', 'REVETEMENTS DURS', 3),
  ('TRIPLEX SIMEON 6 PI?CES', 'SO', 'MENUISERIE BOIS - ALUMINIUM', 4),
  ('TRIPLEX SIMEON 6 PI?CES', 'SO', 'PEINTURE', 5)
) AS v(projet, lot, nom, ordre) ON v.projet = p.nom AND v.lot = l.code_lot
WHERE NOT EXISTS (SELECT 1 FROM immobilier.btp_poste_devis x WHERE x.lot_id = l.id AND x.nom_poste = v.nom);

--changeset syao:2026-06-01-btp-init-lignes-amoka context:btp-init labels:btp-init
INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, ROUND(v.quantite * v.pu, 2), v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
 ('A) TERRASSEMENTS','TERRASSEMENT','Nivellement ou decapage terre vegetale','m?',800.000,500.00,'RDC',1),
 ('A) TERRASSEMENTS','TERRASSEMENT','Implantation des ouvrages','ens',1.000,450000.00,'RDC',2),
 ('A) TERRASSEMENTS','TERRASSEMENT','Fouilles en rigole sous murs de soubassement','ml',19.700,6500.00,'Fondations / Soubassement',3),
 ('B) FONDATION','FONDATION','b?ton de propret? dos? ? 150 kg /m3 epassieur de 5 cm','m?',15.860,75000.00,'Fondations / Soubassement',1),
 ('B) FONDATION','FONDATION','semelles isol?es dos?es ? 350 kg / m3 - a) b?ton','m?',92.100,90000.00,'Fondations / Soubassement',2),
 ('B) FONDATION','FONDATION','semelles isol?es dos?es ? 350 kg / m3 - b) acier','kg',8289.410,900.00,'Fondations / Soubassement',3),
 ('SUPERSTRUCTURE','SUPERSTRUCTURE','Poteaux en BA dos? ? 350 kg / m3 - a) b?ton','m?',6.740,90000.00,'RDC',1),
 ('SUPERSTRUCTURE','SUPERSTRUCTURE','Plancher en corps creux hourdis 16 + 4','m?',310.000,35000.00,'1er ?tage',2),
 ('APPAREILS SANITAIRES','PLOMBERIE','Lavabo + Robinetterie','u',4.000,105000.00,'Tous niveaux',1),
 ('APPAREILS SANITAIRES','PLOMBERIE','WC','u',4.000,110000.00,'Tous niveaux',2),
 ('DISTRIBUTION SECONDAIRE','?LECTRICIT?','1 Point lumineux sur simple allumage','u',60.000,6500.00,'Tous niveaux',1),
 ('DISTRIBUTION SECONDAIRE','?LECTRICIT?','Prises de courant 2P+T 10/16A','u',60.000,25000.00,'Tous niveaux',2)
) AS v(poste, sous_ensemble, designation, unite, quantite, pu, niveau, ordre) ON v.poste = po.nom_poste
WHERE p.nom = 'IMMEUBLE R+5 AMOKA'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id AND x.ordre = v.ordre);

--changeset syao:2026-06-01-btp-init-lignes-triplex context:btp-init labels:btp-init
INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  ('CONCEPTION ET ETUDE D''EXECUTION', 'Frais conception architecturale', 'ens', 1.0000, 250000.00, 250000.00, '?tudes', 1),
  ('CONCEPTION ET ETUDE D''EXECUTION', 'Frais etudes d''ingenierie', 'ens', 1.0000, 250000.00, 250000.00, '?tudes', 2),
  ('INSTALLATION DU CHANTIER', 'Installation du chantier(1 baraques 3 compartiments et citerne 2 m3)', 'fft', 1.0000, 650000.00, 650000.00, '?tudes', 3),
  ('INSTALLATION DU CHANTIER', 'Repliement du chantier et evacuation des gravats', 'fft', 1.0000, 550000.00, 550000.00, '?tudes', 4)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'ETUDES ET INSTALLATION'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  ('A) TERRASSEMENT', 'Implantation de l''ouvrage', 'ens', 1.0000, 200000.00, 200000.00, 'Fondations', 1),
  ('A) TERRASSEMENT', 'Fouilles en rigole', 'ml', 44.2000, 1500.00, 66300.00, 'Fondations', 2),
  ('A) TERRASSEMENT', 'Fouilles semelles isolées', 'm3', 54.1035, 3000.00, 162310.50, 'Fondations', 3),
  ('A) TERRASSEMENT', 'Remblai des fouilles', 'm3', 91.0790, 2500.00, 227697.50, 'Fondations', 4),
  ('A) TERRASSEMENT', 'Remblai sous dallage', 'm3', 55.3260, 4000.00, 221304.00, 'Fondations', 5),
  ('B) FONDATION', 'béton de propreté dosé à 150 kg /m3', 'm3', 2.7445, 70000.00, 192115.00, 'Fondations', 6),
  ('semelles filantes dosées à 350 kg / m3', 'a) béton', 'm3', 4.4800, 90000.00, 403200.00, 'Fondations', 7),
  ('semelles filantes dosées à 350 kg / m3', 'b) acier', 'kg', 313.6000, 850.00, 266560.00, 'Fondations', 8),
  ('semelles isolées dosées à 350 kg / m3', 'a) béton', 'm3', 6.1485, 90000.00, 553365.00, 'Fondations', 9),
  ('semelles isolées dosées à 350 kg / m3', 'b) acier', 'kg', 553.3650, 850.00, 470360.25, 'Fondations', 10),
  ('longrines dosées à 350 kg / m3', 'a) béton', 'm3', 1.7109, 90000.00, 153981.00, 'Fondations', 11),
  ('longrines dosées à 350 kg / m3', 'b) acier', 'kg', 153.9810, 850.00, 130883.85, 'Fondations', 12),
  ('longrines dosées à 350 kg / m3', 'c) coffrage', 'm²', 22.8120, 3600.00, 82123.20, 'Fondations', 13),
  ('Amorce de poteaux en BA', 'a) béton', 'm3', 1.0763, 90000.00, 96862.50, 'Fondations', 14),
  ('Amorce de poteaux en BA', 'b) acier', 'kg', 96.8625, 850.00, 82333.13, 'Fondations', 15),
  ('Amorce de poteaux en BA', 'c) coffrage', 'm²', 19.8000, 3600.00, 71280.00, 'Fondations', 16),
  ('Amorce de raidisseurs en BA', 'a) béton', 'm3', 0.3443, 90000.00, 30982.50, 'Fondations', 17),
  ('Amorce de raidisseurs en BA', 'b) acier', 'kg', 24.0975, 850.00, 20482.88, 'Fondations', 18),
  ('Amorce de raidisseurs en BA', 'c) coffrage', 'm²', 8.7000, 3600.00, 31320.00, 'Fondations', 19),
  ('bêches dosées à 350 kg / m3', 'a) béton', 'm3', 0.3731, 90000.00, 33581.25, 'Fondations', 20),
  ('bêches dosées à 350 kg / m3', 'b) acier', 'kg', 26.1188, 850.00, 22200.94, 'Fondations', 21),
  ('bêches dosées à 350 kg / m3', 'agglos pleins de 15 cm de soubassement', 'm²', 35.3600, 9500.00, 335920.00, 'Fondations', 22),
  ('Amorce de poteaux en BA', 'a) béton', 'm3', 1.0763, 90000.00, 96862.50, 'Fondations', 23),
  ('Amorce de poteaux en BA', 'b) acier', 'kg', 96.8625, 850.00, 82333.13, 'Fondations', 24),
  ('Amorce de poteaux en BA', 'c) coffrage', 'm²', 19.8000, 3600.00, 71280.00, 'Fondations', 25),
  ('Voile escalier en BA dosé à 350 kg / m3', 'a) béton', 'm3', 0.0900, 90000.00, 8100.00, 'Fondations', 26),
  ('Voile escalier en BA dosé à 350 kg / m3', 'b) acier', 'kg', 8.1000, 850.00, 6885.00, 'Fondations', 27),
  ('Voile escalier en BA dosé à 350 kg / m3', 'c) coffrage', 'm²', 1.6200, 3600.00, 5832.00, 'Fondations', 28),
  ('chainage bas en BA dosé à 350 kg/m2', 'a) béton', 'm3', 1.3260, 90000.00, 119340.00, 'Fondations', 29),
  ('chainage bas en BA dosé à 350 kg/m2', 'b) acier', 'kg', 1286.2200, 850.00, 1093287.00, 'Fondations', 30),
  ('chainage bas en BA dosé à 350 kg/m2', 'c) coffrage', 'm²', 17.6800, 3600.00, 63648.00, 'Fondations', 31),
  ('Dallage en béton armé avec treillis soudé epaisseur 10 cm dosé à 250 kg/m2', 'a) béton', 'm3', 9.2210, 90000.00, 829890.00, 'Fondations', 32),
  ('Dallage en béton armé avec treillis soudé epaisseur 10 cm dosé à 250 kg/m2', 'b) acier', 'kg', 368.8400, 850.00, 313514.00, 'Fondations', 33),
  ('Dallage en béton armé avec treillis soudé epaisseur 10 cm dosé à 250 kg/m2', 'c) film polyane', 'm²', 92.2100, 500.00, 46105.00, 'Fondations', 34)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'INFRASTRUCTURE'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  (NULL, 'maçonnerie en agglos de 15 creux', 'm²', 177.0000, 8500.00, 1504500.00, 'RDC', 1),
  (NULL, 'maçonnerie en agglos de 10 creux', 'm²', 35.0000, 7500.00, 262500.00, 'RDC', 2),
  ('Poteaux et raidisseurs en BA', 'a)béton', 'm3', 2.2960, 90000.00, 206640.00, 'RDC', 3),
  ('Poteaux et raidisseurs en BA', 'b)acier', 'kg', 206.6400, 850.00, 175644.00, 'RDC', 4),
  ('Poteaux et raidisseurs en BA', 'c)coffrage', 'm²', 42.2400, 3600.00, 152064.00, 'RDC', 5),
  ('chainage haut en BA dosé à 350 kg/m2', 'a)béton', 'm3', 1.8938, 90000.00, 170442.00, 'RDC', 6),
  ('chainage haut en BA dosé à 350 kg/m2', 'b)acier', 'kg', 170.4420, 850.00, 144875.70, 'RDC', 7),
  ('chainage haut en BA dosé à 350 kg/m2', 'c)coffrage', 'm²', 7.7000, 3600.00, 27720.00, 'RDC', 8),
  ('divers élements en BA dosé à 350 kg/m2', 'a)béton', 'm3', 0.8900, 90000.00, 80100.00, 'RDC', 9),
  ('divers élements en BA dosé à 350 kg/m2', 'b)acier', 'kg', 80.1000, 850.00, 68085.00, 'RDC', 10),
  ('divers élements en BA dosé à 350 kg/m2', 'c)coffrage', 'm²', 16.0200, 3600.00, 57672.00, 'RDC', 11),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'a)béton', 'm3', 6.6429, 90000.00, 597861.00, 'RDC', 12),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'b)acier', 'kg', 597.8610, 850.00, 508181.85, 'RDC', 13),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'c)coffrage', 'm²', 70.1195, 3600.00, 252430.20, 'RDC', 14),
  ('Escalier en Béton Armé BA dosé à 350 kg / m3', 'a)béton', 'm3', 2.4200, 90000.00, 217800.00, 'RDC', 15),
  ('Escalier en Béton Armé BA dosé à 350 kg / m3', 'b)acier', 'kg', 217.8000, 850.00, 185130.00, 'RDC', 16),
  ('Escalier en Béton Armé BA dosé à 350 kg / m3', 'c)coffrage', 'm²', 47.0000, 3600.00, 169200.00, 'RDC', 17),
  ('Plancher en corps creux en hourdis 16+4', 'Plancher en corps creux en hourdis 16 + 4', 'm²', 117.0000, 34500.00, 4036500.00, 'RDC', 18),
  ('staff lisse avec corniche', 'Localisation : toutes pieces de l''appartement et escalier', 'm²', 92.2100, 8900.00, 820669.00, 'RDC', 19),
  ('ENDUITS', 'Lissage au platre des murs interieurs', 'm²', 279.0000, 3000.00, 837000.00, 'RDC', 20),
  ('ENDUITS', 'enduit ciment sur murs extérieurs et interieurs', 'm²', 424.0000, 2300.00, 975200.00, 'RDC', 21),
  ('ENDUITS', 'enduit ciment en sous face plancher', 'm²', 123.5100, 2300.00, 284073.00, 'RDC', 22),
  ('ENDUITS', 'enduit ciment sur divers ouvrages', 'm²', 36.0000, 2300.00, 82800.00, 'RDC', 23)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'ELEVATION RDC'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  (NULL, 'maçonnerie en agglos de 15 creux', 'm²', 229.0000, 8500.00, 1946500.00, '?tage 1', 1),
  (NULL, 'maçonnerie en agglos de 10 creux', 'm²', 35.0000, 7500.00, 262500.00, '?tage 1', 2),
  ('Poteaux et raidisseurs en BA', 'a)béton', 'm3', 2.2960, 90000.00, 206640.00, '?tage 1', 3),
  ('Poteaux et raidisseurs en BA', 'b)acier', 'kg', 206.6400, 850.00, 175644.00, '?tage 1', 4),
  ('Poteaux et raidisseurs en BA', 'c)coffrage', 'm²', 42.2400, 3600.00, 152064.00, '?tage 1', 5),
  ('chainage haut en BA dosé à 350 kg/m2', 'a)béton', 'm3', 2.9400, 90000.00, 264600.00, '?tage 1', 6),
  ('chainage haut en BA dosé à 350 kg/m2', 'b)acier', 'kg', 235.2000, 850.00, 199920.00, '?tage 1', 7),
  ('chainage haut en BA dosé à 350 kg/m2', 'c)coffrage', 'm²', 44.1000, 3600.00, 158760.00, '?tage 1', 8),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'a)béton', 'm3', 6.6429, 90000.00, 597861.00, '?tage 1', 9),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'b)acier', 'kg', 597.8610, 850.00, 508181.85, '?tage 1', 10),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'c)coffrage', 'm²', 70.1195, 3600.00, 252430.20, '?tage 1', 11),
  ('Escalier en Béton Armé BA dosé à 350 kg / m3', 'a)béton', 'm3', 2.4200, 90000.00, 217800.00, '?tage 1', 12),
  ('Escalier en Béton Armé BA dosé à 350 kg / m3', 'b)acier', 'kg', 217.8000, 850.00, 185130.00, '?tage 1', 13),
  ('Escalier en Béton Armé BA dosé à 350 kg / m3', 'c)coffrage', 'm²', 47.0000, 3600.00, 169200.00, '?tage 1', 14),
  ('Plancher en corps creux en hourdis 16+4', 'Plancher en corps creux en hourdis 16 + 4', 'm²', 120.0000, 34500.00, 4140000.00, '?tage 1', 15),
  ('Etanchéité dans les salles humides et balcons', 'Afrikote+bitume+chappe 40', 'm²', 24.7900, 9500.00, 235505.00, '?tage 1', 16),
  ('staff lisse avec corniche', 'Localisation : toutes pieces de l''appartement et escalier', 'm²', 102.3000, 8900.00, 910470.00, '?tage 1', 17),
  ('ENDUITS', 'Lissage au platre des murs interieurs', 'm²', 382.0000, 3000.00, 1146000.00, '?tage 1', 18),
  ('ENDUITS', 'enduit ciment sur murs extérieurs et interieurs', 'm²', 528.0000, 2300.00, 1214400.00, '?tage 1', 19),
  ('ENDUITS', 'enduit ciment en sous face plancher', 'm²', 126.5100, 2300.00, 290973.00, '?tage 1', 20),
  ('ENDUITS', 'enduit ciment sur divers ouvrages', 'm²', 42.0000, 2300.00, 96600.00, '?tage 1', 21)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'ELEVATION ETAGE 1'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  (NULL, 'maçonnerie en agglos de 15 creux', 'm²', 116.0000, 8500.00, 986000.00, '?tage 2', 1),
  (NULL, 'maçonnerie en agglos de 10 creux', 'm²', 10.0000, 7500.00, 75000.00, '?tage 2', 2),
  ('Poteaux et raidisseurs en BA', 'a)béton', 'm3', 2.2960, 90000.00, 206640.00, '?tage 2', 3),
  ('Poteaux et raidisseurs en BA', 'b)acier', 'kg', 206.6400, 850.00, 175644.00, '?tage 2', 4),
  ('Poteaux et raidisseurs en BA', 'c)coffrage', 'm²', 42.2400, 3600.00, 152064.00, '?tage 2', 5),
  ('chainage haut en BA dosé à 350 kg/m2', 'a)béton', 'm3', 0.9000, 90000.00, 81000.00, '?tage 2', 6),
  ('chainage haut en BA dosé à 350 kg/m2', 'b)acier', 'kg', 72.0000, 850.00, 61200.00, '?tage 2', 7),
  ('chainage haut en BA dosé à 350 kg/m2', 'c)coffrage', 'm²', 13.5000, 3600.00, 48600.00, '?tage 2', 8),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'a)béton', 'm3', 4.3263, 90000.00, 389367.00, '?tage 2', 9),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'b)acier', 'kg', 389.3670, 850.00, 330961.95, '?tage 2', 10),
  ('Poutre en Béton Armé BA dosée à 350 kg / m3', 'c)coffrage', 'm²', 49.8180, 3600.00, 179344.80, '?tage 2', 11),
  ('Plancher en corps creux en hourdis 10 + 5', '10+5 pour toiture dalle', 'm²', 70.0000, 30000.00, 2100000.00, '?tage 2', 12),
  ('Etanchéité sur tout l''etage', 'Afrikote+bitume+chappe 40', 'm²', 82.4400, 9500.00, 783180.00, '?tage 2', 13),
  ('Etanchéité sur tout l''etage', 'Afrikote+bitume+paradième', 'm²', 18.7700, 12500.00, 234625.00, '?tage 2', 14),
  ('staff lisse avec corniche', 'Localisation : toutes pieces de l''appartement et escalier', 'm²', 70.0000, 8900.00, 623000.00, '?tage 2', 15),
  ('ENDUITS', 'Lissage au panticoat des murs interieurs', 'm²', 63.0000, 3000.00, 189000.00, '?tage 2', 16),
  ('ENDUITS', 'enduit ciment sur murs extérieurs et interieurs', 'm²', 252.0000, 2300.00, 579600.00, '?tage 2', 17),
  ('ENDUITS', 'enduit ciment en sous face plancher', 'm²', 70.0000, 2300.00, 161000.00, '?tage 2', 18),
  ('ENDUITS', 'enduit ciment sur divers ouvrages', 'm²', 18.0000, 2300.00, 41400.00, '?tage 2', 19)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'ELEVATION ETAGE 2'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  ('Canalisation en PPR', 'diam. 14', 'ml', 30.0000, 3000.00, 90000.00, 'Tous niveaux', 1),
  ('Canalisation en PPR', 'diam. 16', 'ml', 80.0000, 3800.00, 304000.00, 'Tous niveaux', 2),
  ('Canalisation en PPR', 'diam. 25', 'ml', 30.0000, 7300.00, 219000.00, 'Tous niveaux', 3),
  ('Canalisation en PPR', 'Vanne d''arret 15/21', 'u', 8.0000, 8300.00, 66400.00, 'Tous niveaux', 4),
  ('Canalisation PVC, EU-EV', 'PVC diam. 40', 'ml', 30.0000, 1500.00, 45000.00, 'Tous niveaux', 5),
  ('Canalisation PVC, EU-EV', 'PVC diam. 75', 'ml', 45.0000, 2500.00, 112500.00, 'Tous niveaux', 6),
  ('Canalisation PVC, EU-EV', 'PVC diam. 110', 'ml', 90.0000, 3500.00, 315000.00, 'Tous niveaux', 7),
  ('Canalisation PVC, EU-EV', 'PVC diam. 125', 'ml', 90.0000, 5500.00, 495000.00, 'Tous niveaux', 8),
  ('Canalisation PVC, EU-EV', 'PVC diam. 160', 'ml', 45.0000, 7500.00, 337500.00, 'Tous niveaux', 9),
  ('APPAREILS SANITAIRES', 'Evier de cuisine', 'u', 1.0000, 110000.00, 110000.00, 'Tous niveaux', 10),
  ('APPAREILS SANITAIRES', 'Lavabo + Robinetterie', 'u', 6.0000, 75000.00, 450000.00, 'Tous niveaux', 11),
  ('APPAREILS SANITAIRES', 'WC', 'u', 6.0000, 95000.00, 570000.00, 'Tous niveaux', 12),
  ('APPAREILS SANITAIRES', 'Colombe de douche', 'u', 4.0000, 65000.00, 260000.00, 'Tous niveaux', 13),
  ('APPAREILS SANITAIRES', 'Siphon de sol metallique', 'u', 10.0000, 7500.00, 75000.00, 'Tous niveaux', 14),
  ('ACCESSOIRES', 'Porte savon en plastique', 'u', 4.0000, 5000.00, 20000.00, 'Tous niveaux', 15),
  ('ACCESSOIRES', 'Dévidoir de papier en plastique', 'u', 4.0000, 6000.00, 24000.00, 'Tous niveaux', 16),
  ('ACCESSOIRES', 'Porte serviette à 2 branches', 'u', 4.0000, 15000.00, 60000.00, 'Tous niveaux', 17),
  ('ACCESSOIRES', 'Tablette de lavabo vitrée', 'u', 6.0000, 16000.00, 96000.00, 'Tous niveaux', 18),
  ('ACCESSOIRES', 'Glace de lavabo', 'u', 4.0000, 35000.00, 140000.00, 'Tous niveaux', 19),
  ('ASSAINISSEMENT', 'Fosse septique + puits perdu (20 Usagers)', 'u', 1.0000, 1000000.00, 1000000.00, 'Tous niveaux', 20),
  ('ASSAINISSEMENT', 'Regard EU/EV 60 x 60 cm²', 'u', 7.0000, 75000.00, 525000.00, 'Tous niveaux', 21)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'PLOMBERIE ET ASSAINISSEMENT'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  ('LIAISON EQUIPOTENTIELLE', 'Barrette de coupure (grand modele)', 'ml', 20.0000, 8000.00, 160000.00, 'Tous niveaux', 1),
  ('LIAISON EQUIPOTENTIELLE', 'Piquet de terre 2 m', 'ml', 1.0000, 15000.00, 15000.00, 'Tous niveaux', 2),
  ('TABLEAUX ELECTRIQUES', 'Boitier de distribution principal', 'u', 3.0000, 125000.00, 375000.00, 'Tous niveaux', 3),
  ('ALIMENTATION ELECTRIQUE INTERIEUR', 'Tube orange diamètre 13', 'ml', 450.0000, 750.00, 337500.00, 'Tous niveaux', 4),
  ('ALIMENTATION ELECTRIQUE INTERIEUR', 'Tube orange diamètre 11', 'ml', 750.0000, 500.00, 375000.00, 'Tous niveaux', 5),
  ('ALIMENTATION ELECTRIQUE INTERIEUR', 'Câble VGV 3 x 1,5', 'ml', 350.0000, 1500.00, 525000.00, 'Tous niveaux', 6),
  ('ALIMENTATION ELECTRIQUE INTERIEUR', 'TH 10 mm²', 'ml', 80.0000, 7050.00, 564000.00, 'Tous niveaux', 7),
  ('ALIMENTATION ELECTRIQUE INTERIEUR', 'TH 2,5 mm²', 'ml', 200.0000, 1050.00, 210000.00, 'Tous niveaux', 8),
  ('ALIMENTATION ELECTRIQUE INTERIEUR', 'TH 1,5 mm²', 'ml', 150.0000, 650.00, 97500.00, 'Tous niveaux', 9),
  ('DISTRIBUTION SECONDAIRE', 'Interrupteur SA', 'u', 10.0000, 1000.00, 10000.00, 'Tous niveaux', 10),
  ('DISTRIBUTION SECONDAIRE', 'Interrupteur SA étanche', 'u', 8.0000, 2000.00, 16000.00, 'Tous niveaux', 11),
  ('DISTRIBUTION SECONDAIRE', 'Interrupteur VV', 'u', 16.0000, 1500.00, 24000.00, 'Tous niveaux', 12),
  ('DISTRIBUTION SECONDAIRE', 'Prises de courant 2P+T 10/16A', 'u', 22.0000, 1500.00, 33000.00, 'Tous niveaux', 13),
  ('DISTRIBUTION SECONDAIRE', 'Prises de courant 2P+T 10/16A étanche', 'u', 8.0000, 2500.00, 20000.00, 'Tous niveaux', 14),
  ('DISTRIBUTION SECONDAIRE', 'Dismatic 20 A', 'u', 6.0000, 25000.00, 150000.00, 'Tous niveaux', 15),
  ('DISTRIBUTION SECONDAIRE', 'DPN de 16 A', 'u', 8.0000, 8000.00, 64000.00, 'Tous niveaux', 16),
  ('DISTRIBUTION SECONDAIRE', 'DPN de 10 A', 'u', 8.0000, 7000.00, 56000.00, 'Tous niveaux', 17),
  ('DISTRIBUTION SECONDAIRE', 'Boite de derivation ref 294', 'u', 3.0000, 7500.00, 22500.00, 'Tous niveaux', 18),
  ('DISTRIBUTION SECONDAIRE', 'Boite de derivation etanche 150/110', 'u', 6.0000, 7000.00, 42000.00, 'Tous niveaux', 19),
  ('DISTRIBUTION SECONDAIRE', 'Boite ronde', 'u', 60.0000, 200.00, 12000.00, 'Tous niveaux', 20),
  ('DISTRIBUTION SECONDAIRE', 'Boite étanche apparant 100/100', 'u', 6.0000, 5000.00, 30000.00, 'Tous niveaux', 21),
  ('APPAREILS D''ECLAIRAGE', 'Douille', 'u', 30.0000, 500.00, 15000.00, 'Tous niveaux', 22)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'ELECTRICITE - CLIMATISATION'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  ('Carreaux au sol', 'Chape ciment', 'm²', 264.5100, 2400.00, 634824.00, 'Tous niveaux', 1),
  ('Carreaux au sol', 'Carreaux grès cérame 60x60 dont 50% dédié à l''achat du carreau', 'm²', 264.5100, 11000.00, 2909610.00, 'Tous niveaux', 2),
  ('Carreaux au mur', 'faience 15x15 sur une hauteur de 1,80 m au dessus de l''evier et toute hauteur pour les salles d''eaux', 'm²', 136.5000, 11000.00, 1501500.00, 'Tous niveaux', 3)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'REVETEMENTS DURS'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  ('Porte alu gris coulissante anodisée 2 vantaux', 'L200 x H 220', 'u', 3.0000, 412500.00, 1237500.00, 'Tous niveaux', 1),
  ('Portes bois pleines y/c serrures et vernis', '100 x 220', 'u', 3.0000, 145000.00, 435000.00, 'Tous niveaux', 2),
  ('Portes bois pleines y/c serrures et vernis', '90 x 220', 'u', 5.0000, 135000.00, 675000.00, 'Tous niveaux', 3),
  ('Portes bois pleines y/c serrures et vernis', '70 x 220', 'u', 7.0000, 125000.00, 875000.00, 'Tous niveaux', 4),
  ('Fenêtres alu gris coulissante anodisée 2 vantaux', 'L150 x H 110', 'u', 2.0000, 123750.00, 247500.00, 'Tous niveaux', 5),
  ('Fenêtres alu gris coulissante anodisée 2 vantaux', 'L 120 x H 110', 'u', 5.0000, 99000.00, 495000.00, 'Tous niveaux', 6),
  ('Fenêtres alu gris coulissante anodisée 2 vantaux', 'L 80 x H 60', 'u', 6.0000, 60000.00, 360000.00, 'Tous niveaux', 7),
  ('Garde corp en inox avec vitrage triplex 10 mm', 'L 1210 x H 80 cm²', 'u', 1.0000, 1258400.00, 1258400.00, 'Tous niveaux', 8)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'MENUISERIE BOIS - ALUMINIUM'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

INSERT INTO immobilier.btp_ligne_devis (id, poste_id, sous_ensemble, designation, unite, quantite, prix_unitaire, montant_ht, niveau, ordre)
SELECT nextval('immobilier.seq_btp_ligne_devis'), po.id, v.sous_ensemble, v.designation, v.unite, v.quantite, v.pu, v.montant, v.niveau, v.ordre
FROM immobilier.btp_poste_devis po JOIN immobilier.btp_lot_devis l ON l.id = po.lot_id JOIN immobilier.btp_projet p ON p.id = l.projet_id
JOIN (VALUES
  (NULL, 'Peinture pantigré sur murs extérieurs', 'm²', 353.6500, 6500.00, 2298725.00, 'Tous niveaux', 1),
  (NULL, 'Peinture delux sur murs interieurs', 'm²', 724.0000, 1500.00, 1086000.00, 'Tous niveaux', 2),
  (NULL, 'Peinture delux sur staff du plancher', 'm²', 320.0200, 1500.00, 480030.00, 'Tous niveaux', 3)
) AS v(sous_ensemble, designation, unite, quantite, pu, montant, niveau, ordre) ON TRUE
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND po.nom_poste = 'PEINTURE'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_ligne_devis x WHERE x.poste_id = po.id);

--changeset syao:2026-06-01-btp-init-biblio context:btp-init labels:btp-init
INSERT INTO immobilier.refm_btp_tache_biblio (id, code, tache, lot, categorie, unite, rendement, chef, compagnon, manoeuvres, specialiste, couleur_hex)
SELECT nextval('immobilier.seq_refm_btp_tache_biblio'), v.* FROM (VALUES
('PRE-001', 'Installation de chantier', 'Pr?paration', 'Pr?paration', 'ft', 1.00, 1, 0, 2, 0, 'FFD966'),
('TER-001', 'Terrassement g?n?ral (d?capage)', 'Pr?paration', 'Terrassement', 'm?', 4.00, 0, 1, 3, 0, 'FFD966'),
('BET-001', 'B?ton de propret?', 'Fondations', 'B?ton', 'm?', 3.00, 0, 1, 2, 0, 'A9D08E'),
('FER-001', 'Ferraillage fondations', 'Fondations', 'Ferraillage', 'kg', 80.00, 0, 2, 1, 0, 'A9D08E'),
('MAC-001', '?l?vation murs soubassement', 'Gros ?uvre', 'Ma?onnerie', 'm?', 2.50, 0, 2, 2, 0, '9DC3E6'),
('BET-005', 'Dalle RDC (coffrage + ferraillage + b?ton)', 'Gros ?uvre', 'B?ton', 'm?', 1.20, 1, 3, 3, 0, '9DC3E6'),
('PEI-001', 'Peinture int?rieure', 'Finitions', 'Peinture', 'm?', 4.00, 0, 1, 1, 0, 'F8CBAD')
) AS v(code, tache, lot, categorie, unite, rendement, chef, compagnon, manoeuvres, specialiste, couleur_hex)
ON CONFLICT (code) DO NOTHING;

--changeset syao:2026-06-01-btp-init-soustraitants context:btp-init labels:btp-init
INSERT INTO immobilier.btp_soustraitant (id, projet_id, nom, metier, lot, marche_montant)
SELECT nextval('immobilier.seq_btp_soustraitant'), p.id, v.nom, v.metier, v.lot, v.montant
FROM immobilier.btp_projet p CROSS JOIN (VALUES
('ST_01', 'Ma?on', 'GO', 29732403.00), ('ST_02', 'Menuisier coffreur', 'GO', 15847373.00),
('ST_03', 'Ferrailleur', 'GO', 12385920.00), ('ST_06', 'Carreleur', 'SO', 9216355.00),
('ST_07', 'Plombier', 'SO', 15270000.00), ('ST_14', '?lectricien', 'SO', 13203300.00)
) AS v(nom, metier, lot, montant)
WHERE p.nom = 'IMMEUBLE R+5 AMOKA'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_soustraitant x WHERE x.projet_id = p.id AND x.nom = v.nom);

--changeset syao:2026-06-01-btp-init-situation-triplex context:btp-init labels:btp-init
INSERT INTO immobilier.btp_situation_travaux (id, projet_id, numero, mois, annee, date_situation, montant_marche_ht, montant_cumule_ht,
    montant_precedent_ht, montant_periode_ht, remise_cumul, remise_precedent, remise_periode, montant_apres_remise, avancement_global_pct, statut)
SELECT nextval('immobilier.seq_btp_situation_travaux'), p.id, 1, 7, 2025, DATE '2025-07-28', 63800122.68, 61430122.68, 29292164.93, 32137957.75, 2888558.21, 1377371.88, 1511186.33, 58541564.47, 0.9629, 'VALIDEE'
FROM immobilier.btp_projet p
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES'
  AND NOT EXISTS (SELECT 1 FROM immobilier.btp_situation_travaux x WHERE x.projet_id = p.id AND x.numero = 1);

--changeset syao:2026-06-01-btp-init-situation-lignes-triplex context:btp-init labels:btp-init
INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'ETUDES ET INSTALLATION'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 250000.00, 1.0000, 250000.00, 0.0000, 0.00),
  (2, 1.0000, 250000.00, 1.0000, 250000.00, 0.0000, 0.00),
  (3, 1.0000, 650000.00, 1.0000, 650000.00, 0.0000, 0.00),
  (4, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'INFRASTRUCTURE'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 200000.00, 1.0000, 200000.00, 0.0000, 0.00),
  (2, 1.0000, 66300.00, 1.0000, 66300.00, 0.0000, 0.00),
  (3, 1.0000, 162310.50, 1.0000, 162310.50, 0.0000, 0.00),
  (4, 1.0000, 227697.50, 1.0000, 227697.50, 0.0000, 0.00),
  (5, 1.0000, 221304.00, 1.0000, 221304.00, 0.0000, 0.00),
  (6, 1.0000, 192115.00, 1.0000, 192115.00, 0.0000, 0.00),
  (7, 1.0000, 403200.00, 1.0000, 403200.00, 0.0000, 0.00),
  (8, 1.0000, 266560.00, 1.0000, 266560.00, 0.0000, 0.00),
  (9, 1.0000, 553365.00, 1.0000, 553365.00, 0.0000, 0.00),
  (10, 1.0000, 470360.25, 1.0000, 470360.25, 0.0000, 0.00),
  (11, 1.0000, 153981.00, 1.0000, 153981.00, 0.0000, 0.00),
  (12, 1.0000, 130883.85, 1.0000, 130883.85, 0.0000, 0.00),
  (13, 1.0000, 82123.20, 1.0000, 82123.20, 0.0000, 0.00),
  (14, 1.0000, 96862.50, 1.0000, 96862.50, 0.0000, 0.00),
  (15, 1.0000, 82333.13, 1.0000, 82333.13, 0.0000, 0.00),
  (16, 1.0000, 71280.00, 1.0000, 71280.00, 0.0000, 0.00),
  (17, 1.0000, 30982.50, 1.0000, 30982.50, 0.0000, 0.00),
  (18, 1.0000, 20482.88, 1.0000, 20482.88, 0.0000, 0.00),
  (19, 1.0000, 31320.00, 1.0000, 31320.00, 0.0000, 0.00),
  (20, 1.0000, 33581.25, 1.0000, 33581.25, 0.0000, 0.00),
  (21, 1.0000, 22200.94, 1.0000, 22200.94, 0.0000, 0.00),
  (22, 1.0000, 335920.00, 1.0000, 335920.00, 0.0000, 0.00),
  (23, 1.0000, 96862.50, 1.0000, 96862.50, 0.0000, 0.00),
  (24, 1.0000, 82333.13, 1.0000, 82333.13, 0.0000, 0.00),
  (25, 1.0000, 71280.00, 1.0000, 71280.00, 0.0000, 0.00),
  (26, 1.0000, 8100.00, 1.0000, 8100.00, 0.0000, 0.00),
  (27, 1.0000, 6885.00, 1.0000, 6885.00, 0.0000, 0.00),
  (28, 1.0000, 5832.00, 1.0000, 5832.00, 0.0000, 0.00),
  (29, 1.0000, 119340.00, 1.0000, 119340.00, 0.0000, 0.00),
  (30, 1.0000, 1093287.00, 1.0000, 1093287.00, 0.0000, 0.00),
  (31, 1.0000, 63648.00, 1.0000, 63648.00, 0.0000, 0.00),
  (32, 1.0000, 829890.00, 1.0000, 829890.00, 0.0000, 0.00),
  (33, 1.0000, 313514.00, 1.0000, 313514.00, 0.0000, 0.00),
  (34, 1.0000, 46105.00, 1.0000, 46105.00, 0.0000, 0.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'ELEVATION RDC'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 1504500.00, 1.0000, 1504500.00, 0.0000, 0.00),
  (2, 1.0000, 262500.00, 1.0000, 262500.00, 0.0000, 0.00),
  (3, 1.0000, 206640.00, 1.0000, 206640.00, 0.0000, 0.00),
  (4, 1.0000, 175644.00, 1.0000, 175644.00, 0.0000, 0.00),
  (5, 1.0000, 152064.00, 1.0000, 152064.00, 0.0000, 0.00),
  (6, 1.0000, 170442.00, 1.0000, 170442.00, 0.0000, 0.00),
  (7, 1.0000, 144875.70, 1.0000, 144875.70, 0.0000, 0.00),
  (8, 1.0000, 27720.00, 1.0000, 27720.00, 0.0000, 0.00),
  (9, 1.0000, 80100.00, 1.0000, 80100.00, 0.0000, 0.00),
  (10, 1.0000, 68085.00, 1.0000, 68085.00, 0.0000, 0.00),
  (11, 1.0000, 57672.00, 1.0000, 57672.00, 0.0000, 0.00),
  (12, 1.0000, 597861.00, 1.0000, 597861.00, 0.0000, 0.00),
  (13, 1.0000, 508181.85, 1.0000, 508181.85, 0.0000, 0.00),
  (14, 1.0000, 252430.20, 1.0000, 252430.20, 0.0000, 0.00),
  (15, 1.0000, 217800.00, 1.0000, 217800.00, 0.0000, 0.00),
  (16, 1.0000, 185130.00, 1.0000, 185130.00, 0.0000, 0.00),
  (17, 1.0000, 169200.00, 1.0000, 169200.00, 0.0000, 0.00),
  (18, 1.0000, 4036500.00, 1.0000, 4036500.00, 0.0000, 0.00),
  (19, 1.0000, 820669.00, 0.0000, 0.00, 1.0000, 820669.00),
  (20, 1.0000, 837000.00, 0.0000, 0.00, 1.0000, 837000.00),
  (21, 1.0000, 975200.00, 0.0000, 0.00, 1.0000, 975200.00),
  (22, 1.0000, 284073.00, 0.0000, 0.00, 1.0000, 284073.00),
  (23, 1.0000, 82800.00, 0.0000, 0.00, 1.0000, 82800.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'ELEVATION ETAGE 1'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 1946500.00, 1.0000, 1946500.00, 0.0000, 0.00),
  (2, 1.0000, 262500.00, 1.0000, 262500.00, 0.0000, 0.00),
  (3, 1.0000, 206640.00, 1.0000, 206640.00, 0.0000, 0.00),
  (4, 1.0000, 175644.00, 1.0000, 175644.00, 0.0000, 0.00),
  (5, 1.0000, 152064.00, 1.0000, 152064.00, 0.0000, 0.00),
  (6, 1.0000, 264600.00, 1.0000, 264600.00, 0.0000, 0.00),
  (7, 1.0000, 199920.00, 1.0000, 199920.00, 0.0000, 0.00),
  (8, 1.0000, 158760.00, 1.0000, 158760.00, 0.0000, 0.00),
  (9, 1.0000, 597861.00, 1.0000, 597861.00, 0.0000, 0.00),
  (10, 1.0000, 508181.85, 1.0000, 508181.85, 0.0000, 0.00),
  (11, 1.0000, 252430.20, 1.0000, 252430.20, 0.0000, 0.00),
  (12, 1.0000, 217800.00, 1.0000, 217800.00, 0.0000, 0.00),
  (13, 1.0000, 185130.00, 1.0000, 185130.00, 0.0000, 0.00),
  (14, 1.0000, 169200.00, 1.0000, 169200.00, 0.0000, 0.00),
  (15, 1.0000, 4140000.00, 1.0000, 4140000.00, 0.0000, 0.00),
  (16, 1.0000, 235505.00, 0.0000, 0.00, 1.0000, 235505.00),
  (17, 1.0000, 910470.00, 0.0000, 0.00, 1.0000, 910470.00),
  (18, 1.0000, 1146000.00, 0.0000, 0.00, 1.0000, 1146000.00),
  (19, 1.0000, 1214400.00, 0.0000, 0.00, 1.0000, 1214400.00),
  (20, 1.0000, 290973.00, 0.0000, 0.00, 1.0000, 290973.00),
  (21, 1.0000, 96600.00, 0.0000, 0.00, 1.0000, 96600.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'ELEVATION ETAGE 2'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 986000.00, 1.0000, 986000.00, 0.0000, 0.00),
  (2, 1.0000, 75000.00, 1.0000, 75000.00, 0.0000, 0.00),
  (3, 1.0000, 206640.00, 1.0000, 206640.00, 0.0000, 0.00),
  (4, 1.0000, 175644.00, 1.0000, 175644.00, 0.0000, 0.00),
  (5, 1.0000, 152064.00, 1.0000, 152064.00, 0.0000, 0.00),
  (6, 1.0000, 81000.00, 0.0000, 0.00, 1.0000, 81000.00),
  (7, 1.0000, 61200.00, 0.0000, 0.00, 1.0000, 61200.00),
  (8, 1.0000, 48600.00, 0.0000, 0.00, 1.0000, 48600.00),
  (9, 1.0000, 389367.00, 0.0000, 0.00, 1.0000, 389367.00),
  (10, 1.0000, 330961.95, 0.0000, 0.00, 1.0000, 330961.95),
  (11, 1.0000, 179344.80, 0.0000, 0.00, 1.0000, 179344.80),
  (12, 1.0000, 2100000.00, 0.0000, 0.00, 1.0000, 2100000.00),
  (13, 1.0000, 783180.00, 0.0000, 0.00, 1.0000, 783180.00),
  (14, 1.0000, 234625.00, 0.0000, 0.00, 1.0000, 234625.00),
  (15, 1.0000, 623000.00, 0.0000, 0.00, 1.0000, 623000.00),
  (16, 1.0000, 189000.00, 0.0000, 0.00, 1.0000, 189000.00),
  (17, 1.0000, 579600.00, 0.0000, 0.00, 1.0000, 579600.00),
  (18, 1.0000, 161000.00, 0.0000, 0.00, 1.0000, 161000.00),
  (19, 1.0000, 41400.00, 0.0000, 0.00, 1.0000, 41400.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'PLOMBERIE ET ASSAINISSEMENT'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 90000.00, 0.0000, 0.00, 1.0000, 90000.00),
  (2, 1.0000, 304000.00, 0.0000, 0.00, 1.0000, 304000.00),
  (3, 1.0000, 219000.00, 0.0000, 0.00, 1.0000, 219000.00),
  (4, 1.0000, 66400.00, 0.0000, 0.00, 1.0000, 66400.00),
  (5, 1.0000, 45000.00, 0.0000, 0.00, 1.0000, 45000.00),
  (6, 1.0000, 112500.00, 0.0000, 0.00, 1.0000, 112500.00),
  (7, 1.0000, 315000.00, 0.0000, 0.00, 1.0000, 315000.00),
  (8, 1.0000, 495000.00, 0.0000, 0.00, 1.0000, 495000.00),
  (9, 1.0000, 337500.00, 0.0000, 0.00, 1.0000, 337500.00),
  (10, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (11, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (12, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (13, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (14, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (15, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (16, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (17, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (18, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (19, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00),
  (20, 1.0000, 1000000.00, 1.0000, 1000000.00, 0.0000, 0.00),
  (21, 1.0000, 525000.00, 1.0000, 525000.00, 0.0000, 0.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'ELECTRICITE - CLIMATISATION'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 160000.00, 1.0000, 160000.00, 0.0000, 0.00),
  (2, 1.0000, 15000.00, 1.0000, 15000.00, 0.0000, 0.00),
  (3, 1.0000, 375000.00, 0.0000, 0.00, 1.0000, 375000.00),
  (4, 1.0000, 337500.00, 0.0000, 0.00, 1.0000, 337500.00),
  (5, 1.0000, 375000.00, 0.0000, 0.00, 1.0000, 375000.00),
  (6, 1.0000, 525000.00, 0.0000, 0.00, 1.0000, 525000.00),
  (7, 1.0000, 564000.00, 0.0000, 0.00, 1.0000, 564000.00),
  (8, 1.0000, 210000.00, 0.0000, 0.00, 1.0000, 210000.00),
  (9, 1.0000, 97500.00, 0.0000, 0.00, 1.0000, 97500.00),
  (10, 1.0000, 10000.00, 0.0000, 0.00, 1.0000, 10000.00),
  (11, 1.0000, 16000.00, 0.0000, 0.00, 1.0000, 16000.00),
  (12, 1.0000, 24000.00, 0.0000, 0.00, 1.0000, 24000.00),
  (13, 1.0000, 33000.00, 0.0000, 0.00, 1.0000, 33000.00),
  (14, 1.0000, 20000.00, 0.0000, 0.00, 1.0000, 20000.00),
  (15, 1.0000, 150000.00, 0.0000, 0.00, 1.0000, 150000.00),
  (16, 1.0000, 64000.00, 0.0000, 0.00, 1.0000, 64000.00),
  (17, 1.0000, 56000.00, 0.0000, 0.00, 1.0000, 56000.00),
  (18, 1.0000, 22500.00, 0.0000, 0.00, 1.0000, 22500.00),
  (19, 1.0000, 42000.00, 0.0000, 0.00, 1.0000, 42000.00),
  (20, 1.0000, 12000.00, 0.0000, 0.00, 1.0000, 12000.00),
  (21, 1.0000, 30000.00, 0.0000, 0.00, 1.0000, 30000.00),
  (22, 0.0000, 0.00, 0.0000, 0.00, 0.0000, 0.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'REVETEMENTS DURS'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 634824.00, 0.0000, 0.00, 1.0000, 634824.00),
  (2, 1.0000, 2909610.00, 0.0000, 0.00, 1.0000, 2909610.00),
  (3, 1.0000, 1501500.00, 0.0000, 0.00, 1.0000, 1501500.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'MENUISERIE BOIS - ALUMINIUM'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 1237500.00, 0.0000, 0.00, 1.0000, 1237500.00),
  (2, 1.0000, 435000.00, 0.0000, 0.00, 1.0000, 435000.00),
  (3, 1.0000, 675000.00, 0.0000, 0.00, 1.0000, 675000.00),
  (4, 1.0000, 875000.00, 0.0000, 0.00, 1.0000, 875000.00),
  (5, 1.0000, 247500.00, 0.0000, 0.00, 1.0000, 247500.00),
  (6, 1.0000, 495000.00, 0.0000, 0.00, 1.0000, 495000.00),
  (7, 1.0000, 360000.00, 0.0000, 0.00, 1.0000, 360000.00),
  (8, 1.0000, 1258400.00, 0.0000, 0.00, 1.0000, 1258400.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;

INSERT INTO immobilier.btp_situation_ligne (id, situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
SELECT nextval('immobilier.seq_btp_situation_ligne'), st.id, li.id, v.c_pct, v.c_mt, v.p_pct, v.p_mt, v.n_pct, v.n_mt
FROM immobilier.btp_situation_travaux st JOIN immobilier.btp_projet p ON p.id = st.projet_id
JOIN immobilier.btp_lot_devis l ON l.projet_id = p.id JOIN immobilier.btp_poste_devis po ON po.lot_id = l.id AND po.nom_poste = 'PEINTURE'
JOIN immobilier.btp_ligne_devis li ON li.poste_id = po.id
JOIN (VALUES
  (1, 1.0000, 2298725.00, 0.0000, 0.00, 1.0000, 2298725.00),
  (2, 1.0000, 1086000.00, 0.0000, 0.00, 1.0000, 1086000.00),
  (3, 1.0000, 480030.00, 0.0000, 0.00, 1.0000, 480030.00)
) AS v(ordre, c_pct, c_mt, p_pct, p_mt, n_pct, n_mt) ON v.ordre = li.ordre
WHERE p.nom = 'TRIPLEX SIMEON 6 PI?CES' AND st.numero = 1
ON CONFLICT (situation_id, ligne_devis_id) DO NOTHING;
