package com.augefarma.controle_feira.dtos.laboratory;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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
