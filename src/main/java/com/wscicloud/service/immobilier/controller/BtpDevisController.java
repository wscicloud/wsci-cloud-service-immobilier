package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLotDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpPosteDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSousDetailPrixDto;
import com.wscicloud.service.immobilier.service.BtpDevisService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/devis")
@Setter
@Getter
public class BtpDevisController {

    @Autowired
    private BtpDevisService devisService;

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findDevis(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(devisService.findDevis(projetId));
    }

    @PostMapping(value = "/projet/{projetId}/recalculer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity recalculer(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(devisService.recalculer(projetId));
    }

    @PostMapping(
            value = "/lot",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveLot(@Valid @RequestBody BtpLotDevisDto dto) {
        return ResponseEntity.ok().body(devisService.saveLot(dto));
    }

    @DeleteMapping("/lot/{id}")
    public ResponseEntity deleteLot(@PathVariable("id") Long id) {
        devisService.deleteLot(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(
            value = "/poste",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity savePoste(@Valid @RequestBody BtpPosteDevisDto dto) {
        return ResponseEntity.ok().body(devisService.savePoste(dto));
    }

    @DeleteMapping("/poste/{id}")
    public ResponseEntity deletePoste(@PathVariable("id") Long id) {
        devisService.deletePoste(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(
            value = "/ligne",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveLigne(@Valid @RequestBody BtpLigneDevisDto dto) {
        return ResponseEntity.ok().body(devisService.saveLigne(dto));
    }

    @DeleteMapping("/ligne/{id}")
    public ResponseEntity deleteLigne(@PathVariable("id") Long id) {
        devisService.deleteLigne(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(
            value = "/sous-detail",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveSousDetail(@Valid @RequestBody BtpSousDetailPrixDto dto) {
        return ResponseEntity.ok().body(devisService.saveSousDetail(dto));
    }

    @GetMapping(value = "/sous-detail/ligne/{ligneId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findSousDetail(@PathVariable("ligneId") Long ligneId) {
        return ResponseEntity.ok().body(devisService.findSousDetail(ligneId));
    }

    @PostMapping(value = "/projet/{projetId}/dupliquer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity dupliquer(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(devisService.dupliquer(projetId));
    }

    @PostMapping(value = "/projet/{projetId}/version", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity nouvelleVersion(
            @PathVariable("projetId") Long projetId,
            @RequestParam(value = "commentaire", required = false) String commentaire) {
        return ResponseEntity.ok().body(devisService.nouvelleVersion(projetId, commentaire));
    }

    @GetMapping(value = "/projet/{projetId}/versions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findVersions(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(devisService.findVersions(projetId));
    }

    @PutMapping(value = "/projet/{projetId}/statut/{statut}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity changerStatut(
            @PathVariable("projetId") Long projetId, @PathVariable("statut") String statut) {
        return ResponseEntity.ok().body(devisService.changerStatut(projetId, statut));
    }

    @GetMapping(value = "/projet/{projetId}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=devis-" + projetId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(devisService.exportPdf(projetId));
    }

    @GetMapping(value = "/projet/{projetId}/export/excel")
    public ResponseEntity<byte[]> exportExcel(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=devis-" + projetId + ".xls")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(devisService.exportExcel(projetId));
    }
}
