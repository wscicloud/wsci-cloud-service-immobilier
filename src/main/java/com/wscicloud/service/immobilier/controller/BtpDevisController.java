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

import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpLigneDevisDto;
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

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listerParProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(devisService.listerParProjet(projetId));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpDevisDto> lire(@PathVariable Long id) {
        return ResponseEntity.ok(devisService.lire(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpDevisDto> creer(@Valid @RequestBody BtpDevisDto dto) {
        return ResponseEntity.ok(devisService.creer(dto));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpDevisDto> modifier(@Valid @RequestBody BtpDevisDto dto) {
        return ResponseEntity.ok(devisService.modifier(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        devisService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}/statut/{statut}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpDevisDto> changerStatut(@PathVariable Long id, @PathVariable String statut) {
        return ResponseEntity.ok(devisService.changerStatut(id, statut));
    }

    @PostMapping(
            value = "/{devisId}/lignes",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpDevisDto> ajouterLigne(
            @PathVariable Long devisId, @Valid @RequestBody BtpLigneDevisDto ligne) {
        return ResponseEntity.ok(devisService.ajouterLigne(devisId, ligne));
    }

    @PutMapping(
            value = "/{devisId}/lignes/{ligneId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpDevisDto> modifierLigne(
            @PathVariable Long devisId, @PathVariable Long ligneId, @Valid @RequestBody BtpLigneDevisDto ligne) {
        return ResponseEntity.ok(devisService.modifierLigne(devisId, ligneId, ligne));
    }

    @DeleteMapping(value = "/{devisId}/lignes/{ligneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpDevisDto> supprimerLigne(@PathVariable Long devisId, @PathVariable Long ligneId) {
        return ResponseEntity.ok(devisService.supprimerLigne(devisId, ligneId));
    }
}
