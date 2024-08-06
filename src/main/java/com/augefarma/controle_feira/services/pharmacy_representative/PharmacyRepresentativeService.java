package com.augefarma.controle_feira.services.pharmacy_representative;

import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.google.zxing.WriterException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;

@Service
public class PharmacyRepresentativeService {

    private final PharmacyRepresentativeRepository pharmacyRepresentativeRepository;
    private final BadgeService badgeService;

    @Autowired
    public PharmacyRepresentativeService(PharmacyRepresentativeRepository pharmacyRepresentativeRepository,
                                         BadgeService badgeService) {
        this.pharmacyRepresentativeRepository = pharmacyRepresentativeRepository;
        this.badgeService = badgeService;
    }

    /**
     * Registers a new client by converting the PharmacyRepresentativeDto to a PharmacyRepresentativeEntity,
     * saving it to the repository, and returning a PharmacyRepresentativeResponseDto.
     *
     * @param pharmacyRepresentativeDto the data transfer object containing client information
     * @return a PharmacyRepresentativeResponseDto containing the saved client information
     */
    @Transactional
    public PharmacyRepresentativeResponseDto registerPharmacyRepresentative(
            PharmacyRepresentativeDto pharmacyRepresentativeDto) {

        // Create a new PharmacyRepresentativeEntity
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();

        // Set the properties of the pharmacyRepresentative entity from the DTO
        pharmacyRepresentative.setName(pharmacyRepresentativeDto.getName());
        pharmacyRepresentative.setCpf(pharmacyRepresentativeDto.getCpf());
        pharmacyRepresentative.setCnpj(pharmacyRepresentativeDto.getCnpj());
        pharmacyRepresentative.setCorporateReason(pharmacyRepresentativeDto.getCorporateReason());

        // Save the pharmacyRepresentative entity to the repository
        pharmacyRepresentativeRepository.save(pharmacyRepresentative);

        // Return a response DTO containing the saved pharmacyRepresentative information
        return new PharmacyRepresentativeResponseDto(pharmacyRepresentative);
    }

    /**
     * Retrieves a client by its ID and returns a DTO representation of the client.
     *
     * @param pharmacyRepresentativeId the ID of the client to retrieve
     * @return a DTO representing the client
     * @throws ResourceNotFoundException if the client with the given ID is not found
     */
    @Transactional(readOnly = true)
    public PharmacyRepresentativeResponseDto getPharmacyRepresentativeById(Long pharmacyRepresentativeId) {

        try {
            // Attempt to retrieve the pharmacyRepresentative entity from the repository using the given ID
            PharmacyRepresentativeEntity pharmacyRepresentative = pharmacyRepresentativeRepository
                    .getReferenceById(pharmacyRepresentativeId);

            // If found, convert the entity to a DTO and return it
            return new PharmacyRepresentativeResponseDto(pharmacyRepresentative);
        } catch (EntityNotFoundException exception) {
            // If the client entity is not found, throw a custom exception indicating the resource is not found
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    /**
     * Generates a badge for a client by their ID.
     *
     * @param pharmacyRepresentativeId the ID of the client for whom to generate the badge
     * @return a byte array representing the generated badge
     * @throws ResourceNotFoundException if the client with the given ID is not found
     * @throws RuntimeException if an error occurs during badge generation
     */
    @Transactional(readOnly = true)
    public byte[] generatePharmacyRepresentativeBadge(Long pharmacyRepresentativeId) {

        try {
            // Attempt to retrieve the pharmacyRepresentative entity from the repository using the given ID
            PharmacyRepresentativeEntity pharmacyRepresentative = pharmacyRepresentativeRepository
                    .getReferenceById(pharmacyRepresentativeId);

            // Generate the badge for the pharmacyRepresentative and return it as a byte array
            return badgeService.generateBadge(pharmacyRepresentative);
        } catch (EntityNotFoundException exception) {
            // If the client entity is not found, throw a custom exception indicating the resource is not found
            throw new ResourceNotFoundException("Resource not found");
        } catch (IOException | WriterException e) {
            // If an error occurs during badge generation, throw a runtime exception
            throw new RuntimeException(e);
        }
    }
}
