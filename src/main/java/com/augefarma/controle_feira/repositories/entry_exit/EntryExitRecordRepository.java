package com.augefarma.controle_feira.repositories.entry_exit;

import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntryExitRecordRepository extends JpaRepository<EntryExitRecordEntity, Long> {
    List<EntryExitRecordEntity> findByCheckoutTimeIsNull();
    Optional<List<EntryExitRecordEntity>> findByPharmacyRepresentative(
            PharmacyRepresentativeEntity pharmacyRepresentative);
    Optional<List<EntryExitRecordEntity>> findByLaboratoryMember(LaboratoryMemberEntity laboratoryMember);
}
