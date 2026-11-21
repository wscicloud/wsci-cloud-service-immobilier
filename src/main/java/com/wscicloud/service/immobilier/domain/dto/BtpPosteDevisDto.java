package com.wscicloud.service.immobilier.domain.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class BtpPosteDevisDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull private Long lotId;

    @NotBlank private String nomPoste;

    private Integer ordre;
}
