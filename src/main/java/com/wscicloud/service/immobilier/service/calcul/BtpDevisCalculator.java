package com.wscicloud.service.immobilier.service.calcul;

import java.math.BigDecimal;
import java.util.Collection;

import com.wscicloud.service.immobilier.domain.entities.BtpDevis;

import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.montant;
import static com.wscicloud.service.immobilier.service.calcul.BtpMontants.nz;

/** Calculs du devis : montant de ligne, totaux HT/TTC, budget du projet. */
public final class BtpDevisCalculator {

    private BtpDevisCalculator() {}

    public record Totaux(BigDecimal montantHt, BigDecimal montantTtc) {}

    /** montantHt = quantite x prixUnitaireHt arrondi a 2 decimales (HALF_UP). */
    public static BigDecimal montantLigne(BigDecimal quantite, BigDecimal prixUnitaireHt) {
        return BtpMontants.produit(quantite, prixUnitaireHt);
    }

    /** HT = somme des lignes ; TTC = HT x (1 + tauxTva / 100). */
    public static Totaux totaux(Collection<BigDecimal> montantsLignes, BigDecimal tauxTva) {
        BigDecimal ht = BtpMontants.somme(montantsLignes);
        return new Totaux(ht, BtpMontants.ttc(ht, tauxTva));
    }

    /** Budget du projet : somme des devis VALIDE / ACCEPTE / EN_COURS. */
    public static BigDecimal budget(Collection<BtpDevis> devis) {
        BigDecimal total = BigDecimal.ZERO;
        for (BtpDevis d : devis) {
            if (BtpStatuts.DEVIS_BUDGET.contains(d.getStatut())) {
                total = total.add(nz(d.getMontantHt()));
            }
        }
        return montant(total);
    }
}
