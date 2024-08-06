package com.augefarma.controle_feira.dtos.pharmacy_representative;

import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class PharmacyRepresentativeResponseDto {

    private Long id;

    private String name;

    private String cpf;

    private String cnpj;

    private String corporateReason;

    public PharmacyRepresentativeResponseDto(PharmacyRepresentativeEntity pharmacyRepresentative) {
        id = pharmacyRepresentative.getId();
        name = pharmacyRepresentative.getName();
        cpf = pharmacyRepresentative.getCpf();
        cnpj = pharmacyRepresentative.getCnpj();
        corporateReason = pharmacyRepresentative.getCorporateReason();
    }
}
