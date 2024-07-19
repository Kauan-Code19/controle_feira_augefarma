package com.augefarma.controle_feira.repositories.entry_exit;

import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryExitRecordRepository extends JpaRepository<EntryExitRecordEntity, Long> {
}
