package com.augefarma.controle_feira.entities.laboratory;

import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import jakarta.persistence.*;
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
@Table(name = "Laboratories")
public class LaboratoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cpf", nullable = false, unique = true, updatable = false)
    private String cpf;

    @Column(name = "corporate_reason", nullable = false, unique = true, updatable = false)
    private String corporateReason;

    @OneToMany(mappedBy = "laboratoryId")
    @OrderBy("checkinTime ASC")
    private List<EntryExitRecordEntity> checkIns;
}
