package com.augefarma.controle_feira.services.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.repositories.participant.ParticipantRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.google.zxing.WriterException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LaboratoryMemberService {

    private final LaboratoryMemberRepository laboratoryMemberRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final ParticipantRepository participantRepository;
    private final BadgeService badgeService;

    @Autowired
    public LaboratoryMemberService(LaboratoryMemberRepository laboratoryMemberRepository,
                                   LaboratoryRepository laboratoryRepository,
                                   ParticipantRepository participantRepository, BadgeService badgeService) {
        this.laboratoryMemberRepository = laboratoryMemberRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.participantRepository = participantRepository;
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
        participantRepository.save(laboratoryMember);

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
            throw new ResourceNotFoundException("Membro de laboratório não encontrado");
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
            throw new ResourceNotFoundException("Membro de laboratório não encontrado");
        } catch (IOException | WriterException e) {
            // Throw a runtime exception if there is an error generating the badge
            throw new RuntimeException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<LaboratoryMemberResponseDto> getLaboratoryMemberByNameOrCpf(String nameOrCpf) {

        String cpfRegex = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$";

        if (nameOrCpf.matches(cpfRegex)) {
            // If the value is a CPF
            LaboratoryMemberEntity laboratoryMember = laboratoryMemberRepository
                    .findByCpf(nameOrCpf)
                    .orElseThrow(() -> new ResourceNotFoundException("Membro de laboratório não encontrado"));

            // Return a list with only one element
            return List.of(new LaboratoryMemberResponseDto(laboratoryMember));
        }

        // If the value is a name
        List<LaboratoryMemberEntity> laboratoryMembers = laboratoryMemberRepository
                .findByName(nameOrCpf);

        if (laboratoryMembers.isEmpty()) {
            // If no representatives are found with the provided name
            throw new ResourceNotFoundException("Membro de laboratório não encontrado");
        }

        // Convert the list of entities to a list of DTOs and return
        return laboratoryMembers.stream()
                .map(LaboratoryMemberResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLaboratoryMember(Long laboratoryMemberId) {
        try {
            laboratoryMemberRepository.deleteById(laboratoryMemberId);
        } catch (EntityNotFoundException exception) {
            throw new ResourceNotFoundException("Membro de laboratório não encontrado");
        }
    }
}
