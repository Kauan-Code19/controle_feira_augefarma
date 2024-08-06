package com.augefarma.controle_feira.services.socket;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.dtos.event.ListUpdateEventDto;
import com.augefarma.controle_feira.dtos.real_time.EntitiesListResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.repositories.entry_exit.EntryExitRecordRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RealTimeUpdateService {

    @Getter
    private final EntitiesListResponseDto entitiesListResponseDto;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EntryExitRecordRepository entryExitRecordRepository;

    /**
     * Constructor for RealTimeUpdateService.
     *
     * @param applicationEventPublisher publisher for application events
     * @param entryExitRecordRepository repository for managing entry and exit records
     */
    @Autowired
    public RealTimeUpdateService(ApplicationEventPublisher applicationEventPublisher,
                                 EntryExitRecordRepository entryExitRecordRepository) {
        this.entryExitRecordRepository = entryExitRecordRepository;
        this.entitiesListResponseDto = new EntitiesListResponseDto();
        this.applicationEventPublisher = applicationEventPublisher;
        initializeState(); // Initialize the state with existing records
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
        if (!entitiesListResponseDto.getPharmacyRepresentatives().contains(pharmacyRepresentativeResponseDto)) {
            // Add the representative to the list of present representatives
            entitiesListResponseDto.addPharmacyRepresentative(pharmacyRepresentativeResponseDto);
            publishUpdateEvent(); // Publish an update event with the current state
        }
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
        if (entitiesListResponseDto.getPharmacyRepresentatives().contains(pharmacyRepresentativeResponseDto)) {
            // Remove the representative from the list of present representatives
            entitiesListResponseDto.removePharmacyRepresentative(pharmacyRepresentativeResponseDto);
            publishUpdateEvent(); // Publish an update event with the current state
        }
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
        if (!entitiesListResponseDto.getLaboratoryMembers().contains(laboratoryMemberResponseDto)) {
            // Add the laboratory member to the list of present members
            entitiesListResponseDto.addLaboratoryMember(laboratoryMemberResponseDto);
            publishUpdateEvent(); // Publish an update event with the current state
        }
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
        if (entitiesListResponseDto.getLaboratoryMembers().contains(laboratoryMemberResponseDto)) {
            // Remove the laboratory member from the list of present members
            entitiesListResponseDto.removeLaboratoryMember(laboratoryMemberResponseDto);
            publishUpdateEvent(); // Publish an update event with the current state
        }
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

    /**
     * Initializes the state of the service by loading all active entry/exit records
     * and adding the corresponding pharmacy representatives or laboratory members to the list of present entities.
     */
    public void initializeState() {
        // Retrieve all entry/exit records that do not have a checkout time (i.e., still present)
        List<EntryExitRecordEntity> activeRecords = entryExitRecordRepository.findByCheckoutTimeIsNull();

        // Iterate over the active records to add pharmacy representatives or laboratory members to the list of
        // present entities
        for (EntryExitRecordEntity record : activeRecords) {
            if (record.getPharmacyRepresentative() != null) {
                // Add pharmacy representative to the list if present
                addPharmacyRepresentativePresent(record.getPharmacyRepresentative());
            } else if (record.getLaboratoryMember() != null) {
                // Add laboratory member to the list if present
                addLaboratoryMemberPresent(record.getLaboratoryMember());
            }
        }
    }
}
