package com.augefarma.controle_feira.entities.check_in;

import com.augefarma.controle_feira.entities.client.ClientEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "Check-ins")
public class CheckInEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", updatable = false)
    private ClientEntity clientId;

    @ManyToOne
    @JoinColumn(name = "laboratory_id", updatable = false)
    private LaboratoryEntity laboratoryId;

    @Column(name = "checkin_time", updatable = false, nullable = false)
    private LocalDateTime checkinTime;

    @Column(name = "checkout_time", updatable = false)
    private LocalDateTime checkoutTime;
}
