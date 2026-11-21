package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Situation de travaux avec ses lignes. */
@Getter
@Setter
@NoArgsConstructor
public class BtpSituationCompleteDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private BtpSituationTravauxDto situation;

    private List<BtpSituationLigneDto> lignes = new ArrayList<>();
}
