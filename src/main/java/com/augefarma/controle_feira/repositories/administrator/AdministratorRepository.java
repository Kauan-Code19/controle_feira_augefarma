package com.augefarma.controle_feira.repositories.administrator;

import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<AdministratorEntity, Long> {
}
