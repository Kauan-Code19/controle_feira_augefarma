package com.augefarma.controle_feira.unit.services.socket;

import com.augefarma.controle_feira.dtos.event.ListUpdateEventDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.dtos.real_time.EntitiesListResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.exceptions.EntityAlreadyPresentException;
import com.augefarma.controle_feira.exceptions.EntityNotPresentException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryExitRecordRepository;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RealTimeUpdateServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private EntryExitRecordRepository entryExitRecordRepository;

    @InjectMocks
    private RealTimeUpdateService realTimeUpdateService;

    private PharmacyRepresentativeEntity pharmacyRepresentative;
    private LaboratoryMemberEntity laboratoryMember;

    @BeforeEach
    void setUp() {
        pharmacyRepresentative = createPharmacyRepresentative(1L, "John Doe");
        laboratoryMember = createLaboratoryMember(1L, "Jane Smith", "Sample Laboratory");
    }

    @Test
    void shouldAddPharmacyRepresentativeSuccessfully() {
        realTimeUpdateService.addPharmacyRepresentativePresent(pharmacyRepresentative);

        List<PharmacyRepresentativeResponseDto> representatives = realTimeUpdateService.getEntitiesListResponseDto()
                .getPharmacyRepresentatives();
        assertEquals(1, representatives.size(), "Deve conter um representante farmacêutico.");

        PharmacyRepresentativeResponseDto expectedDto = new PharmacyRepresentativeResponseDto(pharmacyRepresentative);
        assertTrue(representatives.contains(expectedDto), "O representante adicionado deve estar na lista.");

        ArgumentCaptor<ListUpdateEventDto> eventCaptor = ArgumentCaptor.forClass(ListUpdateEventDto.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        ListUpdateEventDto capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent, "O evento publicado não deve ser nulo.");
        assertEquals(realTimeUpdateService.getEntitiesListResponseDto(), capturedEvent.getUpdatedList(),
                "O estado atualizado deve ser igual ao estado atual da lista de entidades.");
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicatePharmacyRepresentative() {
        realTimeUpdateService.addPharmacyRepresentativePresent(pharmacyRepresentative);

        EntityAlreadyPresentException exception = assertThrows(EntityAlreadyPresentException.class, () -> {
            realTimeUpdateService.addPharmacyRepresentativePresent(pharmacyRepresentative);
        });

        assertEquals("A entidade já está presente e não pode ser adicionada novamente", exception.getMessage(),
                "A mensagem da exceção deve ser adequada.");
    }

    @Test
    void shouldRemovePharmacyRepresentativeSuccessfully() {
        realTimeUpdateService.addPharmacyRepresentativePresent(pharmacyRepresentative);

        realTimeUpdateService.removePharmacyRepresentativePresent(pharmacyRepresentative);

        List<PharmacyRepresentativeResponseDto> representatives = realTimeUpdateService.getEntitiesListResponseDto()
                .getPharmacyRepresentatives();
        assertEquals(0, representatives.size(), "Não deve conter representantes farmacêuticos após remoção.");

        ArgumentCaptor<ListUpdateEventDto> eventCaptor = ArgumentCaptor.forClass(ListUpdateEventDto.class);
        verify(eventPublisher, times(2)).publishEvent(eventCaptor.capture());

        ListUpdateEventDto capturedEvent = eventCaptor.getAllValues().get(1);
        assertNotNull(capturedEvent, "O último evento publicado não deve ser nulo.");
        assertEquals(realTimeUpdateService.getEntitiesListResponseDto(), capturedEvent.getUpdatedList(),
                "O estado atualizado deve refletir a remoção.");
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentPharmacyRepresentative() {
        EntityNotPresentException exception = assertThrows(EntityNotPresentException.class, () -> {
            realTimeUpdateService.removePharmacyRepresentativePresent(pharmacyRepresentative);
        });

        assertEquals("A entidade não está presente e não pode ser removida", exception.getMessage(),
                "A mensagem da exceção deve ser adequada.");
    }

    @Test
    void shouldAddLaboratoryMemberSuccessfully() {

        realTimeUpdateService.addLaboratoryMemberPresent(laboratoryMember);

        List<LaboratoryMemberResponseDto> members = realTimeUpdateService.getEntitiesListResponseDto()
                .getLaboratoryMembers();
        assertEquals(1, members.size(), "Deve conter um membro de laboratório.");

        LaboratoryMemberResponseDto expectedDto = new LaboratoryMemberResponseDto(laboratoryMember);
        assertTrue(members.contains(expectedDto), "O membro adicionado deve estar na lista.");

        ArgumentCaptor<ListUpdateEventDto> eventCaptor = ArgumentCaptor.forClass(ListUpdateEventDto.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        ListUpdateEventDto capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent, "O evento publicado não deve ser nulo.");
        assertEquals(realTimeUpdateService.getEntitiesListResponseDto(), capturedEvent.getUpdatedList(),
                "O estado atualizado deve ser igual ao estado atual da lista de entidades.");
    }

    @Test
    void shouldThrowExceptionWhenAddingDuplicateLaboratoryMember() {
        realTimeUpdateService.addLaboratoryMemberPresent(laboratoryMember);

        EntityAlreadyPresentException exception = assertThrows(EntityAlreadyPresentException.class, () -> {
            realTimeUpdateService.addLaboratoryMemberPresent(laboratoryMember);
        });

        assertEquals("A entidade já está presente e não pode ser adicionada novamente", exception.getMessage(),
                "A mensagem da exceção deve ser adequada.");
    }

    @Test
    void shouldRemoveLaboratoryMemberSuccessfully() {
        realTimeUpdateService.addLaboratoryMemberPresent(laboratoryMember);

        realTimeUpdateService.removeLaboratoryMemberPresent(laboratoryMember);

        List<LaboratoryMemberResponseDto> members = realTimeUpdateService.getEntitiesListResponseDto()
                .getLaboratoryMembers();
        assertEquals(0, members.size(), "Não deve conter membros de laboratório após remoção.");

        ArgumentCaptor<ListUpdateEventDto> eventCaptor = ArgumentCaptor.forClass(ListUpdateEventDto.class);
        verify(eventPublisher, times(2)).publishEvent(eventCaptor.capture());

        ListUpdateEventDto capturedEvent = eventCaptor.getAllValues().get(1);
        assertNotNull(capturedEvent, "O último evento publicado não deve ser nulo.");
        assertEquals(realTimeUpdateService.getEntitiesListResponseDto(), capturedEvent.getUpdatedList(),
                "O estado atualizado deve refletir a remoção.");
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentLaboratoryMember() {
        EntityNotPresentException exception = assertThrows(EntityNotPresentException.class, () -> {
            realTimeUpdateService.removeLaboratoryMemberPresent(laboratoryMember);
        });

        assertEquals("A entidade não está presente e não pode ser removida", exception.getMessage(),
                "A mensagem da exceção deve ser adequada.");
    }

    @Test
    void shouldInitializeStateSuccessfully() {
        EntryExitRecordEntity recordWithPharmacyRep = new EntryExitRecordEntity();
        recordWithPharmacyRep.setPharmacyRepresentative(pharmacyRepresentative);

        EntryExitRecordEntity recordWithLabMember = new EntryExitRecordEntity();
        recordWithLabMember.setLaboratoryMember(laboratoryMember);

        List<EntryExitRecordEntity> activeRecords = Arrays.asList(recordWithPharmacyRep, recordWithLabMember);

        when(entryExitRecordRepository.findByCheckoutTimeIsNull()).thenReturn(activeRecords);

        realTimeUpdateService.initializeState();

        EntitiesListResponseDto entitiesList = realTimeUpdateService.getEntitiesListResponseDto();

        PharmacyRepresentativeResponseDto expectedPharmacyDto = new PharmacyRepresentativeResponseDto(pharmacyRepresentative);
        LaboratoryMemberResponseDto expectedLabMemberDto = new LaboratoryMemberResponseDto(laboratoryMember);

        assertTrue(entitiesList.getPharmacyRepresentatives().contains(expectedPharmacyDto),
                "O representante farmacêutico deve estar presente após inicialização.");
        assertTrue(entitiesList.getLaboratoryMembers().contains(expectedLabMemberDto),
                "O membro de laboratório deve estar presente após inicialização.");

        verify(eventPublisher, times(2)).publishEvent(any(ListUpdateEventDto.class));
    }

    private PharmacyRepresentativeEntity createPharmacyRepresentative(Long id, String name) {
        PharmacyRepresentativeEntity representative = new PharmacyRepresentativeEntity();
        representative.setId(id);
        representative.setName(name);

        return representative;
    }

    private LaboratoryMemberEntity createLaboratoryMember(Long id, String name, String corporateReason) {
        LaboratoryEntity laboratory = new LaboratoryEntity();
        laboratory.setId(id);
        laboratory.setCorporateReason(corporateReason);
        LaboratoryMemberEntity member = new LaboratoryMemberEntity();
        member.setId(id);
        member.setName(name);
        member.setLaboratory(laboratory);

        return member;
    }

    private void verifyEventPublished(EntitiesListResponseDto expectedList) {
        ArgumentCaptor<ListUpdateEventDto> eventCaptor = ArgumentCaptor.forClass(ListUpdateEventDto.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        ListUpdateEventDto capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent, "O evento publicado não deve ser nulo.");
        assertEquals(expectedList, capturedEvent.getUpdatedList(),
                "O estado atualizado deve ser igual ao estado atual da lista de entidades.");
    }

    @Test
    void shouldInitializeStateWithNullEntities() {
        when(entryExitRecordRepository.findByCheckoutTimeIsNull()).thenReturn(Collections.emptyList());

        realTimeUpdateService.initializeState();

        EntitiesListResponseDto entitiesList = realTimeUpdateService.getEntitiesListResponseDto();

        assertTrue(entitiesList.getPharmacyRepresentatives().isEmpty(),
                "A lista de representantes farmacêuticos deve estar vazia após a inicialização.");
        assertTrue(entitiesList.getLaboratoryMembers().isEmpty(),
                "A lista de membros de laboratório deve estar vazia após a inicialização.");

        verify(eventPublisher, never()).publishEvent(any(ListUpdateEventDto.class));
    }
}
