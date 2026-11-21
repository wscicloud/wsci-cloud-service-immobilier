package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wscicloud.service.immobilier.domain.dto.BtpSituationCompleteDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationLigneDto;
import com.wscicloud.service.immobilier.domain.dto.BtpSituationTravauxDto;
import com.wscicloud.service.immobilier.service.BtpSituationService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/situation")
@Setter
@Getter
public class BtpSituationController {

    @Autowired
    private BtpSituationService situationService;

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listerParProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(situationService.listerParProjet(projetId));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpSituationCompleteDto> lire(@PathVariable Long id) {
        return ResponseEntity.ok(situationService.lire(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpSituationTravauxDto> creer(@Valid @RequestBody BtpSituationTravauxDto dto) {
        return ResponseEntity.ok(situationService.creer(dto));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpSituationTravauxDto> modifier(@Valid @RequestBody BtpSituationTravauxDto dto) {
        return ResponseEntity.ok(situationService.modifier(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        situationService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/statut/{statut}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpSituationTravauxDto> changerStatut(@PathVariable Long id, @PathVariable String statut) {
        return ResponseEntity.ok(situationService.changerStatut(id, statut));
    }

    @PostMapping(
            value = "/{id}/lignes",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpSituationCompleteDto> ajouterLigne(
            @PathVariable Long id, @Valid @RequestBody BtpSituationLigneDto ligne) {
        return ResponseEntity.ok(situationService.ajouterLigne(id, ligne));
    }

    @PutMapping(
            value = "/{id}/lignes/{ligneId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpSituationCompleteDto> modifierLigne(
            @PathVariable Long id, @PathVariable Long ligneId, @Valid @RequestBody BtpSituationLigneDto ligne) {
        return ResponseEntity.ok(situationService.modifierLigne(id, ligneId, ligne));
    }

    @DeleteMapping(value = "/{id}/lignes/{ligneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpSituationCompleteDto> supprimerLigne(@PathVariable Long id, @PathVariable Long ligneId) {
        return ResponseEntity.ok(situationService.supprimerLigne(id, ligneId));
    }
}
