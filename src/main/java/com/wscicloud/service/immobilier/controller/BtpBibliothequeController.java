package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wscicloud.service.immobilier.domain.dto.RefmBtpTacheBiblioDto;
import com.wscicloud.service.immobilier.service.BtpBibliothequeService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/bibliotheque")
@Setter
@Getter
public class BtpBibliothequeController {

    @Autowired
    private BtpBibliothequeService bibliothequeService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity save(@Valid @RequestBody RefmBtpTacheBiblioDto dto) {
        return ResponseEntity.ok().body(bibliothequeService.save(dto));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(bibliothequeService.findById(id));
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findAll() {
        return ResponseEntity.ok().body(bibliothequeService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        bibliothequeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
