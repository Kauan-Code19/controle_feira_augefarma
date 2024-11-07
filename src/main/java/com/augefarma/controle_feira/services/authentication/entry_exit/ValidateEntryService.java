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

    private ValidateEntryExitResponseDto handleCheckInFair(
            ParticipantEntity participant, EventSegment eventSegment) {

        List<EntryRecordEntity> entryRecordsList = participant.getEntryRecords()
                .stream().filter(entry -> entry.getEventSegment() == EventSegment.FAIR)
                .toList();

        if (!entryRecordsList.isEmpty()) {
            return new ValidateEntryExitResponseDto("Acesso negado: CPF " + participant.getCpf()
                    + " com ID " + participant.getId() + " já teve o acesso concedido");
        }

        return performCheckIn(participant, eventSegment);
    }

    private ValidateEntryExitResponseDto performCheckIn(
            ParticipantEntity participant, EventSegment eventSegment) {

        createEntryRecord(participant, eventSegment);

        callRealtimeUpdateService(participant);

        return new ValidateEntryExitResponseDto("Acesso concedido");
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
}
