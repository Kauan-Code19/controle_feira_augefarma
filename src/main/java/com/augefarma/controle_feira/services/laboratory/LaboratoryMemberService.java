package com.augefarma.controle_feira.services.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.google.zxing.WriterException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;

@Service
public class LaboratoryMemberService {

    private final LaboratoryMemberRepository laboratoryMemberRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final BadgeService badgeService;

    @Autowired
    public LaboratoryMemberService(LaboratoryMemberRepository laboratoryMemberRepository,
                                   LaboratoryRepository laboratoryRepository, BadgeService badgeService) {
        this.laboratoryMemberRepository = laboratoryMemberRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.badgeService = badgeService;
    }

    /**
     * Registers a new laboratory member.
     *
     * @param laboratoryMemberDto the DTO containing the laboratory member's details
     * @return a response DTO containing the registered laboratory member's details
     * @throws ResourceNotFoundException if the laboratory is not found
     */
    @Transactional
    public LaboratoryMemberResponseDto registerLaboratoryMember(LaboratoryMemberDto laboratoryMemberDto) {

        // Retrieve the laboratory entity based on the provided corporate reason
        LaboratoryEntity laboratory = laboratoryRepository
                .findByCorporateReason(laboratoryMemberDto.getLaboratoryCorporateReason())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found"));

        // Create a new LaboratoryMemberEntity from the provided DTO
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();
        laboratoryMember.setName(laboratoryMemberDto.getName());
        laboratoryMember.setCpf(laboratoryMemberDto.getCpf());
        laboratoryMember.setLaboratory(laboratory);

        // Save the laboratory member entity to the repository
        laboratoryMemberRepository.save(laboratoryMember);

        // Return the saved entity as a response DTO
        return new LaboratoryMemberResponseDto(laboratoryMember);
    }

    /**
     * Retrieves a laboratory member by ID.
     *
     * @param laboratoryMemberId the ID of the laboratory member to retrieve
     * @return a response DTO containing the laboratory member's details
     * @throws ResourceNotFoundException if the laboratory member is not found
     */
    @Transactional(readOnly = true)
    public LaboratoryMemberResponseDto getLaboratoryMemberById(Long laboratoryMemberId) {

        try {
            // Retrieve the laboratory member entity from the repository using the given ID
            LaboratoryMemberEntity laboratoryMember = laboratoryMemberRepository.getReferenceById(laboratoryMemberId);

            // Return the entity as a response DTO
            return new LaboratoryMemberResponseDto(laboratoryMember);
        } catch (EntityNotFoundException exception) {
            // Throw a custom exception if the laboratory member is not found
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    /**
     * Generates a badge for a laboratory member by ID.
     *
     * @param laboratoryMemberId the ID of the laboratory member for which to generate the badge
     * @return a byte array containing the badge in PDF format
     * @throws ResourceNotFoundException if the laboratory member is not found
     * @throws RuntimeException if there is an error generating the badge
     */
    @Transactional(readOnly = true)
    public byte[] generateLaboratoryMemberBadge(Long laboratoryMemberId) {

        try {
            // Retrieve the laboratory member entity from the repository using the given ID
            LaboratoryMemberEntity laboratoryMember = laboratoryMemberRepository.getReferenceById(laboratoryMemberId);

            // Generate and return the badge as a byte array
            return badgeService.generateBadge(laboratoryMember);
        } catch (EntityNotFoundException exception) {
            // Throw a custom exception if the laboratory member is not found
            throw new ResourceNotFoundException("Resource not found");
        } catch (IOException | WriterException e) {
            // Throw a runtime exception if there is an error generating the badge
            throw new RuntimeException(e);
        }
    }
}
