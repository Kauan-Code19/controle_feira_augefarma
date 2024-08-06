package com.augefarma.controle_feira.dtos.real_time;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class EntitiesListResponseDto {

    private List<PharmacyRepresentativeResponseDto> pharmacyRepresentatives = new ArrayList<>();
    private List<LaboratoryMemberResponseDto> laboratoryMembers = new ArrayList<>();

    public EntitiesListResponseDto(List<PharmacyRepresentativeResponseDto> pharmacyRepresentatives,
                                   List<LaboratoryMemberResponseDto> laboratoryMembers) {
        this.pharmacyRepresentatives = pharmacyRepresentatives;
        this.laboratoryMembers = laboratoryMembers;
    }

    public void addPharmacyRepresentative(PharmacyRepresentativeResponseDto pharmacyRepresentative) {
        pharmacyRepresentatives.add(pharmacyRepresentative);
    }

    public void removePharmacyRepresentative(PharmacyRepresentativeResponseDto pharmacyRepresentative) {
        pharmacyRepresentatives.remove(pharmacyRepresentative);
    }

    public void addLaboratoryMember(LaboratoryMemberResponseDto laboratoryMember) {
        laboratoryMembers.add(laboratoryMember);
    }

    public void removeLaboratoryMember(LaboratoryMemberResponseDto laboratoryMember) {
        laboratoryMembers.remove(laboratoryMember);
    }
}
