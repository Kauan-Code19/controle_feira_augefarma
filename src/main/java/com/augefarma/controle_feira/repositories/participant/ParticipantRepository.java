package com.augefarma.controle_feira.repositories.participant;

import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<ParticipantEntity, Long> {
    Optional<ParticipantEntity> findByCpf(String cpf);
}
