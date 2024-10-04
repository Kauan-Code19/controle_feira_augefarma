package com.augefarma.controle_feira.integration.services.authentication;

import com.augefarma.controle_feira.dtos.authentication.CpfEntityDto;
import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.entry_exit.EventSegment;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.authentication.ValidateEntryExitService;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ValidateEntryExitServiceTest {

    private final String cpfPharmacyRepresentative = "645.678.789-04"; // CPF for a pharmacy representative
    private final String cpfLaboratoryMember = "456.789.654-32"; // CPF for a laboratory member
    private final EventSegment eventSegment = EventSegment.BUFFET; // Event segment for testing
    private final String laboratoryCorporateReason = "VR Lima"; // Corporate reason for the laboratory

    @Autowired
    private PharmacyRepresentativeRepository pharmacyRepresentativeRepository;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private LaboratoryMemberRepository laboratoryMemberRepository;

    @Autowired
    private RealTimeUpdateService realTimeUpdateService;

    @Autowired
    private ValidateEntryExitService validateEntryExitService;

    @Test
    public void validateEntryForPharmacyRepresentative() {
        // Create and save a pharmacy representative for testing
        PharmacyRepresentativeEntity pharmacyRepresentative = createAndSavePharmacyRepresentative("Kauan",
                "567.456.754-67", "67.678.678/4675-89", this.laboratoryCorporateReason);

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf()); // Create DTO for CPF

        // Expected response for successful access
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        // Call the validateEntry method
        ValidateEntryExitResponseDto accessMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, accessMessage);
    }

    @Test
    public void validateExitForPharmacyRepresentative() {
        // Create a pharmacy representative with an incomplete checkout
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutNotCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf()); // Create DTO for CPF

        // Expected response for successful exit recording
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit recorded successfully");
        // Call the validateExit method
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateEntryForLaboratoryMember() {
        // Create and save a laboratory member for testing
        LaboratoryMemberEntity laboratoryMember = createAndSaveLaboratoryMember("Kauan", "783.342.353-54",
                this.laboratoryCorporateReason);

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf()); // Create DTO for CPF

        // Expected response for successful access
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        // Call the validateEntry method
        ValidateEntryExitResponseDto accessMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, accessMessage);
    }

    @Test
    public void validateExitForLaboratoryMember() {
        // Create a laboratory member with an incomplete checkout
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutNotCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf()); // Create DTO for CPF

        // Expected response for successful exit recording
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit recorded successfully");
        // Call the validateExit method
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateEntryExitException_findByCpf() {
        // Test that exceptions are thrown when the CPF is not found for entry
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);
        });
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateEntry(this.cpfLaboratoryMember, this.eventSegment);
        });
        // Test that exceptions are thrown when the CPF is not found for exit
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateExit(this.cpfPharmacyRepresentative);
        });
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateExit(this.cpfLaboratoryMember);
        });
    }

    @Test
    public void validateEntryForPharmacyRepresentative_checkOutCompleted() {
        // Create a pharmacy representative with a completed checkout
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf()); // Create DTO for CPF

        // Expected response for successful access
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        // Call the validateEntry method
        ValidateEntryExitResponseDto accessMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, accessMessage);
    }

    @Test
    public void validateEntryForLaboratoryMember_checkOutCompleted() {
        // Create a laboratory member with a completed checkout
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf()); // Create DTO for CPF

        // Expected response for successful access
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        // Call the validateEntry method
        ValidateEntryExitResponseDto accessMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, accessMessage);
    }

    @Test
    public void validateEntryForPharmacyRepresentative_accessDenied() {
        // Create a pharmacy representative with an incomplete checkout
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutNotCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf()); // Create DTO for CPF

        // Expected response for denied access
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateAccessDeniedMessage(pharmacyRepresentative));
        // Call the validateEntry method
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateEntryForLaboratoryMember_accessDenied() {
        // Create a laboratory member with an incomplete checkout
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutNotCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf()); // Create DTO for CPF

        // Expected response for denied access
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateAccessDeniedMessage(laboratoryMember));
        // Call the validateEntry method
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateExitPharmacyRepresentative_exitDeniedListEmpty() {
        // Create and save a pharmacy representative for testing
        PharmacyRepresentativeEntity pharmacyRepresentative = createAndSavePharmacyRepresentative("Kauan",
                "567.456.754-67", "67.678.678/4675-89", this.laboratoryCorporateReason);

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf()); // Create DTO for CPF

        // Expected response for denied exit
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(pharmacyRepresentative));
        // Call the validateExit method
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateExitLaboratoryMember_exitDeniedListEmpty() {
        // Create and save a laboratory member for testing
        LaboratoryMemberEntity laboratoryMember = createAndSaveLaboratoryMember("Kauan",
                "567.456.754-67", this.laboratoryCorporateReason);

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf()); // Create DTO for CPF

        // Expected response for denied exit
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(laboratoryMember));
        // Call the validateExit method
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        // Assert the expected response matches the actual response
        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateExitPharmacyRepresentative_exitDeniedCheckOutCompleted() {
        // Create a pharmacy representative with a completed checkout status
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutCompleted();

        // Create a DTO for the CPF of the pharmacy representative
        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf());

        // Create the expected response DTO for a denied exit
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(pharmacyRepresentative));

        // Call the validateExit method to check if exit is denied
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        // Assert that the expected response matches the actual response
        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateExitLaboratoryMember_exitDeniedCheckOutCompleted() {
        // Create a laboratory member with a completed checkout status
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutCompleted();

        // Create a DTO for the CPF of the laboratory member
        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf());

        // Create the expected response DTO for a denied exit
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(laboratoryMember));

        // Call the validateExit method to check if exit is denied
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        // Assert that the expected response matches the actual response
        assertResponse(expectedResponse, deniedMessage);
    }

    private PharmacyRepresentativeEntity createPharmacyRepresentativeWithCheckOutCompleted() {
        // Create and save a pharmacy representative and their checkout records as completed
        PharmacyRepresentativeEntity pharmacyRepresentative = createAndSavePharmacyRepresentative("Kauan",
                "567.456.754-67", "67.678.678/4675-89", this.laboratoryCorporateReason);

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutCompleted(pharmacyRepresentative);
        pharmacyRepresentative.setEntryExitRecords(entryExitRecords); // Set the completed records

        return pharmacyRepresentative; // Return the created representative
    }

    private LaboratoryMemberEntity createLaboratoryMemberWithCheckOutCompleted() {
        // Create and save a laboratory member and their checkout records as completed
        LaboratoryMemberEntity laboratoryMember = createAndSaveLaboratoryMember("Kauan",
                "567.456.754-67", this.laboratoryCorporateReason);

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutCompleted(laboratoryMember);
        laboratoryMember.setEntryExitRecords(entryExitRecords); // Set the completed records

        return laboratoryMember; // Return the created laboratory member
    }

    private PharmacyRepresentativeEntity createPharmacyRepresentativeWithCheckOutNotCompleted() {
        // Create and save a pharmacy representative with checkout not completed
        PharmacyRepresentativeEntity pharmacyRepresentative = createAndSavePharmacyRepresentative("Kauan",
                "567.456.754-67", "67.678.678/4675-89", this.laboratoryCorporateReason);

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutNotCompleted(pharmacyRepresentative);
        pharmacyRepresentative.setEntryExitRecords(entryExitRecords); // Set the incomplete records

        return pharmacyRepresentative; // Return the created representative
    }

    private LaboratoryMemberEntity createLaboratoryMemberWithCheckOutNotCompleted() {
        // Create and save a laboratory member with checkout not completed
        LaboratoryMemberEntity laboratoryMember = createAndSaveLaboratoryMember("Kauan",
                "567.456.754-67", this.laboratoryCorporateReason);

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutNotCompleted(laboratoryMember);
        laboratoryMember.setEntryExitRecords(entryExitRecords); // Set the incomplete records

        return laboratoryMember; // Return the created laboratory member
    }

    private PharmacyRepresentativeEntity createAndSavePharmacyRepresentative(String name, String cpf,
                                                                             String cnpj, String corporateReason) {
        // Create a new pharmacy representative entity
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setName(name);
        pharmacyRepresentative.setCpf(cpf);
        pharmacyRepresentative.setCnpj(cnpj);
        pharmacyRepresentative.setCorporateReason(corporateReason);
        pharmacyRepresentative.setEntryExitRecords(new ArrayList<>()); // Initialize records

        return pharmacyRepresentativeRepository.save(pharmacyRepresentative); // Save and return the entity
    }

    private LaboratoryMemberEntity createAndSaveLaboratoryMember(String name, String cpf, String laboratory) {
        // Create a new laboratory member entity
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();
        createLaboratory(); // Ensure the laboratory exists

        LaboratoryEntity laboratoryEntity = laboratoryRepository.findByCorporateReason(laboratory)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found"));

        laboratoryMember.setName(name);
        laboratoryMember.setCpf(cpf);
        laboratoryMember.setLaboratory(laboratoryEntity);
        laboratoryMember.setEntryExitRecords(new ArrayList<>()); // Initialize records

        return laboratoryMemberRepository.save(laboratoryMember); // Save and return the entity
    }

    private void assertResponse(ValidateEntryExitResponseDto expected, ValidateEntryExitResponseDto actual) {
        // Assert that the actual response is not null and matches the expected response
        assertNotNull(actual);
        assertEquals(expected.message(), actual.message());
    }

    private void createLaboratory() {
        // Create a laboratory entity and save it
        LaboratoryEntity laboratory = new LaboratoryEntity();
        laboratory.setCorporateReason(this.laboratoryCorporateReason);
        laboratory.setMembers(new ArrayList<>());

        laboratoryRepository.save(laboratory);
    }

    private List<EntryExitRecordEntity> createEntryExitRecordsWithCheckOutCompleted(Object entity) {
        // Create entry-exit records with checkout completed
        return createEntryExitRecords(LocalDateTime.now().minusHours(1), entity);
    }

    private List<EntryExitRecordEntity> createEntryExitRecordsWithCheckOutNotCompleted(Object entity) {
        // Create entry-exit records with checkout not completed
        return createEntryExitRecords(null, entity);
    }

    private List<EntryExitRecordEntity> createEntryExitRecords(LocalDateTime checkoutTime, Object entity) {
        // Create and configure an entry-exit record
        EntryExitRecordEntity entryExitRecord = new EntryExitRecordEntity();
        entryExitRecord.setCheckinTime(LocalDateTime.now());
        entryExitRecord.setCheckoutTime(checkoutTime); // Set checkout time
        entryExitRecord.setEventSegment(this.eventSegment); // Set event segment

        // Set entity type for the entry-exit record
        if (entity instanceof PharmacyRepresentativeEntity) {
            entryExitRecord.setPharmacyRepresentative((PharmacyRepresentativeEntity) entity);
            entryExitRecord.setLaboratoryMember(null); // Clear laboratory member reference
        }

        if (entity instanceof LaboratoryMemberEntity) {
            entryExitRecord.setLaboratoryMember((LaboratoryMemberEntity) entity);
            entryExitRecord.setPharmacyRepresentative(null); // Clear pharmacy representative reference
        }

        List<EntryExitRecordEntity> entryExitRecords = new ArrayList<>();
        entryExitRecords.add(entryExitRecord); // Add record to the list

        return entryExitRecords; // Return the list of records
    }

    private String generateAccessDeniedMessage(PharmacyRepresentativeEntity pharmacyRepresentative) {
        // Generate a message for access denial for pharmacy representatives
        return "Access denied: CPF " + pharmacyRepresentative.getCpf()
                + " with ID " + pharmacyRepresentative.getId() + " has already been granted access";
    }

    private String generateAccessDeniedMessage(LaboratoryMemberEntity laboratoryMember) {
        // Generate a message for access denial for laboratory members
        return "Access denied: CPF " + laboratoryMember.getCpf()
                + " with ID " + laboratoryMember.getId() + " has already been granted access";
    }

    private String generateExitDeniedMessage(PharmacyRepresentativeEntity pharmacyRepresentative) {
        // Generate a message for exit denial for pharmacy representatives
        return "Departure denied: CPF " + pharmacyRepresentative.getCpf()
                + " with ID " + pharmacyRepresentative.getId() + " already had the exit registered";
    }

    private String generateExitDeniedMessage(LaboratoryMemberEntity laboratoryMember) {
        // Generate a message for exit denial for laboratory members
        return "Departure denied: CPF " + laboratoryMember.getCpf()
                + " with ID " + laboratoryMember.getId() + " already had the exit registered";
    }

    private ValidateEntryExitResponseDto createValidateEntryExitResponseDto(String message) {
        // Create and return a response DTO for entry/exit validation
        return new ValidateEntryExitResponseDto(message);
    }
}
