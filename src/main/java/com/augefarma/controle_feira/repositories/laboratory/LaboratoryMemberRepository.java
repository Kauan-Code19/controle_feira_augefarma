package com.augefarma.controle_feira.repositories.laboratory;

import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratoryMemberRepository extends JpaRepository<LaboratoryMemberEntity, Long> {
    Optional<LaboratoryMemberEntity> findByCpf(String cpf);
    List<LaboratoryMemberEntity> findByName(String name);
}
