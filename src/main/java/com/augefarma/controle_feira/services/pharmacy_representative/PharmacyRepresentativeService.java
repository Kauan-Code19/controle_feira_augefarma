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
import java.util.List;
import java.util.stream.Collectors;

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
            throw new ResourceNotFoundException("Representante de farmácia não encontrado");
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
            throw new ResourceNotFoundException("Representante de farmácia não encontrado");
        } catch (IOException | WriterException e) {
            // If an error occurs during badge generation, throw a runtime exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a list of Pharmacy Representatives by their name or CPF. If the input is a CPF,
     * it returns a list containing one Pharmacy Representative. If the input is a name,
     * it returns all Pharmacy Representatives with that name.
     *
     * @param nameOrCpf the name or CPF of the Pharmacy Representative to search for
     * @return a list of PharmacyRepresentativeResponseDto containing the found Pharmacy Representatives
     * @throws ResourceNotFoundException if no Pharmacy Representatives are found
     */
    @Transactional(readOnly = true)
    public List<PharmacyRepresentativeResponseDto> getPharmacyRepresentativeByNameOrCpf(String nameOrCpf) {

        // Regex to verify if the provided value is a valid CPF
        String cpfRegex = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$";

        if (nameOrCpf.matches(cpfRegex)) {
            // If the value is a CPF
            PharmacyRepresentativeEntity pharmacyRepresentative = pharmacyRepresentativeRepository
                    .findByCpf(nameOrCpf)
                    .orElseThrow(() -> new ResourceNotFoundException("Representante de farmácia não encontrado"));

            // Return a list with only one element
            return List.of(new PharmacyRepresentativeResponseDto(pharmacyRepresentative));

        } else {
            // If the value is a name
            List<PharmacyRepresentativeEntity> pharmacyRepresentatives = pharmacyRepresentativeRepository
                    .findByName(nameOrCpf);

            if (pharmacyRepresentatives.isEmpty()) {
                // If no representatives are found with the provided name
                throw new ResourceNotFoundException("Representante de farmácia não encontrado");
            }

            // Convert the list of entities to a list of DTOs and return
            return pharmacyRepresentatives.stream()
                    .map(PharmacyRepresentativeResponseDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public void deletePharmacyRepresentative(Long pharmacyRepresentativeId) {
        try {
            pharmacyRepresentativeRepository.deleteById(pharmacyRepresentativeId);
        }catch (EntityNotFoundException exception) {
            throw new ResourceNotFoundException("Representante de farmácia não encontrado");
        }
    }
}
