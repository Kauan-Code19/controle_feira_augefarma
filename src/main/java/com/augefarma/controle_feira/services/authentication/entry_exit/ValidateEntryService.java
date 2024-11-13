package com.augefarma.controle_feira.services.authentication.entry_exit;

import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryRecordRepository;
import com.augefarma.controle_feira.repositories.participant.ParticipantRepository;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ValidateEntryService {
    private final EntryRecordRepository entryRecordRepository;
    private final ParticipantRepository participantRepository;
    private final RealTimeUpdateService realTimeUpdateService;

    @Autowired
    public ValidateEntryService(EntryRecordRepository entryRecordRepository,
                                ParticipantRepository participantRepository,
                                RealTimeUpdateService realTimeUpdateService) {
        this.entryRecordRepository = entryRecordRepository;
        this.participantRepository = participantRepository;
        this.realTimeUpdateService = realTimeUpdateService;
    }

    public ValidateEntryExitResponseDto validateEntryFair(String cpf, EventSegment eventSegment) {
        ParticipantEntity participant = getParticipantByCpf(cpf);
        return handleCheckInFair(participant, eventSegment);
    }


    @Transactional(readOnly = true)
    private ParticipantEntity getParticipantByCpf(String cpf) {
        return participantRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum participante encontrado"));
    }


    private ValidateEntryExitResponseDto handleCheckInFair(ParticipantEntity participant, EventSegment eventSegment) {
        List<EntryRecordEntity> previousEntries = hasPreviousEntryForFair(participant);
        return performCheckIn(participant, eventSegment, previousEntries);
    }


    private List<EntryRecordEntity> hasPreviousEntryForFair(ParticipantEntity participant) {
        return  participant.getEntryRecords()
                .stream()
                .filter(entry -> entry.getEventSegment() == EventSegment.FAIR
                        && entry.getCheckinTime().toLocalDate().isEqual(LocalDate.now())).toList();
    }


    private ValidateEntryExitResponseDto performCheckIn(ParticipantEntity participant, EventSegment eventSegment,
                                                        List<EntryRecordEntity> previousEntries) {
        if (!previousEntries.isEmpty()) {
            createEntryRecord(participant, eventSegment);
            return buildAccessGrantedResponse(participant, true);
        }

        callRealtimeUpdateService(participant);
        createEntryRecord(participant, eventSegment);
        return buildAccessGrantedResponse(participant, false);
    }


    @Transactional
    private void createEntryRecord(ParticipantEntity participant,
                                   EventSegment eventSegment) {
        EntryRecordEntity entryRecord = new EntryRecordEntity();
        entryRecord.setCheckinTime(LocalDateTime.now());
        entryRecord.setEventSegment(eventSegment);
        entryRecord.setParticipant(participant);

        entryRecordRepository.save(entryRecord);
    }


    private void callRealtimeUpdateService(ParticipantEntity participant) {
        participant.addToRealtimeUpdateService(realTimeUpdateService);
    }


    private ValidateEntryExitResponseDto buildAccessGrantedResponse(ParticipantEntity participant,
                                                                    boolean hasPreviousEntry) {
        if (hasPreviousEntry) {
            return new ValidateEntryExitResponseDto("Acesso concedido: CPF " + participant.getCpf()
                    + " com ID " + participant.getId() + " já possui um ou mais registros de acesso");
        }

        return new ValidateEntryExitResponseDto("Acesso concedido");
    }
}
