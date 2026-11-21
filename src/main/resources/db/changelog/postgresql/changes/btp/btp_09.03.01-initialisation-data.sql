--liquibase formatted sql

--changeset syao:2026-11-21-btp-10-data-referentiel context:btp-init labels:btp-init
INSERT INTO immobilier.refm_btp_lot (code, libelle, description) VALUES
('GO',  'Gros Œuvre',            'Études, installation, infrastructure, élévation et étanchéité'),
('SO',  'Second Œuvre',          'Plomberie, électricité, revêtements, menuiserie et peinture'),
('VRD', 'Aménagement Extérieur', 'Dallage parking, clôture et jardinage');

INSERT INTO immobilier.refm_btp_poste (lot_id, code, libelle)
SELECT id, 'ETUDES_INST', 'Études et Installation de Chantier' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'INFRA_TERR',  'Infrastructure - A) Terrassement' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'INFRA_FOND',  'Infrastructure - B) Fondation' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'RDC_SUPER',   'Élévation RDC - Superstructure' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'RDC_FAUX_PL', 'Élévation RDC - Faux-plafond' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'RDC_ENDUITS', 'Élévation RDC - Enduits' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'E1_SUPER',    'Élévation Étage 1 - Superstructure' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'E1_ETANCH',   'Élévation Étage 1 - Étanchéité' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'E1_FAUX_PL',  'Élévation Étage 1 - Faux-plafond' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'E1_ENDUITS',  'Élévation Étage 1 - Enduits' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'E2_SUPER',    'Élévation Étage 2 - Superstructure' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'E2_ETANCH',   'Élévation Étage 2 - Étanchéité' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'E2_FAUX_PL',  'Élévation Étage 2 - Faux-plafond' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'E2_ENDUITS',  'Élévation Étage 2 - Enduits' FROM immobilier.refm_btp_lot WHERE code = 'GO'
UNION ALL SELECT id, 'PLOMBERIE',   'Plomberie - Assainissement' FROM immobilier.refm_btp_lot WHERE code = 'SO'
UNION ALL SELECT id, 'ELECTRICITE', 'Électricité et Climatisation' FROM immobilier.refm_btp_lot WHERE code = 'SO'
UNION ALL SELECT id, 'REVETEMENTS', 'Revêtements Durs' FROM immobilier.refm_btp_lot WHERE code = 'SO'
UNION ALL SELECT id, 'MENUISERIE',  'Menuiserie Bois - Menuiserie Aluminium' FROM immobilier.refm_btp_lot WHERE code = 'SO'
UNION ALL SELECT id, 'PEINTURE',    'Peinture' FROM immobilier.refm_btp_lot WHERE code = 'SO'
UNION ALL SELECT id, 'PARKING',     'Dallage parking' FROM immobilier.refm_btp_lot WHERE code = 'VRD'
UNION ALL SELECT id, 'CLOTURE',     'Clôture' FROM immobilier.refm_btp_lot WHERE code = 'VRD'
UNION ALL SELECT id, 'JARDINAGE',   'Jardinage' FROM immobilier.refm_btp_lot WHERE code = 'VRD';

INSERT INTO immobilier.refm_btp_tache (poste_id, code, libelle, unite_defaut)
SELECT id, 'TERRASSEMENT',  'Terrassement', 'm3' FROM immobilier.refm_btp_poste WHERE code = 'INFRA_TERR'
UNION ALL SELECT id, 'FONDATION',     'Fondation', 'ens' FROM immobilier.refm_btp_poste WHERE code = 'INFRA_FOND'
UNION ALL SELECT id, 'CARRELAGE_SOL', 'Carrelage au sol', 'm2' FROM immobilier.refm_btp_poste WHERE code = 'REVETEMENTS'
UNION ALL SELECT id, 'CARRELAGE_MUR', 'Carreaux au mur', 'm2' FROM immobilier.refm_btp_poste WHERE code = 'REVETEMENTS'
UNION ALL SELECT id, 'INST_EAU',      'Installation eau', 'ml' FROM immobilier.refm_btp_poste WHERE code = 'PLOMBERIE'
UNION ALL SELECT id, 'RES_EVAC',      'Réseaux d''évacuation', 'ml' FROM immobilier.refm_btp_poste WHERE code = 'PLOMBERIE';

INSERT INTO immobilier.refm_btp_sous_tache (tache_id, code, libelle, unite_defaut)
SELECT id, 'CHAPE_CIMENT', 'Chape ciment', 'm2' FROM immobilier.refm_btp_tache WHERE code = 'CARRELAGE_SOL'
UNION ALL SELECT id, 'GRES_CERAME',  'Carreaux grès cérame 60x60', 'm2' FROM immobilier.refm_btp_tache WHERE code = 'CARRELAGE_SOL';

--changeset syao:2026-11-21-btp-11-data-projet-devis context:btp-init labels:btp-init
INSERT INTO immobilier.btp_projet (
    code_projet, nom_projet, client_nom, localisation,
    date_debut, date_fin_prevue, budget_prevu_ht, budget_prevu_ttc, devise, statut
) VALUES (
    'PRJ-BING-2026-02',
    'Construction Villa Triplex 6 Pièces (Triplex N°2)',
    'Monsieur YAO KONAN SIMEON',
    'Bingerville, Côte d''Ivoire',
    DATE '2026-10-15',
    DATE '2027-06-30',
    60788243.00,
    60788243.00,
    'FCFA',
    'EN_COURS'
);

INSERT INTO immobilier.btp_devis (
    projet_id, numero_devis, date_devis,
    montant_ht, taux_tva, montant_ttc, statut, notes
)
SELECT
    id,
    'YKSBat0201-07102026',
    DATE '2026-10-07',
    60788243.00,
    0.00,
    60788243.00,
    'VALIDE',
    'Devis quantitatif et estimatif Triplex N°2 (GO: 40 249 364 / SO: 18 262 879 / VRD: 2 276 000).'
FROM immobilier.btp_projet
WHERE code_projet = 'PRJ-BING-2026-02';

--changeset syao:2026-11-21-btp-12-data-lignes-devis context:btp-init labels:btp-init
INSERT INTO immobilier.btp_ligne_devis (
    devis_id, lot_id, poste_id, tache_id, sous_tache_id,
    ordre_affichage, designation, unite, quantite, prix_unitaire_ht, montant_ht
)
SELECT
    d.id,
    l.id,
    p.id,
    t.id,
    st.id,
    v.ordre_affichage,
    v.designation,
    v.unite,
    v.quantite,
    v.prix_unitaire_ht,
    ROUND(v.quantite * v.prix_unitaire_ht, 2)
FROM immobilier.btp_devis d
JOIN (VALUES
    -- 1. ETUDES ET INSTALLATION (GO)
    (1,   'GO',  'ETUDES_INST', NULL, NULL, 'Frais conception architecturale', 'ens', 0.000, 0.00),
    (2,   'GO',  'ETUDES_INST', NULL, NULL, 'Frais études d''ingénierie', 'ens', 0.000, 0.00),
    (3,   'GO',  'ETUDES_INST', NULL, NULL, 'Installation du chantier (1 baraque 2 compartiments)', 'fft', 1.000, 400000.00),
    (4,   'GO',  'ETUDES_INST', NULL, NULL, 'Repliement du chantier et évacuation des gravats', 'fft', 1.000, 550000.00),

    -- 2. INFRASTRUCTURE - A) TERRASSEMENT (GO)
    (5,   'GO',  'INFRA_TERR', 'TERRASSEMENT', NULL, 'Implantation de l''ouvrage', 'ens', 1.000, 200000.00),
    (6,   'GO',  'INFRA_TERR', 'TERRASSEMENT', NULL, 'Fouilles en rigole', 'ml', 45.200, 1500.00),
    (7,   'GO',  'INFRA_TERR', 'TERRASSEMENT', NULL, 'Fouilles semelles isolées', 'm3', 54.100, 3000.00),
    (8,   'GO',  'INFRA_TERR', 'TERRASSEMENT', NULL, 'Remblai des fouilles', 'm3', 93.760, 2500.00),
    (9,   'GO',  'INFRA_TERR', 'TERRASSEMENT', NULL, 'Remblai sous dallage', 'm3', 55.330, 4000.00),

    -- 3. INFRASTRUCTURE - B) FONDATION (GO)
    (10,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Béton de propreté dosé à 150 kg/m3', 'm3', 5.540, 70000.00),
    (11,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Semelles filantes dosées à 350 kg/m3 - a) béton', 'm3', 0.000, 90000.00),
    (12,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Semelles filantes dosées à 350 kg/m3 - b) acier', 'kg', 0.000, 850.00),
    (13,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Semelles isolées dosées à 350 kg/m3 - a) béton', 'm3', 6.150, 90000.00),
    (14,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Semelles isolées dosées à 350 kg/m3 - b) acier', 'kg', 553.000, 850.00),
    (15,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Longrines dosées à 350 kg/m3 - a) béton', 'm3', 1.710, 90000.00),
    (16,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Longrines dosées à 350 kg/m3 - b) acier', 'kg', 154.000, 850.00),
    (17,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Longrines dosées à 350 kg/m3 - c) coffrage', 'm2', 22.810, 3600.00),
    (18,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Amorce de poteaux en BA - a) béton', 'm3', 1.080, 90000.00),
    (19,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Amorce de poteaux en BA - b) acier', 'kg', 129.150, 850.00),
    (20,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Amorce de poteaux en BA - c) coffrage', 'm2', 19.800, 3600.00),
    (21,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Amorce de raidisseurs en BA - a) béton', 'm3', 0.340, 90000.00),
    (22,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Amorce de raidisseurs en BA - b) acier', 'kg', 24.100, 850.00),
    (23,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Amorce de raidisseurs en BA - c) coffrage', 'm2', 8.700, 3600.00),
    (24,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Bêches dosées à 350 kg/m3 - a) béton', 'm3', 0.180, 90000.00),
    (25,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Bêches dosées à 350 kg/m3 - b) acier', 'kg', 12.780, 850.00),
    (26,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Agglos pleins de 15 cm de soubassement', 'm2', 36.000, 9500.00),
    (27,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Voile escalier en BA dosé à 350 kg/m3 - a) béton', 'm3', 0.090, 90000.00),
    (28,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Voile escalier en BA dosé à 350 kg/m3 - b) acier', 'kg', 8.100, 850.00),
    (29,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Voile escalier en BA dosé à 350 kg/m3 - c) coffrage', 'm2', 1.620, 3600.00),
    (30,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Chaînage bas en BA dosé à 350 kg/m3 - a) béton', 'm3', 1.360, 90000.00),
    (31,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Chaînage bas en BA dosé à 350 kg/m3 - b) acier', 'kg', 1315.000, 850.00),
    (32,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Chaînage bas en BA dosé à 350 kg/m3 - c) coffrage', 'm2', 18.080, 3600.00),
    (33,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Dallage en BA avec treillis soudé ép. 10 cm dosé à 250 kg/m3 - a) béton', 'm3', 9.220, 90000.00),
    (34,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Dallage en BA avec treillis soudé ép. 10 cm dosé à 250 kg/m3 - b) acier', 'kg', 368.840, 850.00),
    (35,  'GO',  'INFRA_FOND', 'FONDATION', NULL, 'Dallage en BA avec treillis soudé ép. 10 cm dosé à 250 kg/m3 - c) film polyane', 'm2', 92.210, 500.00),

    -- 4. ELEVATION RDC (GO)
    (36,  'GO',  'RDC_SUPER',  NULL, NULL, 'Maçonnerie en agglos de 15 creux (RDC)', 'm2', 197.000, 8500.00),
    (37,  'GO',  'RDC_SUPER',  NULL, NULL, 'Maçonnerie en agglos de 10 creux (RDC)', 'm2', 37.340, 7500.00),
    (38,  'GO',  'RDC_SUPER',  NULL, NULL, 'Poteaux et raidisseurs en BA (RDC) - a) béton', 'm3', 2.300, 90000.00),
    (39,  'GO',  'RDC_SUPER',  NULL, NULL, 'Poteaux et raidisseurs en BA (RDC) - b) acier', 'kg', 275.520, 850.00),
    (40,  'GO',  'RDC_SUPER',  NULL, NULL, 'Poteaux et raidisseurs en BA (RDC) - c) coffrage', 'm2', 42.240, 3600.00),
    (41,  'GO',  'RDC_SUPER',  NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (RDC) - a) béton', 'm3', 1.890, 90000.00),
    (42,  'GO',  'RDC_SUPER',  NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (RDC) - b) acier', 'kg', 170.440, 850.00),
    (43,  'GO',  'RDC_SUPER',  NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (RDC) - c) coffrage', 'm2', 7.700, 3600.00),
    (44,  'GO',  'RDC_SUPER',  NULL, NULL, 'Divers éléments en BA dosé à 350 kg/m3 (RDC) - a) béton', 'm3', 0.890, 90000.00),
    (45,  'GO',  'RDC_SUPER',  NULL, NULL, 'Divers éléments en BA dosé à 350 kg/m3 (RDC) - b) acier', 'kg', 80.100, 850.00),
    (46,  'GO',  'RDC_SUPER',  NULL, NULL, 'Divers éléments en BA dosé à 350 kg/m3 (RDC) - c) coffrage', 'm2', 16.020, 3600.00),
    (47,  'GO',  'RDC_SUPER',  NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (RDC) - a) béton', 'm3', 6.640, 90000.00),
    (48,  'GO',  'RDC_SUPER',  NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (RDC) - b) acier', 'kg', 598.000, 850.00),
    (49,  'GO',  'RDC_SUPER',  NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (RDC) - c) coffrage', 'm2', 70.120, 3600.00),
    (50,  'GO',  'RDC_SUPER',  NULL, NULL, 'Escalier en BA dosé à 350 kg/m3 (RDC) - a) béton', 'm3', 2.420, 90000.00),
    (51,  'GO',  'RDC_SUPER',  NULL, NULL, 'Escalier en BA dosé à 350 kg/m3 (RDC) - b) acier', 'kg', 218.000, 850.00),
    (52,  'GO',  'RDC_SUPER',  NULL, NULL, 'Escalier en BA dosé à 350 kg/m3 (RDC) - c) coffrage', 'm2', 47.000, 3600.00),
    (53,  'GO',  'RDC_SUPER',  NULL, NULL, 'Plancher en corps creux en hourdis 16+4 (RDC)', 'm2', 117.000, 35000.00),
    (54,  'GO',  'RDC_FAUX_PL',NULL, NULL, 'Staff lisse avec corniche (RDC)', 'm2', 92.210, 8900.00),
    (55,  'GO',  'RDC_ENDUITS',NULL, NULL, 'Lissage au panticoat des murs intérieurs (RDC)', 'm2', 323.680, 3000.00),
    (56,  'GO',  'RDC_ENDUITS',NULL, NULL, 'Enduit ciment sur murs extérieurs et intérieurs (RDC)', 'm2', 468.680, 2300.00),
    (57,  'GO',  'RDC_ENDUITS',NULL, NULL, 'Enduit ciment en sous face plancher (RDC)', 'm2', 123.510, 2300.00),
    (58,  'GO',  'RDC_ENDUITS',NULL, NULL, 'Enduit ciment sur divers ouvrages (RDC)', 'm2', 36.000, 2300.00),

    -- 5. ELEVATION ETAGE 1 (GO)
    (59,  'GO',  'E1_SUPER',   NULL, NULL, 'Maçonnerie en agglos de 15 creux (Étage 1)', 'm2', 226.000, 8500.00),
    (60,  'GO',  'E1_SUPER',   NULL, NULL, 'Maçonnerie en agglos de 10 creux (Étage 1)', 'm2', 45.000, 7500.00),
    (61,  'GO',  'E1_SUPER',   NULL, NULL, 'Poteaux et raidisseurs en BA (Étage 1) - a) béton', 'm3', 2.300, 90000.00),
    (62,  'GO',  'E1_SUPER',   NULL, NULL, 'Poteaux et raidisseurs en BA (Étage 1) - b) acier', 'kg', 275.520, 850.00),
    (63,  'GO',  'E1_SUPER',   NULL, NULL, 'Poteaux et raidisseurs en BA (Étage 1) - c) coffrage', 'm2', 42.240, 3600.00),
    (64,  'GO',  'E1_SUPER',   NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (Étage 1) - a) béton', 'm3', 2.940, 90000.00),
    (65,  'GO',  'E1_SUPER',   NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (Étage 1) - b) acier', 'kg', 235.200, 850.00),
    (66,  'GO',  'E1_SUPER',   NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (Étage 1) - c) coffrage', 'm2', 44.100, 3600.00),
    (67,  'GO',  'E1_SUPER',   NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (Étage 1) - a) béton', 'm3', 6.640, 90000.00),
    (68,  'GO',  'E1_SUPER',   NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (Étage 1) - b) acier', 'kg', 598.000, 850.00),
    (69,  'GO',  'E1_SUPER',   NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (Étage 1) - c) coffrage', 'm2', 70.120, 3600.00),
    (70,  'GO',  'E1_SUPER',   NULL, NULL, 'Escalier en BA dosé à 350 kg/m3 (Étage 1) - a) béton', 'm3', 2.420, 90000.00),
    (71,  'GO',  'E1_SUPER',   NULL, NULL, 'Escalier en BA dosé à 350 kg/m3 (Étage 1) - b) acier', 'kg', 218.000, 850.00),
    (72,  'GO',  'E1_SUPER',   NULL, NULL, 'Escalier en BA dosé à 350 kg/m3 (Étage 1) - c) coffrage', 'm2', 47.000, 3600.00),
    (73,  'GO',  'E1_SUPER',   NULL, NULL, 'Plancher en corps creux en hourdis 16+4 (Étage 1)', 'm2', 120.000, 35000.00),
    (74,  'GO',  'E1_ETANCH',  NULL, NULL, 'Étanchéité salles humides & balcons : Afrikote+bitume+chappe 40 (Étage 1)', 'm2', 24.790, 9500.00),
    (75,  'GO',  'E1_FAUX_PL', NULL, NULL, 'Staff lisse avec corniche (Étage 1)', 'm2', 102.300, 8900.00),
    (76,  'GO',  'E1_ENDUITS', NULL, NULL, 'Lissage au panticoat des murs intérieurs (Étage 1)', 'm2', 396.000, 3000.00),
    (77,  'GO',  'E1_ENDUITS', NULL, NULL, 'Enduit ciment sur murs extérieurs et intérieurs (Étage 1)', 'm2', 542.000, 2300.00),
    (78,  'GO',  'E1_ENDUITS', NULL, NULL, 'Enduit ciment en sous face plancher (Étage 1)', 'm2', 126.510, 2300.00),
    (79,  'GO',  'E1_ENDUITS', NULL, NULL, 'Enduit ciment sur divers ouvrages (Étage 1)', 'm2', 42.000, 2300.00),

    -- 6. ELEVATION ETAGE 2 (GO)
    (80,  'GO',  'E2_SUPER',   NULL, NULL, 'Maçonnerie en agglos de 15 creux (Étage 2)', 'm2', 116.000, 8500.00),
    (81,  'GO',  'E2_SUPER',   NULL, NULL, 'Maçonnerie en agglos de 10 creux (Étage 2)', 'm2', 10.000, 7500.00),
    (82,  'GO',  'E2_SUPER',   NULL, NULL, 'Poteaux et raidisseurs en BA (Étage 2) - a) béton', 'm3', 2.300, 90000.00),
    (83,  'GO',  'E2_SUPER',   NULL, NULL, 'Poteaux et raidisseurs en BA (Étage 2) - b) acier', 'kg', 275.520, 850.00),
    (84,  'GO',  'E2_SUPER',   NULL, NULL, 'Poteaux et raidisseurs en BA (Étage 2) - c) coffrage', 'm2', 42.240, 3600.00),
    (85,  'GO',  'E2_SUPER',   NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (Étage 2) - a) béton', 'm3', 0.900, 90000.00),
    (86,  'GO',  'E2_SUPER',   NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (Étage 2) - b) acier', 'kg', 72.000, 850.00),
    (87,  'GO',  'E2_SUPER',   NULL, NULL, 'Chaînage haut en BA dosé à 350 kg/m3 (Étage 2) - c) coffrage', 'm2', 13.500, 3600.00),
    (88,  'GO',  'E2_SUPER',   NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (Étage 2) - a) béton', 'm3', 4.330, 90000.00),
    (89,  'GO',  'E2_SUPER',   NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (Étage 2) - b) acier', 'kg', 389.000, 850.00),
    (90,  'GO',  'E2_SUPER',   NULL, NULL, 'Poutre en BA dosée à 350 kg/m3 (Étage 2) - c) coffrage', 'm2', 49.820, 3600.00),
    (91,  'GO',  'E2_SUPER',   NULL, NULL, 'Plancher en corps creux hourdis 10+5 pour toiture dalle', 'm2', 70.000, 30000.00),
    (92,  'GO',  'E2_ETANCH',  NULL, NULL, 'Étanchéité sur tout l''étage : Afrikote+bitume+chappe 40', 'm2', 82.440, 9500.00),
    (93,  'GO',  'E2_ETANCH',  NULL, NULL, 'Étanchéité sur tout l''étage : Afrikote+bitume+paradième', 'm2', 18.770, 12500.00),
    (94,  'GO',  'E2_FAUX_PL', NULL, NULL, 'Staff lisse avec corniche (Étage 2)', 'm2', 70.000, 8900.00),
    (95,  'GO',  'E2_ENDUITS', NULL, NULL, 'Lissage au panticoat des murs intérieurs (Étage 2)', 'm2', 63.000, 3000.00),
    (96,  'GO',  'E2_ENDUITS', NULL, NULL, 'Enduit ciment sur murs extérieurs et intérieurs (Étage 2)', 'm2', 252.000, 2300.00),
    (97,  'GO',  'E2_ENDUITS', NULL, NULL, 'Enduit ciment en sous face plancher (Étage 2)', 'm2', 70.000, 2300.00),
    (98,  'GO',  'E2_ENDUITS', NULL, NULL, 'Enduit ciment sur divers ouvrages (Étage 2)', 'm2', 18.000, 2300.00),

    -- 7. PLOMBERIE - ASSAINISSEMENT (SO)
    (99,  'SO',  'PLOMBERIE',  'INST_EAU', NULL, 'Canalisation en PPR diam. 14', 'ml', 30.000, 3000.00),
    (100, 'SO',  'PLOMBERIE',  'INST_EAU', NULL, 'Canalisation en PPR diam. 16', 'ml', 80.000, 3800.00),
    (101, 'SO',  'PLOMBERIE',  'INST_EAU', NULL, 'Canalisation en PPR diam. 25', 'ml', 30.000, 7300.00),
    (102, 'SO',  'PLOMBERIE',  'INST_EAU', NULL, 'Vanne d''arrêt 15/21', 'u', 8.000, 8300.00),
    (103, 'SO',  'PLOMBERIE',  'RES_EVAC', NULL, 'Canalisation PVC EU-EV diam. 40', 'ml', 30.000, 1500.00),
    (104, 'SO',  'PLOMBERIE',  'RES_EVAC', NULL, 'Canalisation PVC EU-EV diam. 75', 'ml', 45.000, 2500.00),
    (105, 'SO',  'PLOMBERIE',  'RES_EVAC', NULL, 'Canalisation PVC EU-EV diam. 110', 'ml', 90.000, 3500.00),
    (106, 'SO',  'PLOMBERIE',  'RES_EVAC', NULL, 'Canalisation PVC EU-EV diam. 125', 'ml', 90.000, 5500.00),
    (107, 'SO',  'PLOMBERIE',  'RES_EVAC', NULL, 'Canalisation PVC EU-EV diam. 160', 'ml', 45.000, 7500.00),
    (108, 'SO',  'PLOMBERIE',  NULL, NULL, 'Évier de cuisine (PU 0 / PM non pris en compte)', 'u', 1.000, 0.00),
    (109, 'SO',  'PLOMBERIE',  NULL, NULL, 'Lavabo + Robinetterie (PU 0 / PM non pris en compte)', 'u', 6.000, 0.00),
    (110, 'SO',  'PLOMBERIE',  NULL, NULL, 'WC (PU 0 / PM non pris en compte)', 'u', 6.000, 0.00),
    (111, 'SO',  'PLOMBERIE',  NULL, NULL, 'Colonne de douche (PU 0 / PM non pris en compte)', 'u', 4.000, 0.00),
    (112, 'SO',  'PLOMBERIE',  NULL, NULL, 'Siphon de sol métallique gros-modèle', 'u', 4.000, 25000.00),
    (113, 'SO',  'PLOMBERIE',  NULL, NULL, 'Siphon de sol métallique petit modèle', 'u', 6.000, 7500.00),
    (114, 'SO',  'PLOMBERIE',  NULL, NULL, 'Porte savon en plastique (PU 0 / PM non pris en compte)', 'u', 4.000, 0.00),
    (115, 'SO',  'PLOMBERIE',  NULL, NULL, 'Dévidoir de papier en plastique (PU 0 / PM non pris en compte)', 'u', 4.000, 0.00),
    (116, 'SO',  'PLOMBERIE',  NULL, NULL, 'Porte serviette à 2 branches (PU 0 / PM non pris en compte)', 'u', 4.000, 0.00),
    (117, 'SO',  'PLOMBERIE',  NULL, NULL, 'Tablette de lavabo vitrée (PU 0 / PM non pris en compte)', 'u', 6.000, 0.00),
    (118, 'SO',  'PLOMBERIE',  NULL, NULL, 'Glace de lavabo (PU 0 / PM non pris en compte)', 'u', 4.000, 0.00),
    (119, 'SO',  'PLOMBERIE',  NULL, NULL, 'Fosse septique + puits perdu (20 Usagers)', 'u', 1.000, 1000000.00),
    (120, 'SO',  'PLOMBERIE',  NULL, NULL, 'Regard EU/EV 60 x 60 cm²', 'u', 7.000, 75000.00),

    -- 8. ELECTRICITE ET CLIMATISATION (SO)
    (121, 'SO',  'ELECTRICITE', NULL, NULL, 'Barrette de coupure (grand modèle)', 'ml', 20.000, 8000.00),
    (122, 'SO',  'ELECTRICITE', NULL, NULL, 'Piquet de terre 2 m', 'ml', 1.000, 15000.00),
    (123, 'SO',  'ELECTRICITE', NULL, NULL, 'Boîtier de distribution principal y compris accessoires', 'u', 3.000, 125000.00),
    (124, 'SO',  'ELECTRICITE', NULL, NULL, 'Tube orange diamètre 13', 'ml', 450.000, 750.00),
    (125, 'SO',  'ELECTRICITE', NULL, NULL, 'Tube orange diamètre 11', 'ml', 750.000, 500.00),
    (126, 'SO',  'ELECTRICITE', NULL, NULL, 'Câble VGV 3x1,5', 'ml', 350.000, 1500.00),
    (127, 'SO',  'ELECTRICITE', NULL, NULL, 'TH 10 mm²', 'ml', 80.000, 7050.00),
    (128, 'SO',  'ELECTRICITE', NULL, NULL, 'TH 2,5 mm²', 'ml', 200.000, 1050.00),
    (129, 'SO',  'ELECTRICITE', NULL, NULL, 'TH 1,5 mm²', 'ml', 150.000, 650.00),
    (130, 'SO',  'ELECTRICITE', NULL, NULL, 'Interrupteur SA', 'u', 10.000, 1000.00),
    (131, 'SO',  'ELECTRICITE', NULL, NULL, 'Interrupteur SA étanche', 'u', 8.000, 2000.00),
    (132, 'SO',  'ELECTRICITE', NULL, NULL, 'Interrupteur VV', 'u', 16.000, 1500.00),
    (133, 'SO',  'ELECTRICITE', NULL, NULL, 'Prises de courant 2P+T 10/16A', 'u', 22.000, 1500.00),
    (134, 'SO',  'ELECTRICITE', NULL, NULL, 'Prises de courant 2P+T 10/16A étanche', 'u', 8.000, 2500.00),
    (135, 'SO',  'ELECTRICITE', NULL, NULL, 'Dismatic 20 A', 'u', 6.000, 25000.00),
    (136, 'SO',  'ELECTRICITE', NULL, NULL, 'DPN de 16 A', 'u', 8.000, 8000.00),
    (137, 'SO',  'ELECTRICITE', NULL, NULL, 'DPN de 10 A', 'u', 8.000, 7000.00),
    (138, 'SO',  'ELECTRICITE', NULL, NULL, 'Boîte de dérivation ref 294', 'u', 3.000, 7500.00),
    (139, 'SO',  'ELECTRICITE', NULL, NULL, 'Boîte de dérivation étanche 150/110', 'u', 6.000, 7000.00),
    (140, 'SO',  'ELECTRICITE', NULL, NULL, 'Boîte ronde', 'u', 60.000, 200.00),
    (141, 'SO',  'ELECTRICITE', NULL, NULL, 'Boîte étanche apparent 100/100', 'u', 6.000, 5000.00),
    (142, 'SO',  'ELECTRICITE', NULL, NULL, 'Douille', 'u', 30.000, 500.00),

    -- 9. REVETEMENTS DURS (SO)
    (143, 'SO',  'REVETEMENTS', 'CARRELAGE_SOL', 'CHAPE_CIMENT', 'Chape ciment', 'm2', 264.510, 2400.00),
    (144, 'SO',  'REVETEMENTS', 'CARRELAGE_SOL', 'GRES_CERAME',  'Carreaux grès cérame 60x60 (50% dédié achat carreau)', 'm2', 264.510, 11000.00),
    (145, 'SO',  'REVETEMENTS', 'CARRELAGE_MUR', NULL,           'Faïence toute hauteur salles d''eaux (hors cuisine)', 'm2', 33.000, 11000.00),

    -- 10. MENUISERIE BOIS - MENUISERIE ALUMINIUM (SO)
    (146, 'SO',  'MENUISERIE',  NULL, NULL, 'Porte alu gris coulissante anodisée 2 vantaux vitrage réfléchissant 6mm (L200xH220)', 'u', 3.000, 412500.00),
    (147, 'SO',  'MENUISERIE',  NULL, NULL, 'Portes bois pleines y/c serrures et vernis 100x220 (PM)', 'u', 3.000, 0.00),
    (148, 'SO',  'MENUISERIE',  NULL, NULL, 'Portes bois pleines y/c serrures et vernis 90x220 (PM)', 'u', 5.000, 0.00),
    (149, 'SO',  'MENUISERIE',  NULL, NULL, 'Portes bois pleines y/c serrures et vernis 70x220 (PM)', 'u', 6.000, 0.00),
    (150, 'SO',  'MENUISERIE',  NULL, NULL, 'Fenêtres alu gris coulissante anodisée vitrage réfléchissant 6mm (L150xH110)', 'u', 2.000, 123750.00),
    (151, 'SO',  'MENUISERIE',  NULL, NULL, 'Fenêtres alu gris coulissante anodisée vitrage réfléchissant 6mm (L120xH110)', 'u', 5.000, 99000.00),
    (152, 'SO',  'MENUISERIE',  NULL, NULL, 'Fenêtres alu gris coulissante anodisée vitrage imprimé 6mm (L80xH60)', 'u', 6.000, 60000.00),
    (153, 'SO',  'MENUISERIE',  NULL, NULL, 'Garde-corps en inox avec vitrage triplex 10mm (L1210xH80 cm²)', 'u', 1.000, 1258400.00),

    -- 11. PEINTURE (SO)
    (154, 'SO',  'PEINTURE',    NULL, NULL, 'Peinture pantigré sur murs extérieurs', 'm2', 353.650, 6500.00),
    (155, 'SO',  'PEINTURE',    NULL, NULL, 'Peinture delux sur murs intérieurs', 'm2', 782.680, 1500.00),
    (156, 'SO',  'PEINTURE',    NULL, NULL, 'Peinture delux sur staff du plancher', 'm2', 320.020, 1500.00),

    -- 12. AMENAGEMENT EXTERIEUR (VRD)
    (157, 'VRD', 'PARKING',     NULL, NULL, 'Nivellement sous dallage parking', 'm3', 24.000, 4000.00),
    (158, 'VRD', 'PARKING',     NULL, NULL, 'Dallage parking BA treillis soudé 10cm 250 kg/m3 - a) béton', 'm3', 4.000, 90000.00),
    (159, 'VRD', 'PARKING',     NULL, NULL, 'Dallage parking BA treillis soudé 10cm 250 kg/m3 - b) acier', 'kg', 160.000, 850.00),
    (160, 'VRD', 'PARKING',     NULL, NULL, 'Dallage parking BA treillis soudé 10cm 250 kg/m3 - c) film polyane', 'm2', 40.000, 500.00),
    (161, 'VRD', 'PARKING',     NULL, NULL, 'Chape ciment sur dallage parking', 'm2', 40.000, 2400.00),
    (162, 'VRD', 'CLOTURE',     NULL, NULL, 'Peinture pantigré sur murs extérieurs (Clôture)', 'm2', 154.000, 6500.00),
    (163, 'VRD', 'CLOTURE',     NULL, NULL, 'Réalisation niche compteur eau et courant', 'ens', 1.000, 150000.00),
    (164, 'VRD', 'JARDINAGE',   NULL, NULL, 'Apport terre végétale et engazonnement', 'ens', 1.000, 185000.00),
    (165, 'VRD', 'JARDINAGE',   NULL, NULL, 'Gravillons lavé sur parking', 'ens', 1.000, 328000.00)
) AS v(ordre_affichage, code_lot, code_poste, code_tache, code_sous_tache, designation, unite, quantite, prix_unitaire_ht) ON TRUE
JOIN immobilier.refm_btp_lot l ON l.code = v.code_lot
LEFT JOIN immobilier.refm_btp_poste p ON p.code = v.code_poste AND p.lot_id = l.id
LEFT JOIN immobilier.refm_btp_tache t ON t.code = v.code_tache AND t.poste_id = p.id
LEFT JOIN immobilier.refm_btp_sous_tache st ON st.code = v.code_sous_tache AND st.tache_id = t.id
WHERE d.numero_devis = 'YKSBat0201-07102026';

--changeset syao:2026-11-21-btp-13-data-situation-facture context:btp-init labels:btp-init
INSERT INTO immobilier.btp_situation_travaux (
    projet_id, numero_situation, date_situation, periode_debut, periode_fin, statut
)
SELECT
    id, 'SIT-01', DATE '2026-11-15', DATE '2026-10-15', DATE '2026-11-15', 'VALIDEE'
FROM immobilier.btp_projet WHERE code_projet = 'PRJ-BING-2026-02';

INSERT INTO immobilier.btp_situation_ligne (
    situation_id, ligne_devis_id, quantite_precedente, quantite_periode, quantite_cumulee, montant_periode_ht
)
SELECT
    s.id,
    ld.id,
    0.000,
    ld.quantite,
    ld.quantite,
    ld.montant_ht
FROM immobilier.btp_situation_travaux s
JOIN immobilier.btp_projet p ON p.id = s.projet_id
JOIN immobilier.btp_devis d ON d.projet_id = p.id
JOIN immobilier.btp_ligne_devis ld ON ld.devis_id = d.id
JOIN immobilier.refm_btp_poste po ON po.id = ld.poste_id
WHERE p.code_projet = 'PRJ-BING-2026-02'
  AND s.numero_situation = 'SIT-01'
  AND po.code IN ('ETUDES_INST', 'INFRA_TERR', 'INFRA_FOND');

-- Totaux de la situation (calcules ici car le recalcul automatique est desormais porte par le service)
UPDATE immobilier.btp_situation_travaux s
SET montant_periode_ht = t.total,
    montant_cumule_ht = t.total,
    taux_avancement_pct = CASE WHEN p.budget_prevu_ht > 0 THEN ROUND(t.total / p.budget_prevu_ht * 100, 2) ELSE 0 END
FROM (
    SELECT situation_id, SUM(montant_periode_ht) AS total
    FROM immobilier.btp_situation_ligne
    GROUP BY situation_id
) t, immobilier.btp_projet p
WHERE t.situation_id = s.id
  AND p.id = s.projet_id
  AND s.numero_situation = 'SIT-01'
  AND p.code_projet = 'PRJ-BING-2026-02';

INSERT INTO immobilier.btp_facture (
    projet_id, situation_id, numero_facture, type_facture,
    date_emission, date_echeance, montant_ht, taux_tva, montant_ttc, statut
)
SELECT
    p.id,
    s.id,
    'FAC-2026-001',
    'SITUATION',
    DATE '2026-11-16',
    DATE '2026-12-16',
    s.montant_periode_ht,
    0.00,
    s.montant_periode_ht,
    'PARTIELLEMENT_PAYEE'
FROM immobilier.btp_projet p
JOIN immobilier.btp_situation_travaux s ON s.projet_id = p.id AND s.numero_situation = 'SIT-01'
WHERE p.code_projet = 'PRJ-BING-2026-02';

INSERT INTO immobilier.btp_paiement (
    facture_id, date_paiement, montant, mode_paiement, reference_transaction, commentaire
)
SELECT
    id, TIMESTAMPTZ '2026-11-18 00:00:00+00', 3500000.00, 'VIREMENT', 'VIR-ECOBANK-20261118-01', 'Premier versement client'
FROM immobilier.btp_facture WHERE numero_facture = 'FAC-2026-001';
