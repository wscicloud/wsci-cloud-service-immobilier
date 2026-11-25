package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wscicloud.service.immobilier.service.SysPersonneService;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/personne")
@Setter
@Getter
public class BtpPersonneController {

    @Autowired
    private SysPersonneService personneService;

    /** Recherche par nom, prenoms ou login dans les personnes de la societe. */
    @GetMapping(value = "/search/{societeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> rechercher(@PathVariable Long societeId, @RequestParam(required = false) String q) {
        return ResponseEntity.ok(personneService.rechercher(societeId, q));
    }
}
