package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wscicloud.service.immobilier.domain.dto.BtpDepenseReelleDto;
import com.wscicloud.service.immobilier.service.BtpDepenseService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/depense")
@Setter
@Getter
public class BtpDepenseController {

    @Autowired
    private BtpDepenseService depenseService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity save(@Valid @RequestBody BtpDepenseReelleDto dto) {
        return ResponseEntity.ok().body(depenseService.save(dto));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(depenseService.findById(id));
    }

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findByProjet(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(depenseService.findByProjet(projetId));
    }

    @GetMapping(value = "/all/{societeId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findBySociete(@PathVariable("societeId") Long societeId) {
        return ResponseEntity.ok().body(depenseService.findBySociete(societeId));
    }

    @GetMapping(value = "/ecart/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity calculerEcart(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(depenseService.calculerEcart(projetId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        depenseService.delete(id);
        return ResponseEntity.ok().build();
    }
}
