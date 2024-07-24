package com.augefarma.controle_feira.dtos.laboratory;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class LaboratoryResponseDto {

    private Long id;

    private String name;

    private String cpf;

    private String corporateReason;

    public LaboratoryResponseDto(LaboratoryEntity laboratory) {
        id = laboratory.getId();
        name = laboratory.getName();
        cpf = laboratory.getCpf();
        corporateReason = laboratory.getCorporateReason();
    }
}
