package com.augefarma.controle_feira.entities.participant;

import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.entry_exit.ExitRecordEntity;
import com.augefarma.controle_feira.interfaces.RealTimePresenceRegistrable;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "participants")
public class ParticipantEntity implements RealTimePresenceRegistrable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cpf", nullable = false, unique = true, updatable = false)
    private String cpf;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.PERSIST)
    @OrderBy("checkinTime ASC")
    private List<EntryRecordEntity> entryRecords;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.PERSIST)
    @OrderBy("checkoutTime ASC")
    private List<ExitRecordEntity> exitRecords;

    @Override
    public void addToRealtimeUpdateService(RealTimeUpdateService realTimeUpdateService) {}

    @Override
    public void removeToRealtimeUpdateService(RealTimeUpdateService realTimeUpdateService) {}
}
