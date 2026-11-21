package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wscicloud.service.immobilier.service.BtpReferentielService;

import lombok.Getter;
import lombok.Setter;

/** Referentiel BTP en lecture seule. */
@RestController
@RequestMapping("btp/referentiel")
@Setter
@Getter
public class BtpReferentielController {

    @Autowired
    private BtpReferentielService referentielService;

    @GetMapping(value = "/lots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listerLots() {
        return ResponseEntity.ok(referentielService.listerLots());
    }

    @GetMapping(value = "/lots/{lotId}/postes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listerPostes(@PathVariable Long lotId) {
        return ResponseEntity.ok(referentielService.listerPostes(lotId));
    }

    @GetMapping(value = "/postes/{posteId}/taches", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listerTaches(@PathVariable Long posteId) {
        return ResponseEntity.ok(referentielService.listerTaches(posteId));
    }

    @GetMapping(value = "/taches/{tacheId}/sous-taches", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listerSousTaches(@PathVariable Long tacheId) {
        return ResponseEntity.ok(referentielService.listerSousTaches(tacheId));
    }
}
