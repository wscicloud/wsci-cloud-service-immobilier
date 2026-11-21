package com.wscicloud.service.immobilier.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationLigneDto;
import com.wscicloud.service.immobilier.service.BtpSituationService;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/situation")
@Setter
@Getter
public class BtpSituationController {

    @Autowired
    private BtpSituationService situationService;

    @PostMapping(value = "/generer/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity generer(
            @PathVariable("projetId") Long projetId,
            @RequestParam("mois") Integer mois,
            @RequestParam("annee") Integer annee) {
        return ResponseEntity.ok().body(situationService.generer(projetId, mois, annee));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(situationService.findById(id));
    }

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findByProjet(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(situationService.findByProjet(projetId));
    }

    @PutMapping(
            value = "/{id}/avancement",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saisirAvancement(
            @PathVariable("id") Long id, @RequestBody List<BtpSituationLigneDto> lignes) {
        return ResponseEntity.ok().body(situationService.saisirAvancement(id, lignes));
    }

    @PostMapping(value = "/{id}/recalculer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity recalculer(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(situationService.recalculer(id));
    }

    @PutMapping(value = "/{id}/valider", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity valider(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(situationService.valider(id));
    }

    @PutMapping(value = "/{id}/facturee", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity marquerFacturee(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(situationService.marquerFacturee(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        situationService.delete(id);
        return ResponseEntity.ok().build();
    }
}
