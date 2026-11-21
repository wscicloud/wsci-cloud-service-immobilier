# Prompt – Génération du module BTP (Immobilier)

## 1. Rôle et objectif

Tu es un développeur senior Java 21 / Spring Boot / Liquibase / JSF (PrimeFaces) travaillant sur le workspace **wsci-cloud**.
Génère le module **BTP** (gestion de chantiers immobiliers, ex. *IMMEUBLE R+5 AMOKA*, *TRIPLEX SIMEON 6 PIÈCES*) dans :

| Projet | Livrable |
|--------|----------|
| `wsci-cloud-service-cashier` | Script Liquibase PostgreSQL (tables + données initiales) + code Java (entities, DTO, mapper, repository, service, controller) |
| `wsci-cloud-portail-ihm` | Pages `.xhtml` d'interaction utilisateur + managed beans / clients REST |

Respecte strictement les conventions existantes (module `stock` comme modèle : `controller`, `domain/{dto,entities,mapper,repository}`, `service` + `ServiceImpl`).

## 2. Fichiers de référence (dossier `src/main/resources/prompts/immobilier/doc-orign/`)

| Fichier | Usage |
|---------|-------|
| `schema-bdd.txt`, `mcd.txt` | Modèle de données source (schéma `amoka`, tables sans préfixe) – **à renommer en `btp_*` / `refm_btp_*`, sans schéma `amoka`** |
| `bdd-initialisation.txt` | Données initiales (projet AMOKA, lots, postes, lignes, bibliothèque, sous-traitants) – **à adapter aux noms de tables et colonnes `btp_*`** |
| `prompt-de-generation-code.txt` | Besoin fonctionnel initial |
| `DEVIS TRIPLEX PROMO SIMEON 6 PIECES 12 10 2023.pdf`, `2026_DEVIS TRIPLEX PROMO 6 PIECES SIMEON BAT N2 DU 07 10 2026.pdf` | Devis quantitatifs et estimatifs types (structure lots/postes/lignes) |
| `SITUATION DES TRAVAUX TRIPLEX SIMEON 14 03 2025.pdf` / `28 07 2025.xls` | Modèle de situation de travaux (marché / cumul M / cumul M-1 / production M) |

### Structure des documents métier à reproduire

**Devis** : `Lot (GO Gros œuvre / SO Second œuvre / VRD)` → `Poste` (Études et installation, Infrastructure [Terrassement, Fondation], Superstructure RDC / Étage 1 / Étage 2, Étanchéité, Faux-plafond, Enduits, Plomberie-Assainissement, Électricité-Climatisation, Revêtements durs, Menuiserie bois-aluminium, Peinture) → `Ligne` (désignation, unité `ens|fft|ml|m²|m³|kg|u`, quantité, PU, montant HT). Les lignes à sous-éléments (a) béton / b) acier / c) coffrage) utilisent le champ `sous_ensemble`. Totaux par poste, par lot, total général, **remise** éventuelle, TVA 18 % optionnelle.

**Situation de travaux** (mensuelle) : pour chaque ligne du marché → `avancement % cumulé M`, `montant cumulé M`, `avancement % cumulé M-1`, `montant cumulé M-1`, `avancement % production M`, `montant production M` (= cumul M − cumul M-1). Récapitulatif par poste + total gros œuvre + total second œuvre + remise + **total après remise**.

Données de démonstration issues des PDF/XLS : projet « TRIPLEX SIMEON 6 PIÈCES – BINGERVILLE », total devis 63 800 123 FCFA, remise 3 000 000, total après remise 60 800 123 ; récapitulatif : Études et installation 1 700 000 ; Infrastructures 6 592 240 ; Élévation RDC 11 817 088 ; Étage 1 13 331 179 ; Étage 2 7 397 627 ; Plomberie 5 314 400 ; Électricité 3 153 500 ; Revêtements durs 5 045 934 ; Menuiserie 5 583 400 ; Peinture 3 864 755.

## 3. Partie A – `wsci-cloud-service-cashier`

### 3.1 Script Liquibase (PostgreSQL)

* Emplacement : `src/main/resources/db/changelog/postgresql/changes/immobilier/btp_09.02.sql` (suivre la numérotation de version courante ; voir skill `version-update-script`).
* Enregistrer dans `db.changelog-master.yaml` **après** `caisse/` :
  ```yaml
  - includeAll:
      path: classpath*:db/changelog/postgresql/changes/immobilier/
      errorIfMissingOrEmpty: false
  ```
  (⚠ ne doit pas exécuter les `.txt` / `.pdf` / `.xls`; seul `.sql` est pris en compte.)
* Format :
  ```sql
  --liquibase formatted sql
  --changeset <auteur>:<date du jour(exemple:2024-02-28)> context:btp-init labels:btp-init
  ```
  Un `changeset` par table / par jeu de données, idempotent (`IF NOT EXISTS`, `ON CONFLICT DO NOTHING`).
* **Tables** (reprendre `schema-bdd.txt`, préfixe `btp_`, référentiel `refm_btp_`) :
  `btp_projet`, `btp_lot_devis`, `btp_poste_devis`, `btp_ligne_devis`, `btp_sous_detail_prix`, `btp_soustraitant`, `btp_depense_reelle`, `btp_situation_travaux`, `btp_tache_planning`, `btp_rapport_journalier`, `refm_btp_tache_biblio` (ex-`tache_btp_biblio`).
  La table `entreprise` du schéma source est **remplacée** par `sys_societe` (pas de `btp_entreprise`).
* **Tables et colonnes à ajouter** (absentes du schéma source) :
  * `btp_situation_ligne` (situation_id, ligne_devis_id, avancement_cumul_pct, montant_cumul_ht, avancement_precedent_pct, montant_precedent_ht, avancement_periode_pct, montant_periode_ht)
  * `btp_facture`, `btp_facture_ligne`, `btp_facture_paiement` (paiements multiples par facture : date, montant, mode, référence)
  * `btp_devis_version` (projet_id, numero_version, date, statut, commentaire) pour le versionning/duplication de devis
  * `btp_projet` : ajouter `societe_id`, `remise_montant NUMERIC(15,2)` et `statut_devis` (BROUILLON / VALIDE / ABANDONNE) ; les autres tables héritent de la société via `projet_id`.
  * `btp_depense_reelle` : ajouter `societe_id` et `reference_piece` (la table existe déjà dans le schéma source).
  * Le contrat de sous-traitance reste porté par `btp_soustraitant` (pas de table de contrat séparée).
* Clés étrangères `societe_id → sys_societe(id)`, index sur toutes les FK, `ON DELETE CASCADE` hiérarchique projet → lot → poste → ligne (→ sous-détail, situation_ligne).
* **Données initiales** (changesets séparés, `context`/`labels` : `btp-init`) :
  1. Projet **IMMEUBLE R+5 AMOKA** (valeurs de `bdd-initialisation.txt`) et projet **TRIPLEX SIMEON 6 PIÈCES** (valeurs des PDF).
  2. Lots `GO`, `SO`, `VRD` ; postes clés listés en §2.
  3. Lignes du devis type AMOKA (échantillon de `bdd-initialisation.txt`) et lignes du devis TRIPLEX extraites des PDF.
  4. Bibliothèque `refm_btp_tache_biblio` (PRE-001, TER-001, BET-001, FER-001, MAC-001, BET-005, PEI-001…).
  5. Sous-traitants par défaut (ST_01 Maçon, ST_02 Menuisier coffreur, ST_03 Ferrailleur, ST_06 Carreleur, ST_07 Plombier, ST_14 Électricien).
  6. Situation de travaux de démonstration du TRIPLEX (dernière situation disponible : 28/07/2025) reproduisant le récapitulatif du §2.
* Utiliser `INSERT … SELECT` avec sous-requêtes (code lot / nom poste) plutôt que des IDs en dur ; recaler les séquences (`setval`) si des IDs explicites sont utilisés. Ne pas utiliser `CREATE SCHEMA amoka` ni `SET search_path TO amoka`.
* Montants : `NUMERIC(15,2)`, taux `NUMERIC(5,4)` ; encodage UTF-8.

### 3.2 Code Java (package `com.wscicloud.service.immobilier`)

```
immobilier/
  controller/   BtpProjetController, BtpDevisController, BtpFactureController,
                BtpSituationController, BtpDepenseController, BtpPlanningController,
                BtpSousTraitantController, BtpBibliothequeController, BtpDashboardController
  domain/dto/ | entities/ | mapper/ | repository/
  service/      interfaces + *Impl
```

Fonctionnalités à couvrir :

1. **Devis** : créer, ajouter lignes / sous-lignes (sous-détail de prix), calculer montants (`montant_ht = quantité × PU`, totaux poste/lot/projet, remise, TVA), **dupliquer** un devis, **versionning**, génération du devis du projet (export PDF/Excel au format des devis joints), statut (BROUILLON, VALIDE, ABANDONNE).
2. **Factures** : création, lignes, calcul des montants (TVA 18 %, retenue de garantie 5 %, avance 4 %), suivi des **paiements multiples**.
3. **Dépenses réelles** + écart budget/réalisé (marge brute, alerte si < `marge_brute_min_acceptable`).
4. **Situations de travaux** : génération mensuelle à partir de la précédente (M-1 repris automatiquement), saisie de l'avancement par ligne, calcul de la production M, récapitulatif par poste, remise, total après remise, statut (BROUILLON / VALIDEE / FACTUREE), alerte si avancement > `taux_avancement_seuil_alerte`.
5. **Planning** : tâches (dates, durée, prédécesseurs JSONB, avancement), calcul de `date_fin`, rapports journaliers.
6. **Sous-traitants** (marché, avancement, retenue de garantie, nature du contrat).
7. **Bibliothèque** de tâches et prix BTP (CRUD sur `refm_btp_tache_biblio`).
8. **Tableau de bord** : budget vs réalisé, avancement, marge, alertes (agrège les données ci-dessus).

Contraintes : MapStruct (ou mappers existants) entité ↔ DTO, `BigDecimal` pour tous les montants (arrondi `HALF_UP`), validation Bean Validation, filtrage par `societe_id`, gestion d'erreurs et audit comme dans `stock`, **tests unitaires JUnit 5** des calculs (devis, situation, facture) – voir skill `unit-testing`.

## 4. Partie B – `wsci-cloud-portail-ihm`

* Emplacement : `src/main/webapp/portail/btp/` (+ entrée dans le menu existant du portail, même mécanisme que `stock/menu.xhtml`).
* Pages XHTML (PrimeFaces, même template/layout et même gestion de messages que `portail/stock`) :

| Page | Contenu |
|------|---------|
| `urlrewrite.xml`, `menu.xhtml`, `aide.xhtml` | Navigation du module et aide |
| `projet-list.xhtml`, `projet-form.xhtml` | Liste/filtre des projets, saisie (paramètres financiers, TVA, marges, durée) |
| `devis-form.xhtml` | Arbre Lot → Poste → Lignes éditable (`p:treeTable`/`p:dataTable` + `p:cellEditor`), recalcul des totaux en AJAX, boutons *Dupliquer*, *Nouvelle version*, *Exporter PDF/Excel* |
| `situation-list.xhtml`, `situation-form.xhtml` | Saisie de l'avancement par ligne (colonnes Marché / Cumul M / Cumul M-1 / Production M), récapitulatif et remise, validation |
| `facture-list.xhtml`, `facture-form.xhtml` | Factures et paiements multiples |
| `depense-list.xhtml`, `depense-form.xhtml` | Dépenses réelles et écarts |
| `planning.xhtml` | Tâches (tableau + diagramme de Gantt), rapports journaliers |
| `soustraitant-list.xhtml`, `soustraitant-form.xhtml` | Sous-traitants |
| `bibliotheque-list.xhtml` | Bibliothèque de tâches/prix de référence |
| `dashboard.xhtml` | Indicateurs : budget vs réalisé, avancement, marge, alertes |

* Beans (`@ViewScoped`) et appels REST vers le service cashier selon le pattern des écrans `stock` ; montants formatés `#,##0` (FCFA), pourcentages `0.00 %`, libellés i18n dans les fichiers de messages existants ; droits d'accès via les rôles du portail.

## 5. Critères d'acceptation

1. Le démarrage du service applique les changesets sans erreur sur une base vierge **et** sur une base existante (idempotent).
2. Les données initiales AMOKA et TRIPLEX sont présentes ; le total du devis TRIPLEX = **63 800 123** (60 800 123 après remise) et la situation de démonstration reproduit le récapitulatif du §2.
3. Chaque fonctionnalité §3.2 (1 à 8) est accessible par API REST et par une page du portail.
4. `mvn -q -pl ..\wsci-cloud-service-cashier -am test` passe ; code compilant sans warnings nouveaux.
5. Aucune modification des modules existants hors enregistrement du changelog et du menu.

## 6. Ordre de livraison

1. Script Liquibase + enregistrement master → 2. Entities/Repositories → 3. DTO/Mappers → 4. Services + tests → 5. Controllers → 6. Pages XHTML + beans → 7. Vérification (build + démarrage).
