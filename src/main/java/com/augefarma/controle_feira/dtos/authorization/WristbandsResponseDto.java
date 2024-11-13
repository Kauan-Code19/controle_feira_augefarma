package com.augefarma.controle_feira.dtos.authorization;

import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
public class WristbandsResponseDto {
    private Long id;

    private String name;

    private String cpf;

    private String lastCheckInFair;

    private String corporateReason;

    public WristbandsResponseDto(ParticipantEntity participant, String lastCheckInFair) {
        id = participant.getId();
        name = participant.getName();
        cpf = participant.getCpf();
        this.lastCheckInFair = lastCheckInFair;
        corporateReason = participant.getSpecificInfoForWristbandResponse();
    }

    public WristbandsResponseDto(ParticipantEntity participant) {
        id = participant.getId();
        name = participant.getName();
        cpf = participant.getCpf();
        this.lastCheckInFair = null;
        corporateReason = participant.getSpecificInfoForWristbandResponse();
    }
}
