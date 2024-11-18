package com.augefarma.controle_feira.dtos.authorization;

import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class WristbandsResponseDto {
    private Long id;

    private String name;

    private String cpf;

    private List<String> checkInsFair;

    private String corporateReason;

    public WristbandsResponseDto(ParticipantEntity participant, List<String> checkInsFair) {
        id = participant.getId();
        name = participant.getName();
        cpf = participant.getCpf();
        this.checkInsFair = checkInsFair;
        corporateReason = participant.getSpecificInfoForWristbandResponse();
    }

    public WristbandsResponseDto(ParticipantEntity participant) {
        id = participant.getId();
        name = participant.getName();
        cpf = participant.getCpf();
        this.checkInsFair = null;
        corporateReason = participant.getSpecificInfoForWristbandResponse();
    }
}
