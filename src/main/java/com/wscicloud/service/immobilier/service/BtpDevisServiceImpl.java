package com.wscicloud.service.immobilier.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpDevisVersionDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLotDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpPosteDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSousDetailPrixDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDevisVersion;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpLotDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpPosteDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;
import com.wscicloud.service.immobilier.domain.entities.BtpSousDetailPrix;
import com.wscicloud.service.immobilier.domain.mapper.BtpDevisVersionMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpLigneDevisMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpLotDevisMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpPosteDevisMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpProjetMapper;
import com.wscicloud.service.immobilier.domain.mapper.BtpSousDetailPrixMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpDevisVersionRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLotDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpPosteDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSousDetailPrixRepository;
import com.wscicloud.service.immobilier.service.calcul.BtpDevisCalculator;
import com.wscicloud.service.immobilier.service.calcul.BtpMontants;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Getter
@Setter
@Service
@Slf4j
public class BtpDevisServiceImpl implements BtpDevisService {

    private static final Set<String> STATUTS = Set.of("BROUILLON", "VALIDE", "ABANDONNE");

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpLotDevisRepository lotRepository;

    @Autowired
    private BtpPosteDevisRepository posteRepository;

    @Autowired
    private BtpLigneDevisRepository ligneRepository;

    @Autowired
    private BtpSousDetailPrixRepository sousDetailRepository;

    @Autowired
    private BtpDevisVersionRepository versionRepository;

    @Autowired
    private BtpProjetMapper projetMapper;

    @Autowired
    private BtpLotDevisMapper lotMapper;

    @Autowired
    private BtpPosteDevisMapper posteMapper;

    @Autowired
    private BtpLigneDevisMapper ligneMapper;

    @Autowired
    private BtpSousDetailPrixMapper sousDetailMapper;

    @Autowired
    private BtpDevisVersionMapper versionMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private BtpProjet getProjet(Long projetId) {
        return projetRepository
                .findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet BTP introuvable : " + projetId));
    }

    @Override
    public BtpDevisDto findDevis(Long projetId) {
        BtpProjet projet = getProjet(projetId);
        BtpDevisDto devis = new BtpDevisDto();
        devis.setProjet(projetMapper.modeleToDto(projet));

        List<BigDecimal> totauxLots = new ArrayList<>();
        for (BtpLotDevis lot : lotRepository.findByProjetIdOrderByOrdre(projetId)) {
            BtpDevisDto.Lot lotDto = new BtpDevisDto.Lot();
            lotDto.setId(lot.getId());
            lotDto.setCodeLot(lot.getCodeLot());
            lotDto.setNomLot(lot.getNomLot());
            lotDto.setOrdre(lot.getOrdre());

            List<BigDecimal> totauxPostes = new ArrayList<>();
            for (BtpPosteDevis poste : posteRepository.findByLotIdOrderByOrdre(lot.getId())) {
                BtpDevisDto.Poste posteDto = new BtpDevisDto.Poste();
                posteDto.setId(poste.getId());
                posteDto.setNomPoste(poste.getNomPoste());
                posteDto.setOrdre(poste.getOrdre());
                posteDto.setLignes(ligneMapper.modeleToDto(ligneRepository.findByPosteIdOrderByOrdre(poste.getId())));
                posteDto.setTotalHt(BtpMontants.somme(posteDto.getLignes().stream()
                        .map(BtpLigneDevisDto::getMontantHt)
                        .toList()));
                totauxPostes.add(posteDto.getTotalHt());
                lotDto.getPostes().add(posteDto);
            }
            lotDto.setTotalHt(BtpMontants.somme(totauxPostes));
            totauxLots.add(lotDto.getTotalHt());
            devis.getLots().add(lotDto);
        }

        devis.setTotalHt(BtpMontants.somme(totauxLots));
        devis.setRemise(BtpMontants.montant(projet.getRemiseMontant()));
        devis.setTotalApresRemise(BtpDevisCalculator.totalApresRemise(devis.getTotalHt(), devis.getRemise()));
        boolean tvaApplicable = Boolean.TRUE.equals(projet.getDevisAfficherTva())
                && !Boolean.TRUE.equals(projet.getDevisExonerationTva());
        devis.setTauxTva(tvaApplicable ? BtpMontants.taux(projet.getTvaTaux()) : BigDecimal.ZERO);
        devis.setMontantTva(
                tvaApplicable
                        ? BtpDevisCalculator.montantTva(devis.getTotalApresRemise(), projet.getTvaTaux())
                        : BigDecimal.ZERO.setScale(2));
        devis.setTotalTtc(BtpMontants.montant(devis.getTotalApresRemise().add(devis.getMontantTva())));
        return devis;
    }

    @Override
    public BtpDevisDto recalculer(Long projetId) {
        for (BtpLotDevis lot : lotRepository.findByProjetIdOrderByOrdre(projetId)) {
            for (BtpPosteDevis poste : posteRepository.findByLotIdOrderByOrdre(lot.getId())) {
                for (BtpLigneDevis ligne : ligneRepository.findByPosteIdOrderByOrdre(poste.getId())) {
                    ligne.setMontantHt(BtpDevisCalculator.montantLigne(ligne.getQuantite(), ligne.getPrixUnitaire()));
                    ligneRepository.save(ligne);
                }
            }
        }
        return findDevis(projetId);
    }

    @Override
    public BtpLotDevisDto saveLot(BtpLotDevisDto dto) {
        return lotMapper.modeleToDto(lotRepository.save(lotMapper.dtoToModele(dto)));
    }

    @Override
    public void deleteLot(Long id) {
        lotRepository.deleteById(id);
    }

    @Override
    public BtpPosteDevisDto savePoste(BtpPosteDevisDto dto) {
        return posteMapper.modeleToDto(posteRepository.save(posteMapper.dtoToModele(dto)));
    }

    @Override
    public void deletePoste(Long id) {
        posteRepository.deleteById(id);
    }

    @Override
    public BtpLigneDevisDto saveLigne(BtpLigneDevisDto dto) {
        dto.setMontantHt(BtpDevisCalculator.montantLigne(dto.getQuantite(), dto.getPrixUnitaire()));
        return ligneMapper.modeleToDto(ligneRepository.save(ligneMapper.dtoToModele(dto)));
    }

    @Override
    public void deleteLigne(Long id) {
        ligneRepository.deleteById(id);
    }

    @Override
    public BtpSousDetailPrixDto saveSousDetail(BtpSousDetailPrixDto dto) {
        BtpSousDetailPrix entity = sousDetailMapper.dtoToModele(dto);
        if (entity.getId() == null) {
            sousDetailRepository.findByLigneId(dto.getLigneId()).ifPresent(e -> entity.setId(e.getId()));
        }
        BtpSousDetailPrix saved = sousDetailRepository.save(entity);

        // le prix de vente du sous-detail alimente le prix unitaire de la ligne
        if (saved.getPrixVenteCalcule() != null && saved.getPrixVenteCalcule().signum() > 0) {
            ligneRepository.findById(saved.getLigneId()).ifPresent(l -> {
                l.setPrixUnitaire(saved.getPrixVenteCalcule());
                l.setMontantHt(BtpDevisCalculator.montantLigne(l.getQuantite(), l.getPrixUnitaire()));
                ligneRepository.save(l);
            });
        }
        return sousDetailMapper.modeleToDto(saved);
    }

    @Override
    public BtpSousDetailPrixDto findSousDetail(Long ligneId) {
        return sousDetailRepository
                .findByLigneId(ligneId)
                .map(sousDetailMapper::modeleToDto)
                .orElse(null);
    }

    @Override
    public BtpProjetDto dupliquer(Long projetId) {
        BtpProjet source = getProjet(projetId);
        BtpProjet copie = new BtpProjet();
        BeanUtils.copyProperties(source, copie, "id");
        copie.setNom(source.getNom() + " (copie)");
        copie.setStatutDevis("BROUILLON");
        copie = projetRepository.save(copie);

        for (BtpLotDevis lot : lotRepository.findByProjetIdOrderByOrdre(projetId)) {
            BtpLotDevis lotCopie = new BtpLotDevis();
            BeanUtils.copyProperties(lot, lotCopie, "id");
            lotCopie.setProjetId(copie.getId());
            lotCopie = lotRepository.save(lotCopie);

            for (BtpPosteDevis poste : posteRepository.findByLotIdOrderByOrdre(lot.getId())) {
                BtpPosteDevis posteCopie = new BtpPosteDevis();
                BeanUtils.copyProperties(poste, posteCopie, "id");
                posteCopie.setLotId(lotCopie.getId());
                posteCopie = posteRepository.save(posteCopie);

                for (BtpLigneDevis ligne : ligneRepository.findByPosteIdOrderByOrdre(poste.getId())) {
                    BtpLigneDevis ligneCopie = new BtpLigneDevis();
                    BeanUtils.copyProperties(ligne, ligneCopie, "id");
                    ligneCopie.setPosteId(posteCopie.getId());
                    ligneCopie = ligneRepository.save(ligneCopie);

                    final Long ligneCopieId = ligneCopie.getId();
                    sousDetailRepository.findByLigneId(ligne.getId()).ifPresent(sd -> {
                        BtpSousDetailPrix sdCopie = new BtpSousDetailPrix();
                        BeanUtils.copyProperties(sd, sdCopie, "id");
                        sdCopie.setLigneId(ligneCopieId);
                        sousDetailRepository.save(sdCopie);
                    });
                }
            }
        }
        return projetMapper.modeleToDto(copie);
    }

    @Override
    public BtpDevisVersionDto nouvelleVersion(Long projetId, String commentaire) {
        BtpDevisDto devis = findDevis(projetId);
        int numero = versionRepository.findByProjetIdOrderByNumeroVersionDesc(projetId).stream()
                        .map(BtpDevisVersion::getNumeroVersion)
                        .findFirst()
                        .orElse(0)
                + 1;
        BtpDevisVersion version = new BtpDevisVersion();
        version.setProjetId(projetId);
        version.setNumeroVersion(numero);
        version.setDateVersion(LocalDate.now());
        version.setStatut(devis.getProjet().getStatutDevis());
        version.setCommentaire(commentaire);
        version.setMontantTotalHt(devis.getTotalHt());
        try {
            version.setSnapshotJson(objectMapper.writeValueAsString(devis));
        } catch (Exception e) {
            throw new IllegalStateException("Impossible de serialiser le devis", e);
        }
        return versionMapper.modeleToDto(versionRepository.save(version));
    }

    @Override
    public List<BtpDevisVersionDto> findVersions(Long projetId) {
        return versionMapper.modeleToDto(versionRepository.findByProjetIdOrderByNumeroVersionDesc(projetId));
    }

    @Override
    public BtpProjetDto changerStatut(Long projetId, String statut) {
        if (statut == null || !STATUTS.contains(statut)) {
            throw new IllegalArgumentException("Statut de devis invalide : " + statut);
        }
        BtpProjet projet = getProjet(projetId);
        projet.setStatutDevis(statut);
        return projetMapper.modeleToDto(projetRepository.save(projet));
    }

    private static DecimalFormat formatMontant() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        return new DecimalFormat("#,##0", symbols);
    }

    private static DecimalFormat formatQuantite() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.##", symbols);
    }

    @Override
    public byte[] exportPdf(Long projetId) {
        BtpDevisDto devis = findDevis(projetId);
        DecimalFormat fm = formatMontant();
        DecimalFormat fq = formatQuantite();
        Font titre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
        Font gras = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 8);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            BtpProjetDto p = devis.getProjet();
            Paragraph t = new Paragraph(
                    p.getDevisTitre() == null ? "DEVIS QUANTITATIF ET ESTIMATIF" : p.getDevisTitre(), titre);
            t.setAlignment(Element.ALIGN_CENTER);
            document.add(t);
            document.add(new Paragraph(p.getNom() + (p.getVillePays() == null ? "" : " - " + p.getVillePays()), gras));
            document.add(new Paragraph("Client : " + (p.getClient() == null ? "" : p.getClient()), normal));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(new float[] {5f, 28f, 6f, 8f, 11f, 12f});
            table.setWidthPercentage(100);
            for (String h : new String[] {"N°", "Designation", "U", "Qte", "PU (FCFA)", "Montant HT"}) {
                cell(table, h, gras, Element.ALIGN_CENTER);
            }
            for (BtpDevisDto.Lot lot : devis.getLots()) {
                if (lot.getPostes().isEmpty()) {
                    continue;
                }
                cellSpan(table, lot.getCodeLot() + " - " + lot.getNomLot(), gras);
                int n = 1;
                for (BtpDevisDto.Poste poste : lot.getPostes()) {
                    cellSpan(table, poste.getNomPoste(), gras);
                    for (BtpLigneDevisDto l : poste.getLignes()) {
                        String des =
                                (l.getSousEnsemble() == null ? "" : l.getSousEnsemble() + " ") + l.getDesignation();
                        cell(table, String.valueOf(n++), normal, Element.ALIGN_CENTER);
                        cell(table, des, normal, Element.ALIGN_LEFT);
                        cell(table, l.getUnite(), normal, Element.ALIGN_CENTER);
                        cell(table, fq.format(BtpMontants.nz(l.getQuantite())), normal, Element.ALIGN_RIGHT);
                        cell(table, fm.format(BtpMontants.nz(l.getPrixUnitaire())), normal, Element.ALIGN_RIGHT);
                        cell(table, fm.format(BtpMontants.nz(l.getMontantHt())), normal, Element.ALIGN_RIGHT);
                    }
                    cellSpan(
                            table,
                            "TOTAL " + poste.getNomPoste() + " : " + fm.format(poste.getTotalHt()) + " FCFA",
                            gras);
                }
                cellSpan(table, "TOTAL " + lot.getNomLot() + " : " + fm.format(lot.getTotalHt()) + " FCFA", gras);
            }
            cellSpan(table, "TOTAL GENERAL HT : " + fm.format(devis.getTotalHt()) + " FCFA", gras);
            if (devis.getRemise().signum() > 0) {
                cellSpan(table, "REMISE : " + fm.format(devis.getRemise()) + " FCFA", gras);
                cellSpan(table, "TOTAL APRES REMISE : " + fm.format(devis.getTotalApresRemise()) + " FCFA", gras);
            }
            if (devis.getMontantTva().signum() > 0) {
                cellSpan(table, "TVA : " + fm.format(devis.getMontantTva()) + " FCFA", gras);
                cellSpan(table, "TOTAL TTC : " + fm.format(devis.getTotalTtc()) + " FCFA", gras);
            }
            document.add(table);
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la generation du PDF du devis", e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
        return out.toByteArray();
    }

    private static void cell(PdfPTable table, String text, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(text == null ? "" : text, font));
        c.setHorizontalAlignment(align);
        c.setPadding(3);
        table.addCell(c);
    }

    private static void cellSpan(PdfPTable table, String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setColspan(6);
        c.setPadding(3);
        c.setBackgroundColor(new java.awt.Color(235, 235, 235));
        table.addCell(c);
    }

    /** Excel au format tableau HTML (ouvert nativement par Excel), sans dependance supplementaire. */
    @Override
    public byte[] exportExcel(Long projetId) {
        BtpDevisDto devis = findDevis(projetId);
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta charset=\"UTF-8\"></head><body><table border=\"1\">");
        sb.append("<tr><th colspan=\"6\">")
                .append(HtmlUtils.htmlEscape(devis.getProjet().getNom()))
                .append("</th></tr>");
        sb.append("<tr><th>N°</th><th>Designation</th><th>U</th><th>Quantite</th><th>PU</th><th>Montant HT</th></tr>");
        for (BtpDevisDto.Lot lot : devis.getLots()) {
            sb.append("<tr><td colspan=\"5\"><b>")
                    .append(HtmlUtils.htmlEscape(lot.getCodeLot() + " - " + lot.getNomLot()))
                    .append("</b></td><td>")
                    .append(lot.getTotalHt().toPlainString())
                    .append("</td></tr>");
            for (BtpDevisDto.Poste poste : lot.getPostes()) {
                sb.append("<tr><td colspan=\"5\"><b>")
                        .append(HtmlUtils.htmlEscape(poste.getNomPoste()))
                        .append("</b></td><td>")
                        .append(poste.getTotalHt().toPlainString())
                        .append("</td></tr>");
                int n = 1;
                for (BtpLigneDevisDto l : poste.getLignes()) {
                    sb.append("<tr><td>")
                            .append(n++)
                            .append("</td><td>")
                            .append(HtmlUtils.htmlEscape((l.getSousEnsemble() == null ? "" : l.getSousEnsemble() + " ")
                                    + l.getDesignation()))
                            .append("</td><td>")
                            .append(HtmlUtils.htmlEscape(l.getUnite()))
                            .append("</td><td>")
                            .append(BtpMontants.nz(l.getQuantite()).toPlainString())
                            .append("</td><td>")
                            .append(BtpMontants.nz(l.getPrixUnitaire()).toPlainString())
                            .append("</td><td>")
                            .append(BtpMontants.nz(l.getMontantHt()).toPlainString())
                            .append("</td></tr>");
                }
            }
        }
        sb.append("<tr><td colspan=\"5\"><b>TOTAL GENERAL HT</b></td><td>")
                .append(devis.getTotalHt().toPlainString())
                .append("</td></tr>");
        sb.append("<tr><td colspan=\"5\"><b>REMISE</b></td><td>")
                .append(devis.getRemise().toPlainString())
                .append("</td></tr>");
        sb.append("<tr><td colspan=\"5\"><b>TOTAL APRES REMISE</b></td><td>")
                .append(devis.getTotalApresRemise().toPlainString())
                .append("</td></tr>");
        sb.append("<tr><td colspan=\"5\"><b>TVA</b></td><td>")
                .append(devis.getMontantTva().toPlainString())
                .append("</td></tr>");
        sb.append("<tr><td colspan=\"5\"><b>TOTAL TTC</b></td><td>")
                .append(devis.getTotalTtc().toPlainString())
                .append("</td></tr>");
        sb.append("</table></body></html>");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
