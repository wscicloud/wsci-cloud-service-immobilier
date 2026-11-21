package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity create(@Valid @RequestBody BtpProjetDto dto) {
        return ResponseEntity.ok().body(projetService.create(dto));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity update(@Valid @RequestBody BtpProjetDto dto) {
        return ResponseEntity.ok().body(projetService.update(dto));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(projetService.findById(id));
    }

    @GetMapping(value = "/all/{societeId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findAllBySocieteId(@PathVariable("societeId") Long societeId) {
        return ResponseEntity.ok().body(projetService.findAllBySocieteId(societeId));
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findAll() {
        return ResponseEntity.ok().body(projetService.findAll());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        projetService.delete(id);
        return ResponseEntity.ok().build();
    }
}
