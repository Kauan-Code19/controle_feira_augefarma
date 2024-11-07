package com.augefarma.controle_feira.integration.services.authentication.entry_exit;

import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.entry_exit.ExitRecordEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryRecordRepository;
import com.augefarma.controle_feira.repositories.entry_exit.ExitRecordRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.repositories.participant.ParticipantRepository;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.authentication.entry_exit.ValidateExitService;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ValidateExitServiceTest {
    List<EntryRecordEntity> entryRecords = new ArrayList<>();

    private LaboratoryMemberEntity laboratoryMember;
    private PharmacyRepresentativeEntity pharmacyRepresentative;
    private LaboratoryEntity laboratory;

    @Autowired
    private ExitRecordRepository exitRecordRepository;

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
    private ValidateExitService validateExitService;

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
        exitRecordRepository.deleteAll();
        participantRepository.deleteAll();
        laboratoryRepository.deleteAll();
        realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives().clear();
        realTimeUpdateService.getEntitiesListResponseDto().getLaboratoryMembers().clear();
    }


    // Testes usando a subclasse PharmacyRepresentative

    @Test
    public void validateExitBuffetSuccessForPharmacyRepresentative() {
        this.pharmacyRepresentative.getEntryRecords().add(createAndSaveEntryRecord(this.pharmacyRepresentative));
        this.pharmacyRepresentative.addToRealtimeUpdateService(realTimeUpdateService);

        ValidateEntryExitResponseDto response = validateExitService.validateExitBuffet(
                this.pharmacyRepresentative.getCpf(), EventSegment.BUFFET);

        assertResponseMessage(response, "Saída registrada");
        verifyExitRecordSavedForPharmacyRepresentative();
        verifyRealTimeUpdateServiceForPharmacyRepresentative();
    }


    private EntryRecordEntity createAndSaveEntryRecord(PharmacyRepresentativeEntity pharmacyRepresentative) {
        EntryRecordEntity entryRecord = new EntryRecordEntity();
        entryRecord.setCheckinTime(LocalDateTime.now());
        entryRecord.setEventSegment(EventSegment.FAIR);
        entryRecord.setParticipant(pharmacyRepresentative);

        return entryRecordRepository.save(entryRecord);
    }


    private void verifyExitRecordSavedForPharmacyRepresentative() {
        List<ExitRecordEntity> records = exitRecordRepository.findAll();
        ParticipantEntity lastParticipantRecords = records.get(records.size() - 1).getParticipant();
        assertEquals(this.pharmacyRepresentative, lastParticipantRecords);
    }


    @Test
    public void validateExitBuffetForPharmacyRepresentativeAccessDenied_EntryRecordEmpty() {
        ValidateEntryExitResponseDto response = validateExitService.validateExitBuffet(
                this.pharmacyRepresentative.getCpf(), EventSegment.BUFFET);

        assertResponseMessage(response, "Acesso negado: CPF " + this.pharmacyRepresentative.getCpf() +
                " com ID " + this.pharmacyRepresentative.getId() + " usuário sem registro de check-in");
        verifyExitRecordNotSavedForPharmacyRepresentative();
        verifyRealTimeUpdateServiceForPharmacyRepresentative();
    }


    private void verifyExitRecordNotSavedForPharmacyRepresentative() {
        List<EntryRecordEntity> recordsEventSegmentFair = this.pharmacyRepresentative.getEntryRecords().stream()
                .filter(entry -> entry.getEventSegment() == EventSegment.FAIR).toList();

        assertEquals(0, recordsEventSegmentFair.size());
    }


    private void verifyRealTimeUpdateServiceForPharmacyRepresentative() {
        assertFalse(realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives()
                .contains(createpharmacyRepresentativeResponseDto()));
    }


    private PharmacyRepresentativeResponseDto createpharmacyRepresentativeResponseDto() {
        return new PharmacyRepresentativeResponseDto(this.pharmacyRepresentative);
    }


    // Teste usando a subclasse LaboratoryMember

    @Test
    public void validateEntryBuffetSuccessForLaboratoryMember() {
        this.laboratoryMember.getEntryRecords().add(createAndSaveExitRecord(this.laboratoryMember));
        this.laboratoryMember.addToRealtimeUpdateService(realTimeUpdateService);

        ValidateEntryExitResponseDto response = validateExitService.validateExitBuffet(
                this.laboratoryMember.getCpf(), EventSegment.BUFFET);

        assertResponseMessage(response, "Saída registrada");
        verifyExitRecordSavedForLaboratoryMember();
        verifyRealTimeUpdateServiceForLaboratoryMember();
    }


    private EntryRecordEntity createAndSaveExitRecord(LaboratoryMemberEntity laboratoryMember) {
        EntryRecordEntity entryRecord = new EntryRecordEntity();
        entryRecord.setCheckinTime(LocalDateTime.now());
        entryRecord.setEventSegment(EventSegment.FAIR);
        entryRecord.setParticipant(laboratoryMember);

        return entryRecordRepository.save(entryRecord);
    }


    private void verifyExitRecordSavedForLaboratoryMember() {
        List<ExitRecordEntity> records = exitRecordRepository.findAll();
        assertEquals(1, records.size());
        assertEquals(this.laboratoryMember, records.get(0).getParticipant());
    }


    @Test
    public void validateExitBuffetForLaboratoryMemberAccessDenied_EntryRecordEmpty() {
        ValidateEntryExitResponseDto response = validateExitService.validateExitBuffet(
                this.laboratoryMember.getCpf(), EventSegment.BUFFET);

        assertResponseMessage(response, "Acesso negado: CPF " + this.laboratoryMember.getCpf() +
                " com ID " + this.laboratoryMember.getId() + " usuário sem registro de check-in");
        verifyEntryRecordNotSavedForLaboratoryMember();
        verifyRealTimeUpdateServiceForLaboratoryMember();
    }


    private void verifyEntryRecordNotSavedForLaboratoryMember() {
        List<EntryRecordEntity> recordsEventSegmentFair = this.laboratoryMember.getEntryRecords().stream()
                .filter(entry -> entry.getEventSegment() == EventSegment.FAIR).toList();

        assertEquals(0, recordsEventSegmentFair.size());
    }


    private void verifyRealTimeUpdateServiceForLaboratoryMember() {
        assertFalse(realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives()
                .contains(createpharmacyRepresentativeResponseDto()));
    }


    private LaboratoryMemberResponseDto createLaboratoryMemberResponseDto() {
        return new LaboratoryMemberResponseDto(this.laboratoryMember);
    }


    // Teste para qualquer subclasse

    @Test
    public void validateExitFairFailed_CPFNotAssociated() {
        assertThrows(ResourceNotFoundException.class, () -> {
            validateExitService.validateExitBuffet("123.456.789-00", EventSegment.BUFFET);
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
