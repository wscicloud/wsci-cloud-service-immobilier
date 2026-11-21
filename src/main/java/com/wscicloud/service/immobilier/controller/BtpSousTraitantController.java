package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wscicloud.service.immobilier.domain.dto.BtpSoustraitantDto;
import com.wscicloud.service.immobilier.service.BtpSousTraitantService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/soustraitant")
@Setter
@Getter
public class BtpSousTraitantController {

    @Autowired
    private BtpSousTraitantService sousTraitantService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity save(@Valid @RequestBody BtpSoustraitantDto dto) {
        return ResponseEntity.ok().body(sousTraitantService.save(dto));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(sousTraitantService.findById(id));
    }

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findByProjet(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(sousTraitantService.findByProjet(projetId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        sousTraitantService.delete(id);
        return ResponseEntity.ok().build();
    }
}
