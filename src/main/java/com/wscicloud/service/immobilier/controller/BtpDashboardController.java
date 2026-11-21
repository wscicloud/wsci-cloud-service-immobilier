package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wscicloud.service.immobilier.service.BtpDashboardService;

import lombok.Getter;
import lombok.Setter;

@RestController
@RequestMapping("btp/dashboard")
@Setter
@Getter
public class BtpDashboardController {

    @Autowired
    private BtpDashboardService dashboardService;

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findByProjet(@PathVariable("projetId") Long projetId) {
        return ResponseEntity.ok().body(dashboardService.findByProjet(projetId));
    }

    @GetMapping(value = "/all/{societeId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity findBySociete(@PathVariable("societeId") Long societeId) {
        return ResponseEntity.ok().body(dashboardService.findBySociete(societeId));
    }
}
