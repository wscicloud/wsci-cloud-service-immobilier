package com.wscicloud.service.immobilier.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wscicloud.service.immobilier.domain.dto.BtpProjetDto;
import com.wscicloud.service.immobilier.domain.entities.BtpProjet;
import com.wscicloud.service.immobilier.domain.mapper.BtpProjetMapper;
import com.wscicloud.service.immobilier.domain.repository.BtpProjetRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@Slf4j
public class BtpProjetServiceImpl implements BtpProjetService {

    static final String STATUT_DEFAUT = "EN_COURS";
    static final String DEVISE_DEFAUT = "FCFA";

    @Autowired
    private BtpProjetRepository projetRepository;

    @Autowired
    private BtpProjetMapper projetMapper;

    @Override
    public BtpProjetDto creer(BtpProjetDto dto) {
        if (projetRepository.existsByCodeProjet(dto.getCodeProjet())) {
            throw new IllegalArgumentException("Le code projet existe deja : " + dto.getCodeProjet());
        }
        BtpProjet projet = projetMapper.dtoToModele(dto);
        projet.setId(null);
        projet.setCreeLe(OffsetDateTime.now());
        if (projet.getStatut() == null) {
            projet.setStatut(STATUT_DEFAUT);
        }
        if (projet.getDevise() == null) {
            projet.setDevise(DEVISE_DEFAUT);
        }
        projet.setBudgetPrevuHt(nz(projet.getBudgetPrevuHt()));
        projet.setBudgetPrevuTtc(nz(projet.getBudgetPrevuTtc()));
        projet.setBudgetReelHt(nz(projet.getBudgetReelHt()));
        projet.setBudgetReelTtc(nz(projet.getBudgetReelTtc()));
        projet.setMargePrevuTtc(nz(projet.getMargePrevuTtc()));
        projet.setMargeReelTtc(nz(projet.getMargeReelTtc()));
        return projetMapper.modeleToDto(projetRepository.save(projet));
    }

    @Override
    public BtpProjetDto modifier(BtpProjetDto dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("L'identifiant du projet est obligatoire");
        }
        BtpProjet existant = trouver(dto.getId());
        BtpProjet projet = projetMapper.dtoToModele(dto);
        projet.setCreeLe(existant.getCreeLe());
        if (projet.getStatut() == null) {
            projet.setStatut(existant.getStatut());
        }
        if (projet.getDevise() == null) {
            projet.setDevise(existant.getDevise());
        }
        return projetMapper.modeleToDto(projetRepository.save(projet));
    }

    @Override
    public BtpProjetDto lire(Long id) {
        return projetMapper.modeleToDto(trouver(id));
    }

    @Override
    public List<BtpProjetDto> lister(Long societeId) {
        return projetMapper.modeleToDto(projetRepository.findAllBySocieteIdOrderByCreeLeDesc(societeId));
    }

    @Override
    public List<BtpProjetDto> rechercher(Long societeId, String q) {
        if (q == null || q.isBlank()) {
            return lister(societeId);
        }
        return projetMapper.modeleToDto(projetRepository.rechercher(societeId, q.trim()));
    }

    @Override
    public void supprimer(Long id) {
        projetRepository.delete(trouver(id));
    }

    private BtpProjet trouver(Long id) {
        return projetRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Projet introuvable : " + id));
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
