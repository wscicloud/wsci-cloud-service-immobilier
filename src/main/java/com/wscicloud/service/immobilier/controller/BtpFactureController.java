package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wscicloud.service.immobilier.domain.dto.BtpFactureCompletDto;
import com.wscicloud.service.immobilier.domain.dto.BtpFacturePaiementDto;
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

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity save(@Valid @RequestBody BtpFactureCompletDto dto) {
        return ResponseEntity.ok().body(factureService.save(dto));
    }

    @PostMapping(value = "/situation/{situationId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity genererDepuisSituation(
            @PathVariable("situationId") Long situationId, @RequestParam("numero") String numero) {
        return ResponseEntity.ok().body(factureService.genererDepuisSituation(situationId, numero));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(factureService.findById(id));
    }

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findByProjet(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(factureService.findByProjet(projetId));
    }

    @PostMapping(
            value = "/{id}/paiement",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity ajouterPaiement(@PathVariable("id") Long id, @Valid @RequestBody BtpFacturePaiementDto dto) {
        return ResponseEntity.ok().body(factureService.ajouterPaiement(id, dto));
    }

    @DeleteMapping("/paiement/{paiementId}")
    public ResponseEntity supprimerPaiement(@PathVariable("paiementId") Long paiementId) {
        return ResponseEntity.ok().body(factureService.supprimerPaiement(paiementId));
    }

    @GetMapping(value = "/{id}/reste-a-payer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity resteAPayer(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(factureService.resteAPayer(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        factureService.delete(id);
        return ResponseEntity.ok().build();
    }
}
