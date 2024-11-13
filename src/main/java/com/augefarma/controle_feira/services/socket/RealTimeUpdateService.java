package com.augefarma.controle_feira.services.socket;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.dtos.event.ListUpdateEventDto;
import com.augefarma.controle_feira.dtos.real_time.EntitiesListResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryRecordEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.participant.ParticipantEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.exceptions.EntityAlreadyPresentException;
import com.augefarma.controle_feira.exceptions.EntityNotPresentException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryRecordRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RealTimeUpdateService {

    @Getter
    private final EntitiesListResponseDto entitiesListResponseDto;
    private final EntryRecordRepository entryRecordRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public RealTimeUpdateService(EntryRecordRepository entryRecordRepository,
                                 ApplicationEventPublisher applicationEventPublisher) {
        this.entryRecordRepository = entryRecordRepository;
        this.entitiesListResponseDto = new EntitiesListResponseDto();
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeState() {
        List<EntryRecordEntity> entryRecords = getEntryRecordsForTodayWithoutCheckOut();
        Set<ParticipantEntity> processedParticipants = new HashSet<>();

        for (EntryRecordEntity entry : entryRecords) {
            ParticipantEntity participant = entry.getParticipant();

            if (isParticipantNotProcessed(participant, processedParticipants)) {
                addParticipantToRealtimeUpdateService(participant);
                processedParticipants.add(participant);
            }
        }
    }


    private List<EntryRecordEntity> getEntryRecordsForTodayWithoutCheckOut() {
        return entryRecordRepository.findAll()
                .stream()
                .filter(entry -> entry.getEventSegment() == EventSegment.FAIR
                        && entry.getParticipant().getExitRecords().isEmpty()
                        && entry.getCheckinTime().toLocalDate().isEqual(LocalDate.now()))
                .toList();
    }


    private boolean isParticipantNotProcessed(ParticipantEntity participant, Set<ParticipantEntity> processedParticipants) {
        return !processedParticipants.contains(participant);
    }


    private void addParticipantToRealtimeUpdateService(ParticipantEntity participant) {
        participant.addToRealtimeUpdateService(this);
    }

    /**
     * Adds a pharmacy representative to the list of currently present representatives and publishes an update event.
     * If the representative is not already present, it is added, and an update event is published.
     *
     * @param pharmacyRepresentative the pharmacy representative to be added
     */
    public void addPharmacyRepresentativePresent(PharmacyRepresentativeEntity pharmacyRepresentative) {
        // Convert PharmacyRepresentativeEntity to PharmacyRepresentativeResponseDto
        PharmacyRepresentativeResponseDto pharmacyRepresentativeResponseDto =
                new PharmacyRepresentativeResponseDto(pharmacyRepresentative);

        // Check if the representative is already in the list
        if (entitiesListResponseDto.getPharmacyRepresentatives().contains(pharmacyRepresentativeResponseDto)) {
            throw new EntityAlreadyPresentException("A entidade já está presente e não pode ser adicionada novamente");
        }

        // Add the representative to the list of present representatives
        entitiesListResponseDto.addPharmacyRepresentative(pharmacyRepresentativeResponseDto);
        publishUpdateEvent(); // Publish an update event with the current state
    }

    /**
     * Removes a pharmacy representative from the list of currently present representatives and publishes an update event.
     * If the representative is present, it is removed, and an update event is published.
     *
     * @param pharmacyRepresentative the pharmacy representative to be removed
     */
    public void removePharmacyRepresentativePresent(PharmacyRepresentativeEntity pharmacyRepresentative) {
        // Convert PharmacyRepresentativeEntity to PharmacyRepresentativeResponseDto
        PharmacyRepresentativeResponseDto pharmacyRepresentativeResponseDto =
                new PharmacyRepresentativeResponseDto(pharmacyRepresentative);

        // Check if the representative is in the list
        if (!entitiesListResponseDto.getPharmacyRepresentatives().contains(pharmacyRepresentativeResponseDto)) {
            throw new EntityNotPresentException("A entidade não está presente e não pode ser removida");
        }

        // Remove the representative from the list of present representatives
        entitiesListResponseDto.removePharmacyRepresentative(pharmacyRepresentativeResponseDto);
        publishUpdateEvent(); // Publish an update event with the current state
    }

    /**
     * Adds a laboratory member to the list of currently present laboratory members and publishes an update event.
     * If the laboratory member is not already present, it is added, and an update event is published.
     *
     * @param laboratoryMemberEntity the laboratory member to be added
     */
    public void addLaboratoryMemberPresent(LaboratoryMemberEntity laboratoryMemberEntity) {
        // Convert LaboratoryMemberEntity to LaboratoryMemberResponseDto
        LaboratoryMemberResponseDto laboratoryMemberResponseDto =
                new LaboratoryMemberResponseDto(laboratoryMemberEntity);

        // Check if the laboratory member is already in the list
        if (entitiesListResponseDto.getLaboratoryMembers().contains(laboratoryMemberResponseDto)) {
            throw new EntityAlreadyPresentException(
                    "A entidade já está presente e não pode ser adicionada novamente");
        }

        // Add the laboratory member to the list of present members
        entitiesListResponseDto.addLaboratoryMember(laboratoryMemberResponseDto);
        publishUpdateEvent(); // Publish an update event with the current state
    }

    /**
     * Removes a laboratory member from the list of currently present laboratory members and publishes an update event.
     * If the laboratory member is present, it is removed, and an update event is published.
     *
     * @param laboratoryMember the laboratory member to be removed
     */
    public void removeLaboratoryMemberPresent(LaboratoryMemberEntity laboratoryMember) {
        // Convert LaboratoryMemberEntity to LaboratoryMemberResponseDto
        LaboratoryMemberResponseDto laboratoryMemberResponseDto = new LaboratoryMemberResponseDto(laboratoryMember);

        // Check if the laboratory member is in the list
        if (!entitiesListResponseDto.getLaboratoryMembers().contains(laboratoryMemberResponseDto)) {
            throw new EntityNotPresentException("A entidade não está presente e não pode ser removida");
        }

        // Remove the laboratory member from the list of present members
        entitiesListResponseDto.removeLaboratoryMember(laboratoryMemberResponseDto);
        publishUpdateEvent(); // Publish an update event with the current state
    }

    /**
     * Publishes a ListUpdateEvent with the current list of entities.
     * This event is used to notify subscribers about updates to the list of entities.
     */
    private void publishUpdateEvent() {
        // Create a new ListUpdateEventDto with the current state of entities
        ListUpdateEventDto event = new ListUpdateEventDto(this, entitiesListResponseDto);
        applicationEventPublisher.publishEvent(event); // Publish the event to the application event publisher
    }
}
