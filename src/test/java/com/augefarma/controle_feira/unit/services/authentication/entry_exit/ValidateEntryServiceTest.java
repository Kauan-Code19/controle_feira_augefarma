package com.augefarma.controle_feira.unit.services.authentication.entry_exit;

import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryRecordRepository;
import com.augefarma.controle_feira.repositories.participant.ParticipantRepository;
import com.augefarma.controle_feira.services.authentication.entry_exit.ValidateEntryService;
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
public class ValidateEntryServiceTest {
    List<EntryRecordEntity> entryRecords = new ArrayList<>();

    @Mock
    private ParticipantEntity participant;

    @Mock
    private EntryRecordRepository entryRecordRepository;

    @Mock
    private RealTimeUpdateService realTimeUpdateService;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ValidateEntryService validateEntryService;

    @BeforeEach
    void setup() {
        when(this.participant.getCpf()).thenReturn("000.000.000-00");
    }

    @Test
    public void validateEntryFairSuccess() {
        when(participantRepository.findByCpf(this.participant.getCpf()))
                .thenReturn(Optional.of(this.participant));

        ValidateEntryExitResponseDto response = validateEntryService.validateEntryFair(this.participant.getCpf(),
                EventSegment.FAIR);

        assertResponseMessage(response, "Acesso concedido");
        verifyEntryRecordSaved();
        verifyRealTimeUpdateServiceCalled();
    }

    private void verifyEntryRecordSaved() {
        verify(entryRecordRepository, times(1)).save(any(EntryRecordEntity.class));
    }

    private void verifyRealTimeUpdateServiceCalled() {
        verify(this.participant, times(1)).addToRealtimeUpdateService(realTimeUpdateService);
    }

    @Test
    public void validateEntryFairFailed_CPFNotAssociated() {
        when(participantRepository.findByCpf(this.participant.getCpf()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
           validateEntryService.validateEntryFair(this.participant.getCpf(), EventSegment.FAIR);
        });
    }

    @Test
    public void validateEntryFairAccessDenied_EntryRecordNotEmpty() {
        when(this.participant.getEntryRecords()).thenReturn(this.entryRecords);

        participant.getEntryRecords().add(createEntryRecord());

        when(participantRepository.findByCpf(this.participant.getCpf()))
                .thenReturn(Optional.of(this.participant));

        ValidateEntryExitResponseDto response = validateEntryService.validateEntryFair(this.participant.getCpf(),
                EventSegment.FAIR);

        assertResponseMessage(response, "Acesso negado: CPF " + participant.getCpf() +
                " com ID " + participant.getId() + " já teve o acesso concedido");
        verifyEntryRecordNotSaved();
        verifyRealTimeUpdateServiceNotCalled();
    }

    private EntryRecordEntity createEntryRecord() {
        EntryRecordEntity entryRecord = new EntryRecordEntity();
        entryRecord.setCheckinTime(LocalDateTime.now());
        entryRecord.setEventSegment(EventSegment.FAIR);
        entryRecord.setParticipant(this.participant);

        return entryRecord;
    }

    private void verifyEntryRecordNotSaved() {
        verify(entryRecordRepository, never()).save(any(EntryRecordEntity.class));
    }

    private void verifyRealTimeUpdateServiceNotCalled() {
        verify(participant, never()).addToRealtimeUpdateService(realTimeUpdateService);
    }

    private void assertResponseMessage(ValidateEntryExitResponseDto response, String expectedMessage) {
        assertEquals(expectedMessage, response.message());
    }
}
