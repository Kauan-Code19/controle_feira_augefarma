package com.augefarma.controle_feira.services.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;

    @Autowired
    public LaboratoryService(LaboratoryRepository laboratoryRepository) {
        this.laboratoryRepository = laboratoryRepository;
    }

    /**
     * Registers a new laboratory.
     *
     * @param laboratoryDto the DTO containing the laboratory's information
     * @return a LaboratoryResponseDto containing the registered laboratory's details
     */
    @Transactional
    public LaboratoryResponseDto registerLaboratory(LaboratoryDto laboratoryDto) {
        LaboratoryEntity laboratory = new LaboratoryEntity(); // Create a new LaboratoryEntity from the provided DTO

        // Set the properties of the entity from the DTO
        laboratory.setName(laboratoryDto.getName());
        laboratory.setCpf(laboratoryDto.getCpf());
        laboratory.setCorporateReason(laboratoryDto.getCorporateReason());

        laboratoryRepository.save(laboratory); // Save the laboratory entity to the repository

        return new LaboratoryResponseDto(laboratory); // Convert the saved entity to a DTO and return it
    }
}
