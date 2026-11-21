package com.wscicloud.service.immobilier.controller;

import java.math.BigDecimal;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureCompletDto;
import com.wscicloud.service.immobilier.domain.dto.BtpPaiementDto;
import com.wscicloud.service.immobilier.service.BtpFactureService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/facture")
@Setter
@Getter
public class BtpFactureController {

    @Autowired
    private BtpFactureService factureService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpFactureCompletDto> creer(@Valid @RequestBody BtpFactureCompletDto dto) {
        return ResponseEntity.ok(factureService.creer(dto));
    }

    @PostMapping(value = "/situation/{situationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpFactureCompletDto> creerDepuisSituation(
            @PathVariable Long situationId,
            @RequestParam String numero,
            @RequestParam(required = false, defaultValue = "0") BigDecimal tauxTva) {
        return ResponseEntity.ok(factureService.creerDepuisSituation(situationId, numero, tauxTva));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpFactureCompletDto> lire(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.lire(id));
    }

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listerParProjet(@PathVariable Long projetId) {
        return ResponseEntity.ok(factureService.listerParProjet(projetId));
    }

    @PostMapping(
            value = "/{id}/paiement",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpFactureCompletDto> enregistrerPaiement(
            @PathVariable Long id, @Valid @RequestBody BtpPaiementDto paiement) {
        return ResponseEntity.ok(factureService.enregistrerPaiement(id, paiement));
    }

    @DeleteMapping(value = "/paiement/{paiementId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpFactureCompletDto> supprimerPaiement(@PathVariable Long paiementId) {
        return ResponseEntity.ok(factureService.supprimerPaiement(paiementId));
    }

    @GetMapping(value = "/{id}/reste-a-payer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> resteAPayer(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.resteAPayer(id));
    }

    @PutMapping(value = "/{id}/annuler", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpFactureCompletDto> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.annuler(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        factureService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
