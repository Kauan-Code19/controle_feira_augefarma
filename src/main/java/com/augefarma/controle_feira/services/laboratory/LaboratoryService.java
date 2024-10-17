package com.augefarma.controle_feira.services.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LaboratoryService {

    private final LaboratoryRepository laboratoryRepository;

    @Autowired
    public LaboratoryService(LaboratoryRepository laboratoryRepository, BadgeService badgeService) {
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
        // Create a new LaboratoryEntity from the provided DTO
        LaboratoryEntity laboratory = new LaboratoryEntity();

        // Set the properties of the entity from the DTO
        laboratory.setCorporateReason(laboratoryDto.getCorporateReason());

        // Save the laboratory entity to the repository
        laboratoryRepository.save(laboratory);

        // Convert the saved entity to a DTO and return it
        return new LaboratoryResponseDto(laboratory);
    }

    @Transactional(readOnly = true)
    public LaboratoryResponseDto getLaboratoryByCorporateReason(String corporateReason) {
        LaboratoryEntity laboratory = laboratoryRepository.findByCorporateReason(corporateReason)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratório não encontrado"));

        return new LaboratoryResponseDto(laboratory);
    }

    @Transactional
    public void deleteLaboratory(Long laboratoryId) {
        try {
            laboratoryRepository.deleteById(laboratoryId);
        } catch (EntityNotFoundException exception) {
            throw new ResourceNotFoundException("Laboratório não encontrado");
        }
    }
}
