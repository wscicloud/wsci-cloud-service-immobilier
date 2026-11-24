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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;
import com.wscicloud.service.immobilier.service.BtpProjetService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/projet")
@Setter
@Getter
public class BtpProjetController {

    @Autowired
    private BtpProjetService projetService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpProjetDto> creer(@Valid @RequestBody BtpProjetDto dto) {
        return ResponseEntity.ok(projetService.creer(dto));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpProjetDto> modifier(@Valid @RequestBody BtpProjetDto dto) {
        return ResponseEntity.ok(projetService.modifier(dto));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpProjetDto> lire(@PathVariable Long id) {
        return ResponseEntity.ok(projetService.lire(id));
    }

    @GetMapping(value = "/all/{societeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> lister(@PathVariable Long societeId) {
        return ResponseEntity.ok(projetService.lister(societeId));
    }

    @GetMapping(value = "/search/{societeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rechercher(@PathVariable Long societeId, @RequestParam(required = false) String q) {
        return ResponseEntity.ok(projetService.rechercher(societeId, q));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        projetService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
