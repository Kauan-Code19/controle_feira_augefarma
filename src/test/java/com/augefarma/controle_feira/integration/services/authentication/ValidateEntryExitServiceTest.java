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

    private final String cpfPharmacyRepresentative = "645.678.789-04";
    private final String cpfLaboratoryMember = "456.789.654-32";
    private final EventSegment eventSegment = EventSegment.BUFFET;
    private final String laboratoryCorporateReason = "VR Lima";

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
        PharmacyRepresentativeEntity pharmacyRepresentative = createAndSavePharmacyRepresentative("Kauan",
                "567.456.754-67", "67.678.678/4675-89", this.laboratoryCorporateReason);

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        ValidateEntryExitResponseDto accessMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        assertResponse(expectedResponse, accessMessage);
    }

    @Test
    public void validateExitForPharmacyRepresentative() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutNotCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit recorded successfully");
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateEntryForLaboratoryMember() {
        LaboratoryMemberEntity laboratoryMember = createAndSaveLaboratoryMember("Kauan", "783.342.353-54",
                this.laboratoryCorporateReason);

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        ValidateEntryExitResponseDto accessMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        assertResponse(expectedResponse, accessMessage);
    }

    @Test
    public void validateExitForLaboratoryMember() {
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutNotCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Exit recorded successfully");
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateEntryExitException_findByCpf() {
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

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        ValidateEntryExitResponseDto accessMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        assertResponse(expectedResponse, accessMessage);
    }

    @Test
    public void validateEntryForLaboratoryMember_checkOutCompleted() {
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto("Access granted");
        ValidateEntryExitResponseDto accessMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        assertResponse(expectedResponse, accessMessage);
    }

    @Test
    public void validateEntryForPharmacyRepresentative_accessDenied() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutNotCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateAccessDeniedMessage(pharmacyRepresentative));
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateEntryForLaboratoryMember_accessDenied() {
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutNotCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateAccessDeniedMessage(laboratoryMember));
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), this.eventSegment);

        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateExitPharmacyRepresentative_exitDeniedListEmpty() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createAndSavePharmacyRepresentative("Kauan",
                "567.456.754-67", "67.678.678/4675-89", this.laboratoryCorporateReason);

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(pharmacyRepresentative));
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateExitLaboratoryMember_exitDeniedListEmpty() {
        LaboratoryMemberEntity laboratoryMember = createAndSaveLaboratoryMember("Kauan",
                "567.456.754-67", this.laboratoryCorporateReason);

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(laboratoryMember));
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateExitPharmacyRepresentative_exitDeniedCheckOutCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createPharmacyRepresentativeWithCheckOutCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(pharmacyRepresentative.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(pharmacyRepresentative));
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        assertResponse(expectedResponse, deniedMessage);
    }

    @Test
    public void validateExitLaboratoryMember_exitDeniedCheckOutCompleted() {
        LaboratoryMemberEntity laboratoryMember = createLaboratoryMemberWithCheckOutCompleted();

        CpfEntityDto cpfEntityDto = new CpfEntityDto(laboratoryMember.getCpf());

        ValidateEntryExitResponseDto expectedResponse = createValidateEntryExitResponseDto(
                generateExitDeniedMessage(laboratoryMember));
        ValidateEntryExitResponseDto deniedMessage = validateEntryExitService
                .validateExit(cpfEntityDto.cpf());

        assertResponse(expectedResponse, deniedMessage);
    }

    private PharmacyRepresentativeEntity createPharmacyRepresentativeWithCheckOutCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createAndSavePharmacyRepresentative("Kauan",
                "567.456.754-67", "67.678.678/4675-89", this.laboratoryCorporateReason);

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutCompleted(pharmacyRepresentative);

        pharmacyRepresentative.setEntryExitRecords(entryExitRecords);

        return pharmacyRepresentative;
    }

    private LaboratoryMemberEntity createLaboratoryMemberWithCheckOutCompleted() {
        LaboratoryMemberEntity laboratoryMember = createAndSaveLaboratoryMember("Kauan",
                "567.456.754-67", this.laboratoryCorporateReason);

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutCompleted(laboratoryMember);

        laboratoryMember.setEntryExitRecords(entryExitRecords);

        return laboratoryMember;
    }

    private PharmacyRepresentativeEntity createPharmacyRepresentativeWithCheckOutNotCompleted() {
        PharmacyRepresentativeEntity pharmacyRepresentative = createAndSavePharmacyRepresentative("Kauan",
                "567.456.754-67", "67.678.678/4675-89", this.laboratoryCorporateReason);

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutNotCompleted(pharmacyRepresentative);
        pharmacyRepresentative.setEntryExitRecords(entryExitRecords);

        return pharmacyRepresentative;
    }

    private LaboratoryMemberEntity createLaboratoryMemberWithCheckOutNotCompleted() {
        LaboratoryMemberEntity laboratoryMember = createAndSaveLaboratoryMember("Kauan",
                "567.456.754-67", this.laboratoryCorporateReason);

        List<EntryExitRecordEntity> entryExitRecords = createEntryExitRecordsWithCheckOutNotCompleted(laboratoryMember);
        laboratoryMember.setEntryExitRecords(entryExitRecords);

        return laboratoryMember;
    }

    private PharmacyRepresentativeEntity createAndSavePharmacyRepresentative(String name, String cpf,
                                                                             String cnpj, String corporateReason) {
        PharmacyRepresentativeEntity pharmacyRepresentative = new PharmacyRepresentativeEntity();

        pharmacyRepresentative.setName(name);
        pharmacyRepresentative.setCpf(cpf);
        pharmacyRepresentative.setCnpj(cnpj);
        pharmacyRepresentative.setCorporateReason(corporateReason);
        pharmacyRepresentative.setEntryExitRecords(new ArrayList<>());

        return pharmacyRepresentativeRepository.save(pharmacyRepresentative);
    }

    private LaboratoryMemberEntity createAndSaveLaboratoryMember(String name, String cpf, String laboratory) {
        LaboratoryMemberEntity laboratoryMember = new LaboratoryMemberEntity();
        createLaboratory();

        LaboratoryEntity laboratoryEntity = laboratoryRepository.findByCorporateReason(laboratory)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found"));

        laboratoryMember.setName(name);
        laboratoryMember.setCpf(cpf);
        laboratoryMember.setLaboratory(laboratoryEntity);
        laboratoryMember.setEntryExitRecords(new ArrayList<>());

        return laboratoryMemberRepository.save(laboratoryMember);
    }

    private void assertResponse(ValidateEntryExitResponseDto expected, ValidateEntryExitResponseDto actual) {
        assertNotNull(actual);
        assertEquals(expected.message(), actual.message());
    }

    private void createLaboratory() {
        LaboratoryEntity laboratory = new LaboratoryEntity();

        laboratory.setCorporateReason(this.laboratoryCorporateReason);
        laboratory.setMembers(new ArrayList<>());

        laboratoryRepository.save(laboratory);
    }

    private List<EntryExitRecordEntity> createEntryExitRecordsWithCheckOutCompleted(Object entity) {
        return createEntryExitRecords(LocalDateTime.now().minusHours(1), entity);
    }

    private List<EntryExitRecordEntity> createEntryExitRecordsWithCheckOutNotCompleted(Object entity) {
        return createEntryExitRecords(null, entity);
    }

    private List<EntryExitRecordEntity> createEntryExitRecords(LocalDateTime checkoutTime, Object entity) {
        EntryExitRecordEntity entryExitRecord = new EntryExitRecordEntity();
        entryExitRecord.setCheckinTime(LocalDateTime.now());
        entryExitRecord.setCheckoutTime(checkoutTime);
        entryExitRecord.setEventSegment(this.eventSegment);

        if (entity instanceof PharmacyRepresentativeEntity) {
            entryExitRecord.setPharmacyRepresentative((PharmacyRepresentativeEntity) entity);
            entryExitRecord.setLaboratoryMember(null);
        }

        if (entity instanceof LaboratoryMemberEntity) {
            entryExitRecord.setLaboratoryMember((LaboratoryMemberEntity) entity);
            entryExitRecord.setPharmacyRepresentative(null);
        }

        List<EntryExitRecordEntity> entryExitRecords = new ArrayList<>();
        entryExitRecords.add(entryExitRecord);

        return entryExitRecords;
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
