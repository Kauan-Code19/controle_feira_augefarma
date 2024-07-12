package com.augefarma.controle_feira.repositories.laboratory;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratoryRepository extends JpaRepository<LaboratoryEntity, Long> {
}