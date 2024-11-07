package com.augefarma.controle_feira.integration.services.authentication.entry_exit;

import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryRecordRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.repositories.participant.ParticipantRepository;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.authentication.entry_exit.ValidateEntryService;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ValidateEntryServiceTest {
    List<EntryRecordEntity> entryRecords = new ArrayList<>();

    private LaboratoryMemberEntity laboratoryMember;
    private PharmacyRepresentativeEntity pharmacyRepresentative;
    private LaboratoryEntity laboratory;

    @Autowired
    private EntryRecordRepository entryRecordRepository;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private LaboratoryMemberRepository laboratoryMemberRepository;

    @Autowired
    private PharmacyRepresentativeRepository pharmacyRepresentativeRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ValidateEntryService validateEntryService;

    @Autowired
    private RealTimeUpdateService realTimeUpdateService;

    @BeforeEach
    void setup() {
        createAndSaveLaboratory();
        createAndSaveLaboratoryMember();
        createAndSavePharmacyRepresentative();
    }

    @AfterEach
    void clean() {
        entryRecordRepository.deleteAll();
        participantRepository.deleteAll();
        laboratoryRepository.deleteAll();
        realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives().clear();
        realTimeUpdateService.getEntitiesListResponseDto().getLaboratoryMembers().clear();
    }

    // Testes usando a subclasse PharmacyRepresentative

    @Test
    public void validateEntryFairSuccessForPharmacyRepresentative() {
        ValidateEntryExitResponseDto response = validateEntryService.validateEntryFair(
                this.pharmacyRepresentative.getCpf(), EventSegment.FAIR);

        assertResponseMessage(response, "Acesso concedido");
        verifyEntryRecordSavedForPharmacyRepresentative();
        verifyRealTimeUpdateServiceForPharmacyRepresentative();
    }


    private void verifyEntryRecordSavedForPharmacyRepresentative() {
        List<EntryRecordEntity> records = entryRecordRepository.findAll();
        ParticipantEntity lastParticipantRecords = records.get(records.size() - 1).getParticipant();
        assertEquals(this.pharmacyRepresentative, lastParticipantRecords);
    }


    @Test
    public void validateEntryFairForPharmacyRepresentativeAccessDenied_EntryRecordNotEmpty() {
        this.pharmacyRepresentative.getEntryRecords().add(createAndSaveEntryRecord(this.pharmacyRepresentative));
        this.pharmacyRepresentative.addToRealtimeUpdateService(realTimeUpdateService);

        ValidateEntryExitResponseDto response = validateEntryService.validateEntryFair(
                this.pharmacyRepresentative.getCpf(), EventSegment.FAIR);

        assertResponseMessage(response, "Acesso negado: CPF " + this.pharmacyRepresentative.getCpf() +
                " com ID " + this.pharmacyRepresentative.getId() + " já teve o acesso concedido");
        verifyEntryRecordNotSavedForPharmacyRepresentative();
        verifyRealTimeUpdateServiceForPharmacyRepresentative();
    }


    private EntryRecordEntity createAndSaveEntryRecord(PharmacyRepresentativeEntity pharmacyRepresentative) {
        EntryRecordEntity entryRecord = new EntryRecordEntity();
        entryRecord.setCheckinTime(LocalDateTime.now());
        entryRecord.setEventSegment(EventSegment.FAIR);
        entryRecord.setParticipant(pharmacyRepresentative);

        return entryRecordRepository.save(entryRecord);
    }


    private void verifyEntryRecordNotSavedForPharmacyRepresentative() {
        List<EntryRecordEntity> recordsEventSegmentFair = this.pharmacyRepresentative.getEntryRecords().stream()
                .filter(entry -> entry.getEventSegment() == EventSegment.FAIR).toList();

        assertEquals(1, recordsEventSegmentFair.size());
    }


    private void verifyRealTimeUpdateServiceForPharmacyRepresentative() {
        assertTrue(realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives()
                .contains(createpharmacyRepresentativeResponseDto()));
    }


    private PharmacyRepresentativeResponseDto createpharmacyRepresentativeResponseDto() {
        return new PharmacyRepresentativeResponseDto(this.pharmacyRepresentative);
    }


    // Teste usando a subclasse LaboratoryMember

    @Test
    public void validateEntryFairSuccessForLaboratoryMember() {
        ValidateEntryExitResponseDto response = validateEntryService.validateEntryFair(
                this.laboratoryMember.getCpf(), EventSegment.FAIR);

        assertResponseMessage(response, "Acesso concedido");
        verifyEntryRecordSavedForLaboratoryMember();
        verifyRealTimeUpdateServiceForLaboratoryMember();
    }


    private void verifyEntryRecordSavedForLaboratoryMember() {
        List<EntryRecordEntity> records = entryRecordRepository.findAll();
        assertEquals(1, records.size());
        assertEquals(this.laboratoryMember, records.get(0).getParticipant());
    }


    @Test
    public void validateEntryFairForLaboratoryMemberAccessDenied_EntryRecordNotEmpty() {
        this.laboratoryMember.getEntryRecords().add(createAndSaveEntryRecord(this.laboratoryMember));
        this.laboratoryMember.addToRealtimeUpdateService(realTimeUpdateService);

        ValidateEntryExitResponseDto response = validateEntryService.validateEntryFair(
                this.laboratoryMember.getCpf(), EventSegment.FAIR);

        assertResponseMessage(response, "Acesso negado: CPF " + this.laboratoryMember.getCpf() +
                " com ID " + this.laboratoryMember.getId() + " já teve o acesso concedido");
        verifyEntryRecordNotSavedForLaboratoryMember();
        verifyRealTimeUpdateServiceForLaboratoryMember();
    }


    private EntryRecordEntity createAndSaveEntryRecord(LaboratoryMemberEntity laboratoryMember) {
        EntryRecordEntity entryRecord = new EntryRecordEntity();
        entryRecord.setCheckinTime(LocalDateTime.now());
        entryRecord.setEventSegment(EventSegment.FAIR);
        entryRecord.setParticipant(laboratoryMember);

        return entryRecordRepository.save(entryRecord);
    }


    private void verifyEntryRecordNotSavedForLaboratoryMember() {
        List<EntryRecordEntity> recordsEventSegmentFair = this.laboratoryMember.getEntryRecords().stream()
                .filter(entry -> entry.getEventSegment() == EventSegment.FAIR).toList();

        assertEquals(1, recordsEventSegmentFair.size());
    }


    private void verifyRealTimeUpdateServiceForLaboratoryMember() {
        assertTrue(realTimeUpdateService.getEntitiesListResponseDto().getLaboratoryMembers()
                .contains(createLaboratoryMemberResponseDto()));
    }


    private LaboratoryMemberResponseDto createLaboratoryMemberResponseDto() {
        return new LaboratoryMemberResponseDto(this.laboratoryMember);
    }


    // Teste para qualquer subclasse

    @Test
    public void validateEntryFairFailed_CPFNotAssociated() {
        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryService.validateEntryFair("123.456.789-00", EventSegment.FAIR);
        });
    }


    // Método usado para ambos os Testes

    private void assertResponseMessage(ValidateEntryExitResponseDto response, String expectedMessage) {
        assertEquals(expectedMessage, response.message());
    }


    // Criação das Entidades para os Testes

    private void createAndSaveLaboratory() {
        this.laboratory = new LaboratoryEntity();
        this.laboratory.setCorporateReason("Teste Labor");
        this.laboratory.setMembers(new ArrayList<>());

        laboratoryRepository.save(this.laboratory);
    }


    private void createAndSaveLaboratoryMember() {
        this.laboratoryMember = new LaboratoryMemberEntity(this.laboratory);
        this.laboratoryMember.setName("Laboratory Member Test");
        this.laboratoryMember.setCpf("111.111.111-11");
        this.laboratoryMember.setEntryRecords(entryRecords);

        participantRepository.save(this.laboratoryMember);
    }


    private void createAndSavePharmacyRepresentative() {
        this.pharmacyRepresentative = new PharmacyRepresentativeEntity("00.000.000/0000-00",
                "Pharmacy Representative Test");
        this.pharmacyRepresentative.setName("Pharmacy Representative");
        this.pharmacyRepresentative.setCpf("000.000.000-00");
        this.pharmacyRepresentative.setEntryRecords(entryRecords);

        participantRepository.save(this.pharmacyRepresentative);
    }
}
