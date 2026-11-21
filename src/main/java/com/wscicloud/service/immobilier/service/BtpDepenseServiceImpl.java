package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpDepenseReelleDto;
import com.wscicloud.service.immobilier.domain.dto.BtpDevisDto;
import com.wscicloud.service.immobilier.domain.dto.BtpEcartDto;
import com.wscicloud.service.immobilier.domain.entities.BtpDepenseReelle;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;
import com.wscicloud.service.immobilier.domain.mapper.BtpDepenseReelleMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpDepenseReelleRepository;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;
import com.wscicloud.service.immobilier.service.calcul.BtpMontants;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Getter
@Setter
@Service
@Slf4j
public class BtpDepenseServiceImpl implements BtpDepenseService {

    @Autowired
    private BtpDepenseReelleRepository depenseRepository;

    @Autowired
    private BtpDepenseReelleMapper depenseMapper;

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpDevisService devisService;

    @Override
    public BtpDepenseReelleDto save(BtpDepenseReelleDto dto) {
        dto.setMontantHt(BtpMontants.montant(dto.getMontantHt()));
        dto.setTva(BtpMontants.montant(dto.getTva()));
        dto.setMontantTtc(BtpMontants.montant(dto.getMontantHt().add(dto.getTva())));
        if (dto.getSocieteId() == null) {
            projetRepository.findById(dto.getProjetId()).ifPresent(p -> dto.setSocieteId(p.getSocieteId()));
        }
        return depenseMapper.modeleToDto(depenseRepository.save(depenseMapper.dtoToModele(dto)));
    }

    @Override
    public BtpDepenseReelleDto findById(Long id) {
        return depenseRepository.findById(id).map(depenseMapper::modeleToDto).orElse(null);
    }

    @Override
    public List<BtpDepenseReelleDto> findByProjet(Long projetId) {
        return depenseMapper.modeleToDto(depenseRepository.findByProjetIdOrderByDateDepenseDesc(projetId));
    }

    @Override
    public List<BtpDepenseReelleDto> findBySociete(Long societeId) {
        return depenseMapper.modeleToDto(depenseRepository.findBySocieteId(societeId));
    }

    @Override
    public void delete(Long id) {
        depenseRepository.deleteById(id);
    }

    @Override
    public BtpEcartDto calculerEcart(Long projetId) {
        BtpProjet projet = projetRepository
                .findById(projetId)
                .orElseThrow(() -> new IllegalArgumentException("Projet BTP introuvable : " + projetId));
        BtpDevisDto devis = devisService.findDevis(projetId);
        List<BtpDepenseReelle> depenses = depenseRepository.findByProjetIdOrderByDateDepenseDesc(projetId);

        BtpEcartDto ecart = new BtpEcartDto();
        ecart.setProjetId(projetId);
        ecart.setBudgetHt(devis.getTotalApresRemise());

        Map<String, BigDecimal> parCategorie = new LinkedHashMap<>();
        BigDecimal realise = BigDecimal.ZERO;
        for (BtpDepenseReelle d : depenses) {
            realise = realise.add(BtpMontants.nz(d.getMontantHt()));
            parCategorie.merge(d.getCategorie(), BtpMontants.nz(d.getMontantHt()), BigDecimal::add);
        }
        List<BtpEcartDto.Categorie> categories = new ArrayList<>();
        parCategorie.forEach((cat, montant) -> {
            BtpEcartDto.Categorie c = new BtpEcartDto.Categorie();
            c.setCategorie(cat);
            c.setMontantHt(BtpMontants.montant(montant));
            categories.add(c);
        });
        ecart.setParCategorie(categories);
        appliquerMarge(ecart, realise, projet.getMargeBruteMinAcceptable());
        return ecart;
    }

    /** ecart = budget - realise ; marge brute % = ecart / budget ; alerte si inferieure au minimum accepte. */
    static void appliquerMarge(BtpEcartDto ecart, BigDecimal realise, BigDecimal margeMin) {
        ecart.setRealiseHt(BtpMontants.montant(realise));
        ecart.setEcart(BtpMontants.montant(ecart.getBudgetHt().subtract(ecart.getRealiseHt())));
        ecart.setMargeBrute(ecart.getEcart());
        ecart.setMargeBrutePct(BtpMontants.ratio(ecart.getEcart(), ecart.getBudgetHt()));
        ecart.setMargeBruteMinAcceptable(BtpMontants.taux(margeMin));
        ecart.setAlerteMarge(ecart.getBudgetHt().signum() > 0
                && ecart.getMargeBrutePct().compareTo(margeMin == null ? BigDecimal.ZERO : margeMin) < 0);
    }
}
