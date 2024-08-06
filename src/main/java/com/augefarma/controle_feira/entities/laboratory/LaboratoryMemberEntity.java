package com.augefarma.controle_feira.entities.laboratory;

import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "LaboratoryMembers")
public class LaboratoryMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cpf", nullable = false, unique = true, updatable = false)
    private String cpf;

    @ManyToOne
    @JoinColumn(name = "laboratory_id", nullable = false)
    private LaboratoryEntity laboratory;

    @OneToMany(mappedBy = "laboratoryMember")
    @OrderBy("checkinTime ASC")
    private List<EntryExitRecordEntity> entryExitRecords;
}
