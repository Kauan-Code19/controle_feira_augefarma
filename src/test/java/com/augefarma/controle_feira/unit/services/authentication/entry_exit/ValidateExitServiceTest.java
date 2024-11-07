package com.augefarma.controle_feira.unit.services.authentication.entry_exit;

import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.entry_exit.ExitRecordEntity;
import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.ExitRecordRepository;
import com.augefarma.controle_feira.repositories.participant.ParticipantRepository;
import com.augefarma.controle_feira.services.authentication.entry_exit.ValidateExitService;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ValidateExitServiceTest {
    List<EntryRecordEntity> entryRecords = new ArrayList<>();

    @Mock
    private ParticipantEntity participant;

    @Mock
    private ExitRecordRepository exitRecordRepository;

    @Mock
    private RealTimeUpdateService realTimeUpdateService;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ValidateExitService validateEntryService;

    @BeforeEach
    void setup() {
        when(this.participant.getCpf()).thenReturn("000.000.000-00");

        when(this.participant.getEntryRecords()).thenReturn(this.entryRecords);

        participant.getEntryRecords().add(createEntryRecord());
    }

    @Test
    public void validateExitBuffetSuccess() {
        when(participantRepository.findByCpf(this.participant.getCpf()))
                .thenReturn(Optional.of(this.participant));

        ValidateEntryExitResponseDto response = validateEntryService.validateExitBuffet(this.participant.getCpf(),
                EventSegment.BUFFET);

        assertResponseMessage(response, "Saída registrada");
        verifyExitRecordSaved();
        verifyRealTimeUpdateServiceCalled();
    }

    private void verifyExitRecordSaved() {
        verify(exitRecordRepository, times(1)).save(any(ExitRecordEntity.class));
    }

    private void verifyRealTimeUpdateServiceCalled() {
        verify(this.participant, times(1)).removeToRealtimeUpdateService(realTimeUpdateService);
    }

    @Test
    public void validateExitBuffetFailed_CPFNotAssociated() {
        when(participantRepository.findByCpf(this.participant.getCpf()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            validateEntryService.validateExitBuffet(this.participant.getCpf(), EventSegment.BUFFET);
        });
    }

    @Test
    public void validateExitBuffetAccessDenied_EntryRecordEmpty() {
        participant.getEntryRecords().clear();

        when(participantRepository.findByCpf(this.participant.getCpf()))
                .thenReturn(Optional.of(this.participant));

        ValidateEntryExitResponseDto response = validateEntryService.validateExitBuffet(this.participant.getCpf(),
                EventSegment.BUFFET);

        assertResponseMessage(response, "Acesso negado: CPF " + participant.getCpf() +
                " com ID " + participant.getId() + " usuário sem registro de check-in");
        verifyExitRecordNotSaved();
        verifyRealTimeUpdateServiceNotCalled();
    }

    private void verifyExitRecordNotSaved() {
        verify(exitRecordRepository, never()).save(any(ExitRecordEntity.class));
    }

    private void verifyRealTimeUpdateServiceNotCalled() {
        verify(participant, never()).removeToRealtimeUpdateService(realTimeUpdateService);
    }

    private void assertResponseMessage(ValidateEntryExitResponseDto response, String expectedMessage) {
        assertEquals(expectedMessage, response.message());
    }

    private EntryRecordEntity createEntryRecord() {
        EntryRecordEntity entryRecord = new EntryRecordEntity();
        entryRecord.setCheckinTime(LocalDateTime.now());
        entryRecord.setEventSegment(EventSegment.FAIR);
        entryRecord.setParticipant(this.participant);

        return entryRecord;
    }
}
