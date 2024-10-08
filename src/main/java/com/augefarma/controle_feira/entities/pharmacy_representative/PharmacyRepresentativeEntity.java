package com.augefarma.controle_feira.entities.pharmacy_representative;

import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
@Table(name = "PharmacyRepresentatives")
public class PharmacyRepresentativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "cpf", nullable = false, unique = true, updatable = false)
    private String cpf;

    @Column(name = "cnpj", nullable = false, unique = true, updatable = false)
    private String cnpj;

    @Column(name = "corporate_reason", nullable = false, unique = true, updatable = false)
    private String corporateReason;

    @OneToMany(mappedBy = "pharmacyRepresentative")
    @OrderBy("checkinTime ASC")
    private List<EntryExitRecordEntity> entryExitRecords;
}
