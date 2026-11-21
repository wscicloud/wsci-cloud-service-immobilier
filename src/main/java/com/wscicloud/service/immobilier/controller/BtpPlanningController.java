package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wscicloud.service.immobilier.domain.dto.BtpRapportJournalierDto;
import com.wscicloud.service.immobilier.domain.dto.BtpTachePlanningDto;
import com.wscicloud.service.immobilier.service.BtpPlanningService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/planning")
@Setter
@Getter
public class BtpPlanningController {

    @Autowired
    private BtpPlanningService planningService;

    @PostMapping(
            value = "/tache",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveTache(@Valid @RequestBody BtpTachePlanningDto dto) {
        return ResponseEntity.ok().body(planningService.saveTache(dto));
    }

    @GetMapping(value = "/tache/projet/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findTaches(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(planningService.findTaches(projetId));
    }

    @DeleteMapping("/tache/{id}")
    public ResponseEntity deleteTache(@PathVariable("id") Long id) {
        planningService.deleteTache(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(
            value = "/rapport",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveRapport(@Valid @RequestBody BtpRapportJournalierDto dto) {
        return ResponseEntity.ok().body(planningService.saveRapport(dto));
    }

    @GetMapping(value = "/rapport/projet/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findRapports(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(planningService.findRapports(projetId));
    }

    @DeleteMapping("/rapport/{id}")
    public ResponseEntity deleteRapport(@PathVariable("id") Long id) {
        planningService.deleteRapport(id);
        return ResponseEntity.ok().build();
    }
}
