package com.wscicloud.service.immobilier.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wscicloud.service.immobilier.domain.dto.PersonneDto;
import com.wscicloud.service.immobilier.domain.repository.SysPersonneRepository;

@RestController
@RequestMapping("btp/personne")
public class BtpPersonneController {

    private static final int MAX_RESULTATS = 20;

    @Autowired
    private SysPersonneRepository personneRepository;

    /** Recherche par nom, prenoms ou login dans les personnes de la societe. */
    @GetMapping(value = "/search/{societeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public ResponseEntity<List<PersonneDto>> rechercher(
            @PathVariable Long societeId, @RequestParam(required = false, defaultValue = "") String q) {
        List<PersonneDto> result = personneRepository
                .rechercher(societeId, q.trim(), PageRequest.of(0, MAX_RESULTATS))
                .stream()
                .map(p -> new PersonneDto(p.getId(), p.getLogin(), p.getNom(), p.getPrenoms(), p.getLibelle()))
                .toList();
        return ResponseEntity.ok(result);
    }
}
