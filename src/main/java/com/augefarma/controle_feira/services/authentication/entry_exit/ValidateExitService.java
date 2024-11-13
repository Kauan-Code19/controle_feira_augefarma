package com.augefarma.controle_feira.services.authentication.entry_exit;

import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.entry_exit.ExitRecordEntity;
import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.ExitRecordRepository;
import com.augefarma.controle_feira.repositories.participant.ParticipantRepository;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ValidateExitService {

    private final RealTimeUpdateService realTimeUpdateService;
    private final ExitRecordRepository exitRecordRepository;
    private final ParticipantRepository participantRepository;

    @Autowired
    public ValidateExitService(RealTimeUpdateService realTimeUpdateService, ExitRecordRepository exitRecordRepository,
                               ParticipantRepository participantRepository) {
        this.realTimeUpdateService = realTimeUpdateService;
        this.exitRecordRepository = exitRecordRepository;
        this.participantRepository = participantRepository;
    }

    public ValidateEntryExitResponseDto validateExitBuffet(String cpf, EventSegment eventSegment) {
        ParticipantEntity participant = getParticipantByCpf(cpf);
        return handleCheckOutBuffet(participant, eventSegment);
    }


    @Transactional(readOnly = true)
    private ParticipantEntity getParticipantByCpf(String cpf) {
        return participantRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum participante encontrado"));
    }


    private ValidateEntryExitResponseDto handleCheckOutBuffet(
            ParticipantEntity participant, EventSegment eventSegment) {

        List<EntryRecordEntity> previousEntries = hasPreviousEntryForFair(participant);
        List<ExitRecordEntity> previousExits = hasPreviousExitForBuffet(participant);

        if (previousEntries.isEmpty()) {
            return new ValidateEntryExitResponseDto("Saída negado: CPF " + participant.getCpf()
                    + " com ID " + participant.getId() + " usuário sem registro de check-in");
        }

        return performCheckOut(participant, eventSegment, previousExits);
    }


    private List<EntryRecordEntity> hasPreviousEntryForFair(ParticipantEntity participant) {
        return  participant.getEntryRecords()
                .stream()
                .filter(entry -> entry.getEventSegment() == EventSegment.FAIR
                        && entry.getCheckinTime().toLocalDate().isEqual(LocalDate.now())).toList();
    }

    private List<ExitRecordEntity> hasPreviousExitForBuffet(ParticipantEntity participant) {
        return  participant.getExitRecords()
                .stream()
                .filter(exit -> exit.getEventSegment() == EventSegment.BUFFET
                        && exit.getCheckoutTime().toLocalDate().isEqual(LocalDate.now())).toList();
    }

    private ValidateEntryExitResponseDto performCheckOut(ParticipantEntity participant, EventSegment eventSegment,
                                                         List<ExitRecordEntity> previousEntries) {

        if (!previousEntries.isEmpty()) {
            createExitRecord(participant, eventSegment);
            return buildRegisteredExitResponse(participant, true);
        }

        callRealtimeUpdateService(participant);
        createExitRecord(participant, eventSegment);
        return buildRegisteredExitResponse(participant, false);
    }


    private void callRealtimeUpdateService(ParticipantEntity participant) {
        participant.removeToRealtimeUpdateService(realTimeUpdateService);
    }


    @Transactional
    private void createExitRecord(ParticipantEntity participant,
                                  EventSegment eventSegment) {
        ExitRecordEntity exitRecord = new ExitRecordEntity();
        exitRecord.setCheckoutTime(LocalDateTime.now());
        exitRecord.setEventSegment(eventSegment);
        exitRecord.setParticipant(participant);

        exitRecordRepository.save(exitRecord);
    }


    private ValidateEntryExitResponseDto buildRegisteredExitResponse(ParticipantEntity participant,
                                                                    boolean hasPreviousExit) {
        if (hasPreviousExit) {
            return new ValidateEntryExitResponseDto("Saída registrada: CPF " + participant.getCpf()
                    + " com ID " + participant.getId() + " já possui um ou mais registros de saída");
        }

        return new ValidateEntryExitResponseDto("Saída registrada");
    }
}
