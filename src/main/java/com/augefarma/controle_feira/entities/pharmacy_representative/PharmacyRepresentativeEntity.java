package com.augefarma.controle_feira.entities.pharmacy_representative;

import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
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
@Table(name = "PharmacyRepresentatives")
public class PharmacyRepresentativeEntity extends ParticipantEntity {

    @Column(name = "cnpj", nullable = false, unique = true, updatable = false)
    private String cnpj;

    @Column(name = "corporate_reason", nullable = false, unique = true, updatable = false)
    private String corporateReason;

    @Override
    public void addToRealtimeUpdateService(RealTimeUpdateService realTimeUpdateService) {
        realTimeUpdateService.addPharmacyRepresentativePresent(this);
    }

    @Override
    public void removeToRealtimeUpdateService(RealTimeUpdateService realTimeUpdateService) {
        realTimeUpdateService.removePharmacyRepresentativePresent(this);
    }
}
