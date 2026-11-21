package com.wscicloud.service.immobilier.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.AvancementLot;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.ResteAFacturer;
import com.wscicloud.service.immobilier.domain.dto.BtpDashboardDto.SoldeClient;
import com.wscicloud.service.immobilier.domain.entities.BtpDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpFacture;
import com.wscicloud.service.immobilier.domain.entities.BtpLigneDevis;
import com.wscicloud.service.immobilier.domain.entities.BtpPaiement;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationLigne;
import com.wscicloud.service.immobilier.domain.entities.BtpSituationTravaux;
import com.wscicloud.service.immobilier.domain.entities.RefmBtpLot;
import com.wscicloud.service.immobilier.domain.repository.BtpDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpFactureRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpLigneDevisRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpPaiementRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationLigneRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpSituationTravauxRepository;
import com.wscicloud.service.immobilier.domain.repository.RefmBtpLotRepository;
import com.wscicloud.service.immobilier.service.calcul.BtpDashboardCalculator;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class BtpDashboardServiceImpl implements BtpDashboardService {

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpDevisRepository devisRepository;

    @Autowired
    private BtpLigneDevisRepository ligneDevisRepository;

    @Autowired
    private BtpSituationTravauxRepository situationRepository;

    @Autowired
    private BtpSituationLigneRepository situationLigneRepository;

    @Autowired
    private BtpFactureRepository factureRepository;

    @Autowired
    private BtpPaiementRepository paiementRepository;

    @Autowired
    private RefmBtpLotRepository lotRepository;

    @Override
    public BtpDashboardDto tableauDeBord(Long projetId) {
        BtpDashboardDto dto = new BtpDashboardDto();
        dto.setProjetId(projetId);
        dto.setAvancementParLot(avancementParLot(projetId));
        dto.setResteAFacturer(resteAFacturer(projetId));
        dto.setSoldeClient(soldeClient(projetId));
        return dto;
    }

    @Override
    public List<AvancementLot> avancementParLot(Long projetId) {
        verifierProjet(projetId);
        List<BtpDevis> devis = devisRepository.findByProjetIdOrderByDateDevisDescIdDesc(projetId);
        List<BtpLigneDevis> lignesDevis = devis.isEmpty()
                ? List.of()
                : ligneDevisRepository.findByDevisIdIn(
                        devis.stream().map(BtpDevis::getId).toList());
        List<BtpSituationTravaux> situations = situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(projetId);
        var lignesSituations = situations.isEmpty()
                ? List.<BtpSituationLigne>of()
                : situationLigneRepository.findBySituationIdIn(
                        situations.stream().map(BtpSituationTravaux::getId).toList());

        List<AvancementLot> lots =
                BtpDashboardCalculator.avancementParLot(devis, lignesDevis, situations, lignesSituations);
        Map<Long, RefmBtpLot> referentiel =
                lotRepository.findAll().stream().collect(Collectors.toMap(RefmBtpLot::getId, Function.identity()));
        lots.forEach(l -> {
            RefmBtpLot lot = referentiel.get(l.getLotId());
            if (lot != null) {
                l.setLotCode(lot.getCode());
                l.setLotLibelle(lot.getLibelle());
            }
        });
        return lots;
    }

    @Override
    public ResteAFacturer resteAFacturer(Long projetId) {
        verifierProjet(projetId);
        return BtpDashboardCalculator.resteAFacturer(
                projetId,
                devisRepository.findByProjetIdOrderByDateDevisDescIdDesc(projetId),
                situationRepository.findByProjetIdOrderByDateSituationAscIdAsc(projetId),
                factureRepository.findByProjetIdOrderByDateEmissionDescIdDesc(projetId));
    }

    @Override
    public SoldeClient soldeClient(Long projetId) {
        verifierProjet(projetId);
        List<BtpFacture> factures = factureRepository.findByProjetIdOrderByDateEmissionDescIdDesc(projetId);
        var paiements = factures.isEmpty()
                ? List.<BtpPaiement>of()
                : paiementRepository.findByFactureIdIn(
                        factures.stream().map(BtpFacture::getId).toList());
        return BtpDashboardCalculator.soldeClient(projetId, factures, paiements);
    }

    private void verifierProjet(Long projetId) {
        if (projetId == null || !projetRepository.existsById(projetId)) {
            throw new IllegalArgumentException("Projet introuvable : " + projetId);
        }
    }
}
