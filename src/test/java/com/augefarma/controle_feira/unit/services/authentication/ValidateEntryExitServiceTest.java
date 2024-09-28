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

    private final String cpfPharmacyRepresentative = "645.678.789-04";
    private final String cpfLaboratoryMember = "456.789.654-32";
    private final EventSegment eventSegment = EventSegment.BUFFET;
    private boolean shouldVerifySave = true;

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
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setEntryExitRecords(new ArrayList<>());

        setupPharmacyRepresentativeMockAndSaveEntryExitRecord(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);

        assertResponse(expectedResponse, actualResponse);
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateExitForPharmacyRepresentative() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutNotCompleted();

        setupPharmacyRepresentativeMockAndSaveEntryExitRecord(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit recorded successfully");
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfPharmacyRepresentative);

        assertResponse(expectedResponse, actualResponse);
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateEntryForLaboratoryMember() {
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();
        laboratoryMember.setEntryExitRecords(new ArrayList<>());

        setupLaboratoryMemberMockAndSaveEntryExitRecord(this.cpfLaboratoryMember, laboratoryMember);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfLaboratoryMember, this.eventSegment);

        assertResponse(expectedResponse, actualResponse);
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateExitForLaboratoryMember() {
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutNotCompleted();

        setupLaboratoryMemberMockAndSaveEntryExitRecord(this.cpfLaboratoryMember, laboratoryMember);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit recorded successfully");
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfLaboratoryMember);

        assertResponse(expectedResponse, actualResponse);
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }


    @Test
    public void validateEntryExitException_findByCpf() {
        when(pharmacyRepresentativeRepository.findByCpf(anyString()))
                .thenReturn(Optional.empty());
        when(laboratoryMemberRepository.findByCpf(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
           validateEntryExitService.validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);
        });
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateEntry(this.cpfLaboratoryMember, this.eventSegment);
        });
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateExit(this.cpfPharmacyRepresentative);
        });
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryExitService.validateExit(this.cpfLaboratoryMember);
        });
    }

    @Test
    public void validateEntryForPharmacyRepresentative_checkOutCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutCompleted();

        setupPharmacyRepresentativeMockAndSaveEntryExitRecord(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);

        assertResponse(expectedResponse, actualResponse);
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateEntryForLaboratoryMember_checkOutCompleted() {
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutCompleted();

        setupLaboratoryMemberMockAndSaveEntryExitRecord(this.cpfLaboratoryMember, laboratoryMember);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfLaboratoryMember, this.eventSegment);

        assertResponse(expectedResponse, actualResponse);
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateEntryForPharmacyRepresentative_accessDenied() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutNotCompleted();

        setupPharmacyRepresentativeMock(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateAccessDeniedMessage(pharmacyRepresentative));
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfPharmacyRepresentative, this.eventSegment);

        this.shouldVerifySave = false;

        assertResponse(expectedResponse, actualResponse);
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateEntryLaboratoryMember_accessDenied() {
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutNotCompleted();

        setupLaboratoryMemberMock(this.cpfLaboratoryMember, laboratoryMember);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateAccessDeniedMessage(laboratoryMember));
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateEntry(this.cpfLaboratoryMember, this.eventSegment);

        this.shouldVerifySave = false;

        assertResponse(expectedResponse, actualResponse);
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateExitPharmacyRepresentative_exitDeniedListEmpty() {
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setEntryExitRecords(new ArrayList<>());

        setupPharmacyRepresentativeMock(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(pharmacyRepresentative));
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfPharmacyRepresentative);

        this.shouldVerifySave = false;

        assertResponse(expectedResponse, actualResponse);
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateExitLaboratoryMember_exitDeniedListEmpty() {
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();
        laboratoryMember.setEntryExitRecords(new ArrayList<>());

        setupLaboratoryMemberMock(this.cpfLaboratoryMember, laboratoryMember);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(laboratoryMember));
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfLaboratoryMember);

        this.shouldVerifySave = false;

        assertResponse(expectedResponse, actualResponse);
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    @Test
    public void validateExitPharmacyRepresentative_exitDeniedCheckOutCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutCompleted();

        setupPharmacyRepresentativeMock(this.cpfPharmacyRepresentative, pharmacyRepresentative);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(pharmacyRepresentative));
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfPharmacyRepresentative);

        this.shouldVerifySave = false;

        assertResponse(expectedResponse, actualResponse);
        verifyPharmacyRepresentativeInteraction(this.cpfPharmacyRepresentative);
    }

    @Test
    public void validateExitLaboratoryMember_exitDeniedCheckOutCompleted() {
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutCompleted();

        setupLaboratoryMemberMock(this.cpfLaboratoryMember, laboratoryMember);

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(laboratoryMember));
        ValidateEntryExitResponseDto actualResponse = validateEntryExitService
                .validateExit(this.cpfLaboratoryMember);

        this.shouldVerifySave = false;

        assertResponse(expectedResponse, actualResponse);
        verifyLaboratoryMemberInteraction(this.cpfLaboratoryMember);
    }

    private PharmacyRepresentativeEntity createPharmacyRepresentativeWithCheckOutCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutCompleted();
        pharmacyRepresentative.setEntryExitRecords(entryExitRecords);

        return pharmacyRepresentative;
    }

    private LaboratoryMemberEntity createLaboratoryMemberWithCheckOutCompleted() {
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutCompleted();
        laboratoryMember.setEntryExitRecords(entryExitRecords);

        return laboratoryMember;
    }

    private PharmacyRepresentativeEntity createPharmacyRepresentativeWithCheckOutNotCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutNotCompleted();
        pharmacyRepresentative.setEntryExitRecords(entryExitRecords);

        return pharmacyRepresentative;
    }

    private LaboratoryMemberEntity createLaboratoryMemberWithCheckOutNotCompleted() {
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutNotCompleted();
        laboratoryMember.setEntryExitRecords(entryExitRecords);

        return laboratoryMember;
    }

    private List<EntryExitRecordEntity> createEntryExitRecordsWithCheckOutCompleted() {
        return createEntryExitRecords(LocalDateTime.now().minusHours(1));
    }

    private List<EntryExitRecordEntity> createEntryExitRecordsWithCheckOutNotCompleted() {
        return createEntryExitRecords(null);
    }

    private List<EntryExitRecordEntity> createEntryExitRecords(LocalDateTime checkoutTime) {
        EntryExitRecordEntity entryExitRecord = new EntryExitRecordEntity();
        entryExitRecord.setCheckoutTime(checkoutTime);

        List<EntryExitRecordEntity> entryExitRecords = new ArrayList<>();
        entryExitRecords.add(entryExitRecord);

        return entryExitRecords;
    }

    private void setupPharmacyRepresentativeMockAndSaveEntryExitRecord(
            String cpf, PharmacyRepresentativeEntity pharmacyRepresentative) {
        when(pharmacyRepresentativeRepository.findByCpf(cpf))
                .thenReturn(Optional.of(pharmacyRepresentative));
        when(entryExitRecordRepository.save(any(EntryExitRecordEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void setupPharmacyRepresentativeMock(String cpf,
                                                         PharmacyRepresentativeEntity pharmacyRepresentative) {
        when(pharmacyRepresentativeRepository.findByCpf(cpf))
                .thenReturn(Optional.of(pharmacyRepresentative));
    }

    private void setupLaboratoryMemberMockAndSaveEntryExitRecord(
            String cpf, LaboratoryMemberEntity laboratoryMember) {
        when(laboratoryMemberRepository.findByCpf(cpf))
                .thenReturn(Optional.of(laboratoryMember));
        when(entryExitRecordRepository.save(any(EntryExitRecordEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void setupLaboratoryMemberMock(String cpf, LaboratoryMemberEntity laboratoryMember) {
        when(laboratoryMemberRepository.findByCpf(cpf))
                .thenReturn(Optional.of(laboratoryMember));
    }

    private void verifyPharmacyRepresentativeInteraction(String cpf) {
        verify(pharmacyRepresentativeRepository).findByCpf(cpf);

        if (shouldVerifySave) {
            verify(entryExitRecordRepository).save(any(EntryExitRecordEntity.class));
        }
    }

    private void verifyLaboratoryMemberInteraction(String cpf) {
        verify(laboratoryMemberRepository).findByCpf(cpf);

        if (shouldVerifySave) {
            verify(entryExitRecordRepository).save(any(EntryExitRecordEntity.class));
        }
    }

    private void assertResponse(ValidateEntryExitResponseDto expected, ValidateEntryExitResponseDto actual) {
        assertNotNull(actual);
        assertEquals(expected.message(), actual.message());
    }

    private String generateAccessDeniedMessage(PharmacyRepresentativeEntity pharmacyRepresentative) {
        return "Access denied: CPF " + pharmacyRepresentative.getCpf()
                + " with ID " + pharmacyRepresentative.getId() + " has already been granted access";
    }

    private String generateAccessDeniedMessage(LaboratoryMemberEntity laboratoryMember) {
        return "Access denied: CPF " + laboratoryMember.getCpf()
                + " with ID " + laboratoryMember.getId() + " has already been granted access";
    }

    private String generateExitDeniedMessage(PharmacyRepresentativeEntity pharmacyRepresentative) {
        return "Departure denied: CPF " + pharmacyRepresentative.getCpf()
                + " with ID " + pharmacyRepresentative.getId() + " already had the exit registered";
    }

    private String generateExitDeniedMessage(LaboratoryMemberEntity laboratoryMember) {
        return "Departure denied: CPF " + laboratoryMember.getCpf()
                + " with ID " + laboratoryMember.getId() + " already had the exit registered";
    }

    private ValidateEntryExitResponseDto createValidateEntryExitResponseDto(String message) {
        return new ValidateEntryExitResponseDto(message);
    }
}
