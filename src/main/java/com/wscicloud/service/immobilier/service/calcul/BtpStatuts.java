package com.wscicloud.service.immobilier.service.calcul;

import java.util.Set;

/** Statuts metier du module BTP. */
public final class BtpStatuts {

    public static final String DEVIS_BROUILLON = "BROUILLON";
    public static final String DEVIS_VALIDE = "VALIDE";
    public static final String DEVIS_ACCEPTE = "ACCEPTE";
    public static final String DEVIS_EN_COURS = "EN_COURS";

    /** Devis pris en compte dans le budget du projet. */
    public static final Set<String> DEVIS_BUDGET = Set.of(DEVIS_VALIDE, DEVIS_ACCEPTE, DEVIS_EN_COURS);

    public static final Set<String> DEVIS_AUTORISES =
            Set.of(DEVIS_BROUILLON, DEVIS_VALIDE, DEVIS_ACCEPTE, DEVIS_EN_COURS, "TERMINE", "REFUSE", "ANNULE");

    public static final String SITUATION_BROUILLON = "BROUILLON";
    public static final String SITUATION_VALIDEE = "VALIDEE";
    public static final String SITUATION_FACTUREE = "FACTUREE";

    /** Situations dont les travaux sont consideres comme executes. */
    public static final Set<String> SITUATION_EXECUTEE = Set.of(SITUATION_VALIDEE, SITUATION_FACTUREE);

    public static final Set<String> SITUATION_AUTORISES =
            Set.of(SITUATION_BROUILLON, "SOUMISE", SITUATION_VALIDEE, SITUATION_FACTUREE, "REJETEE");

    public static final String FACTURE_EMISE = "EMISE";
    public static final String FACTURE_PARTIELLEMENT_PAYEE = "PARTIELLEMENT_PAYEE";
    public static final String FACTURE_PAYEE = "PAYEE";
    public static final String FACTURE_ANNULEE = "ANNULEE";

    public static final String TYPE_FACTURE_SITUATION = "SITUATION";

    private BtpStatuts() {}
}
