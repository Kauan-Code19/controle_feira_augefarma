package com.augefarma.controle_feira.repositories.pharmacy_representative;

import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyRepresentativeRepository extends JpaRepository<PharmacyRepresentativeEntity, Long> {
    Optional<PharmacyRepresentativeEntity> findByCpf(String cpf);
    List<PharmacyRepresentativeEntity> findByName(String name);
}
