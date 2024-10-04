package com.augefarma.controle_feira.unit.services.authentication;

import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.entry_exit.EventSegment;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryExitRecordRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.authentication.ValidateEntryExitService;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidateEntryExitServiceTest {

    private final String cpfPharmacyRepresentative = "645.678.789-04"; // CPF of the pharmacy representative
    private final String cpfLaboratoryMember = "456.789.654-32"; // CPF of the laboratory member
    private final EventSegment eventSegment = EventSegment.BUFFET; // Event segment
    private boolean shouldVerifySave = true; // Flag to determine if save verification should be performed

    @Mock
    private PharmacyRepresentativeRepository pharmacyRepresentativeRepository;

    @Mock
    private LaboratoryMemberRepository laboratoryMemberRepository;

    @Mock
    private EntryExitRecordRepository entryExitRecordRepository;

    @Mock
    private RealTimeUpdateService realTimeUpdateService;

    @InjectMocks
    private ValidateEntryExitService validateEntryExitService;

    @Test
    public void validateEntryForPharmacyRepresentative() {
        // Create a new PharmacyRepresentativeEntity with an empty list of entry/exit records
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setEntryExitRecords(new ArrayList<>());

        // Mock the behavior and save the entry/exit record for the pharmacy representative
        setupPharmacyRepresentativeMockAndSaveEntryExitRecord(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        // Create the expected response DTO for a successful entry
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        // Call the service method to validate the entry
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Verify the interactions with the pharmacy representative repository
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateExitForPharmacyRepresentative() {
        // Create a pharmacy representative with an entry/exit record where the checkout is not completed
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutNotCompleted();

        // Mock the behavior and save the entry/exit record for the pharmacy representative
        setupPharmacyRepresentativeMockAndSaveEntryExitRecord(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        // Create the expected response DTO for a successful exit
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit recorded successfully");
        // Call the service method to validate the exit
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfPharmacyRepresentative);

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Verify the interactions with the pharmacy representative repository
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateEntryForLaboratoryMember() {
        // Create a new LaboratoryMemberEntity with an empty list of entry/exit records
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();
        laboratoryMember.setEntryExitRecords(new ArrayList<>());

        // Mock the behavior and save the entry/exit record for the laboratory member
        setupLaboratoryMemberMockAndSaveEntryExitRecord(this.cpfLaboratoryMember, laboratoryMember);

        // Create the expected response DTO for a successful entry
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        // Call the service method to validate the entry
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfLaboratoryMember, this.eventSegment);

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Verify the interactions with the laboratory member repository
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateExitForLaboratoryMember() {
        // Create a laboratory member with an entry/exit record where the checkout is not completed
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutNotCompleted();

        // Mock the behavior and save the entry/exit record for the laboratory member
        setupLaboratoryMemberMockAndSaveEntryExitRecord(this.cpfLaboratoryMember, laboratoryMember);

        // Create the expected response DTO for a successful exit
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit recorded successfully");
        // Call the service method to validate the exit
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfLaboratoryMember);

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Verify the interactions with the laboratory member repository
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateEntryExitException_findByCpf() {
        // Mock the repository to return empty when searching for the pharmacy representative
        when(pharmacyRepresentativeRepository.findByCpf(anyString()))
                .thenReturn(Optional.empty());
        // Mock the repository to return empty when searching for the laboratory member
        when(laboratoryMemberRepository.findByCpf(anyString()))
                .thenReturn(Optional.empty());

        // Assert that a ResourceNotFoundException is thrown when validating entry for a pharmacy representative
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);
        });
        // Assert that a ResourceNotFoundException is thrown when validating entry for a laboratory member
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateEntry(this.cpfLaboratoryMember, this.eventSegment);
        });
        // Assert that a ResourceNotFoundException is thrown when validating exit for a pharmacy representative
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateExit(this.cpfPharmacyRepresentative);
        });
        // Assert that a ResourceNotFoundException is thrown when validating exit for a laboratory member
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateExit(this.cpfLaboratoryMember);
        });
    }

    @Test
    public void validateEntryForPharmacyRepresentative_checkOutCompleted() {
        // Create a pharmacy representative with an entry/exit record where the checkout is completed
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutCompleted();

        // Mock the behavior and save the entry/exit record for the pharmacy representative
        setupPharmacyRepresentativeMockAndSaveEntryExitRecord(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        // Create the expected response DTO for a successful entry
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        // Call the service method to validate the entry
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Verify the interactions with the pharmacy representative repository
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateEntryForLaboratoryMember_checkOutCompleted() {
        // Create a laboratory member with an entry/exit record where the checkout is completed
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutCompleted();

        // Mock the behavior and save the entry/exit record for the laboratory member
        setupLaboratoryMemberMockAndSaveEntryExitRecord(this.cpfLaboratoryMember, laboratoryMember);

        // Create the expected response DTO for a successful entry
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        // Call the service method to validate the entry
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfLaboratoryMember, this.eventSegment);

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Verify the interactions with the laboratory member repository
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateEntryForPharmacyRepresentative_accessDenied() {
        // Create a pharmacy representative with an entry/exit record where the checkout is not completed
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutNotCompleted();

        // Mock the pharmacy representative behavior
        setupPharmacyRepresentativeMock(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        // Create the expected response DTO for an access denied scenario
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateAccessDeniedMessage(pharmacyRepresentative));
        // Call the service method to validate the entry
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);

        // Prevent save verification
        this.shouldVerifySave = false;

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Verify the interactions with the pharmacy representative repository
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateEntryLaboratoryMember_accessDenied() {
        // Create a laboratory member with an entry/exit record where the checkout is not completed
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutNotCompleted();

        // Mock the laboratory member behavior
        setupLaboratoryMemberMock(this.cpfLaboratoryMember, laboratoryMember);

        // Create the expected response DTO for an access denied scenario
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateAccessDeniedMessage(laboratoryMember));
        // Call the service method to validate the entry
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfLaboratoryMember, this.eventSegment);

        // Prevent save verification
        this.shouldVerifySave = false;

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Verify the interactions with the laboratory member repository
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateExitPharmacyRepresentative_exitDeniedListEmpty() {
        // Create a pharmacy representative with an empty list of entry/exit records
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setEntryExitRecords(new ArrayList<>());

        // Mock the pharmacy representative behavior
        setupPharmacyRepresentativeMock(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        // Create the expected response DTO for an exit denied scenario
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit not found");
        // Call the service method to validate the exit
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfPharmacyRepresentative);

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Prevent save verification
        this.shouldVerifySave = false;

        // Verify the interactions with the pharmacy representative repository
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateExitLaboratoryMember_exitDeniedListEmpty() {
        // Create a laboratory member with an empty list of entry/exit records
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();
        laboratoryMember.setEntryExitRecords(new ArrayList<>());

        // Mock the laboratory member behavior
        setupLaboratoryMemberMock(this.cpfLaboratoryMember, laboratoryMember);

        // Create the expected response DTO for an exit denied scenario
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit not found");
        // Call the service method to validate the exit
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfLaboratoryMember);

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);
        // Prevent save verification
        this.shouldVerifySave = false;

        // Verify the interactions with the laboratory member repository
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateExitPharmacyRepresentative_exitDeniedCheckOutCompleted() {
        // Create a Pharmacy Representative entity with completed check-out
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutCompleted();

        // Set up the mock for the pharmacy representative
        setupPharmacyRepresentativeMock(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        // Create the expected response for exit validation
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(pharmacyRepresentative));

        // Call the exit validation service
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfPharmacyRepresentative);

        // Set the flag to not verify if the record was saved
        this.shouldVerifySave = false;

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);

        // Verify interaction with the pharmacy representative mock
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateExitLaboratoryMember_exitDeniedCheckOutCompleted() {
        // Create a Laboratory Member entity with completed check-out
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutCompleted();

        // Set up the mock for the laboratory member
        setupLaboratoryMemberMock(this.cpfLaboratoryMember, laboratoryMember);

        // Create the expected response for exit validation
        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(laboratoryMember));

        // Call the exit validation service
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfLaboratoryMember);

        // Set the flag to not verify if the record was saved
        this.shouldVerifySave = false;

        // Assert that the actual response matches the expected response
        assertResponse(expectedResponse, actualResponse);

        // Verify interaction with the laboratory member mock
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    // Helper method to create a Pharmacy Representative with completed check-out
    private PharmacyRepresentativeEntity createPharmacyRepresentativeWithCheckOutCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();

        // Create entry-exit records with check-out completed
        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutCompleted();
        pharmacyRepresentative.setEntryExitRecords(entryExitRecords);

        return pharmacyRepresentative;
    }

    // Helper method to create a Laboratory Member with completed check-out
    private LaboratoryMemberEntity createLaboratoryMemberWithCheckOutCompleted() {
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();

        // Create entry-exit records with check-out completed
        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutCompleted();
        laboratoryMember.setEntryExitRecords(entryExitRecords);

        return laboratoryMember;
    }

    // Helper method to create a Pharmacy Representative with check-out not completed
    private PharmacyRepresentativeEntity createPharmacyRepresentativeWithCheckOutNotCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();

        // Create entry-exit records with check-out not completed
        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutNotCompleted();
        pharmacyRepresentative.setEntryExitRecords(entryExitRecords);

        return pharmacyRepresentative;
    }

    // Helper method to create a Laboratory Member with check-out not completed
    private LaboratoryMemberEntity createLaboratoryMemberWithCheckOutNotCompleted() {
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();

        // Create entry-exit records with check-out not completed
        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutNotCompleted();
        laboratoryMember.setEntryExitRecords(entryExitRecords);

        return laboratoryMember;
    }

    // Helper method to create entry-exit records with check-out completed
    private List<EntryExitRecordEntity> createEntryExitRecordsWithCheckOutCompleted() {
        return createEntryExitRecords(LocalDateTime.now().minusHours(1)); // Set check-out time to one hour ago
    }

    // Helper method to create entry-exit records with check-out not completed
    private List<EntryExitRecordEntity> createEntryExitRecordsWithCheckOutNotCompleted() {
        return createEntryExitRecords(null); // Set check-out time to null
    }

    // Helper method to create entry-exit records with a specific check-out time
    private List<EntryExitRecordEntity> createEntryExitRecords(LocalDateTime checkoutTime) {
        EntryExitRecordEntity entryExitRecord = new EntryExitRecordEntity();
        entryExitRecord.setCheckoutTime(checkoutTime); // Set the check-out time

        List<EntryExitRecordEntity> entryExitRecords = new ArrayList<>();
        entryExitRecords.add(entryExitRecord); // Add the record to the list

        return entryExitRecords;
    }

    // Helper method to set up mock for Pharmacy Representative and save entry-exit record
    private void setupPharmacyRepresentativeMockAndSaveEntryExitRecord(
            String cpf, PharmacyRepresentativeEntity pharmacyRepresentative) {
        when(pharmacyRepresentativeRepository.findByCpf(cpf))
                .thenReturn(Optional.of(pharmacyRepresentative));
        when(entryExitRecordRepository.save(any(EntryExitRecordEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved entry-exit record
    }

    // Helper method to set up mock for Pharmacy Representative
    private void setupPharmacyRepresentativeMock(String cpf,
                                                 PharmacyRepresentativeEntity pharmacyRepresentative) {
        when(pharmacyRepresentativeRepository.findByCpf(cpf))
                .thenReturn(Optional.of(pharmacyRepresentative)); // Return the pharmacy representative
    }

    // Helper method to set up mock for Laboratory Member and save entry-exit record
    private void setupLaboratoryMemberMockAndSaveEntryExitRecord(
            String cpf, LaboratoryMemberEntity laboratoryMember) {
        when(laboratoryMemberRepository.findByCpf(cpf))
                .thenReturn(Optional.of(laboratoryMember));
        when(entryExitRecordRepository.save(any(EntryExitRecordEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved entry-exit record
    }

    // Helper method to set up mock for Laboratory Member
    private void setupLaboratoryMemberMock(String cpf, LaboratoryMemberEntity laboratoryMember) {
        when(laboratoryMemberRepository.findByCpf(cpf))
                .thenReturn(Optional.of(laboratoryMember)); // Return the laboratory member
    }

    // Helper method to verify interaction with Pharmacy Representative mock
    private void verifyPharmacyRepresentativeInteraction(String cpf) {
        verify(pharmacyRepresentativeRepository).findByCpf(cpf); // Verify CPF lookup

        // If shouldVerifySave is true, verify that the entry-exit record was saved
        if (shouldVerifySave) {
            verify(entryExitRecordRepository).save(any(EntryExitRecordEntity.class));
        }
    }

    // Helper method to verify interaction with Laboratory Member mock
    private void verifyLaboratoryMemberInteraction(String cpf) {
        verify(laboratoryMemberRepository).findByCpf(cpf); // Verify CPF lookup

        // If shouldVerifySave is true, verify that the entry-exit record was saved
        if (shouldVerifySave) {
            verify(entryExitRecordRepository).save(any(EntryExitRecordEntity.class));
        }
    }

    // Helper method to assert that the actual response matches the expected response
    private void assertResponse(ValidateEntryExitResponseDto expected, ValidateEntryExitResponseDto actual) {
        assertNotNull(actual); // Assert that the actual response is not null
        assertEquals(expected.message(), actual.message()); // Assert that the messages match
    }

    // Helper method to generate an access denied message for Pharmacy Representative
    private String generateAccessDeniedMessage(PharmacyRepresentativeEntity pharmacyRepresentative) {
        return "Access denied: CPF " + pharmacyRepresentative.getCpf()
                + " with ID " + pharmacyRepresentative.getId() + " has already been granted access";
    }

    // Helper method to generate an access denied message for Laboratory Member
    private String generateAccessDeniedMessage(LaboratoryMemberEntity laboratoryMember) {
        return "Access denied: CPF " + laboratoryMember.getCpf()
                + " with ID " + laboratoryMember.getId() + " has already been granted access";
    }

    // Helper method to generate an exit denied message for Pharmacy Representative
    private String generateExitDeniedMessage(PharmacyRepresentativeEntity pharmacyRepresentative) {
        return "Departure denied: CPF " + pharmacyRepresentative.getCpf()
                + " with ID " + pharmacyRepresentative.getId() + " already had the exit registered";
    }

    // Helper method to generate an exit denied message for Laboratory Member
    private String generateExitDeniedMessage(LaboratoryMemberEntity laboratoryMember) {
        return "Departure denied: CPF " + laboratoryMember.getCpf()
                + " with ID " + laboratoryMember.getId() + " already had the exit registered";
    }

    // Helper method to create a ValidateEntryExitResponseDto with a message
    private ValidateEntryExitResponseDto createValidateEntryExitResponseDto(String message) {
        return new ValidateEntryExitResponseDto(message); // Create and return the response DTO
    }
}
