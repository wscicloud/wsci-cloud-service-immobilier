package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class RefmBtpTacheDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long posteId;

    private String code;

    private String libelle;

    private String uniteDefaut;
}
