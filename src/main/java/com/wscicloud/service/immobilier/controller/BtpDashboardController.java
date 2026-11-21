package com.wscicloud.service.immobilier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto;
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

    @GetMapping(value = "/projet/{projetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BtpDashboardDto> tableauDeBord(@PathVariable Long projetId) {
        return ResponseEntity.ok(dashboardService.tableauDeBord(projetId));
    }

    @GetMapping(value = "/projet/{projetId}/avancement-lots", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> avancementLots(@PathVariable Long projetId) {
        return ResponseEntity.ok(dashboardService.avancementParLot(projetId));
    }

    @GetMapping(value = "/projet/{projetId}/reste-a-facturer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resteAFacturer(@PathVariable Long projetId) {
        return ResponseEntity.ok(dashboardService.resteAFacturer(projetId));
    }

    @GetMapping(value = "/projet/{projetId}/solde-client", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> soldeClient(@PathVariable Long projetId) {
        return ResponseEntity.ok(dashboardService.soldeClient(projetId));
    }
}
