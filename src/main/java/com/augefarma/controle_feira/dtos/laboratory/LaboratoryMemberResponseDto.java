package com.augefarma.controle_feira.dtos.laboratory;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class LaboratoryMemberResponseDto {

    private Long id;

    private String name;

    private String cpf;

    private String laboratory;

    public LaboratoryMemberResponseDto(LaboratoryMemberEntity laboratoryMember) {
        id = laboratoryMember.getId();
        name = laboratoryMember.getName();
        cpf = laboratoryMember.getCpf();
        laboratory = laboratoryMember.getLaboratory().getCorporateReason();
    }
}
