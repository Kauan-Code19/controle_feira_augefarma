package com.augefarma.controle_feira.entities.laboratory;

import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "LaboratoryMembers")
public class LaboratoryMemberEntity extends ParticipantEntity {

    @ManyToOne
    @JoinColumn(name = "laboratory_id", nullable = false)
    private LaboratoryEntity laboratory;

    @Override
    public void addToRealtimeUpdateService(RealTimeUpdateService realTimeUpdateService) {
        realTimeUpdateService.addLaboratoryMemberPresent(this);
    }

    @Override
    public void removeToRealtimeUpdateService(RealTimeUpdateService realTimeUpdateService) {
        realTimeUpdateService.removeLaboratoryMemberPresent(this);
    }

    @Override
    public String getSpecificInfoForWristbandResponse() {
        return this.getLaboratory().getCorporateReason();
    }
}
