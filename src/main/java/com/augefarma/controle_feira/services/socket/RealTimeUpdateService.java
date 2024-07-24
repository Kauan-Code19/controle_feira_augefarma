package com.augefarma.controle_feira.services.socket;

import com.augefarma.controle_feira.dtos.client.ClientResponseDto;
import com.augefarma.controle_feira.dtos.event.ListUpdateEventDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryResponseDto;
import com.augefarma.controle_feira.dtos.real_time.EntitiesListResponseDto;
import com.augefarma.controle_feira.entities.client.ClientEntity;
import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
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
     * Adds a client to the list of currently present clients and publishes an update event.
     * If the client is not already present, it is added, and an update event is published.
     *
     * @param client the client to be added
     */
    public void addClientPresent(ClientEntity client) {
        // Convert ClientEntity to ClientResponseDto
        ClientResponseDto clientResponseDto = new ClientResponseDto(client);

        // Check if the client is already in the list
        if (!entitiesListResponseDto.getClients().contains(clientResponseDto)) {
            // Add the client to the list of present clients
            entitiesListResponseDto.addClient(clientResponseDto);
            publishUpdateEvent(); // Publish an update event with the current state
        }
    }

    /**
     * Removes a client from the list of currently present clients and publishes an update event.
     * If the client is present, it is removed, and an update event is published.
     *
     * @param client the client to be removed
     */
    public void removeClientPresent(ClientEntity client) {
        // Convert ClientEntity to ClientResponseDto
        ClientResponseDto clientResponseDto = new ClientResponseDto(client);

        // Check if the client is in the list
        if (entitiesListResponseDto.getClients().contains(clientResponseDto)) {
            // Remove the client from the list of present clients
            entitiesListResponseDto.removeClient(clientResponseDto);
            publishUpdateEvent(); // Publish an update event with the current state
        }
    }

    /**
     * Adds a laboratory to the list of currently present laboratories and publishes an update event.
     * If the laboratory is not already present, it is added, and an update event is published.
     *
     * @param laboratory the laboratory to be added
     */
    public void addLaboratoryPresent(LaboratoryEntity laboratory) {
        // Convert LaboratoryEntity to LaboratoryResponseDto
        LaboratoryResponseDto laboratoryResponseDto = new LaboratoryResponseDto(laboratory);

        // Check if the laboratory is already in the list
        if (!entitiesListResponseDto.getLaboratories().contains(laboratoryResponseDto)) {
            // Add the laboratory to the list of present laboratories
            entitiesListResponseDto.addLaboratory(laboratoryResponseDto);
            publishUpdateEvent(); // Publish an update event with the current state
        }
    }

    /**
     * Removes a laboratory from the list of currently present laboratories and publishes an update event.
     * If the laboratory is present, it is removed, and an update event is published.
     *
     * @param laboratory the laboratory to be removed
     */
    public void removeLaboratoryPresent(LaboratoryEntity laboratory) {
        // Convert LaboratoryEntity to LaboratoryResponseDto
        LaboratoryResponseDto laboratoryResponseDto = new LaboratoryResponseDto(laboratory);

        // Check if the laboratory is in the list
        if (entitiesListResponseDto.getLaboratories().contains(laboratoryResponseDto)) {
            // Remove the laboratory from the list of present laboratories
            entitiesListResponseDto.removeLaboratory(laboratoryResponseDto);
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
     * and adding the corresponding clients or laboratories to the list of present entities.
     */
    public void initializeState() {
        // Retrieve all entry/exit records that do not have a checkout time (i.e., still present)
        List<EntryExitRecordEntity> activeRecords = entryExitRecordRepository.findByCheckoutTimeIsNull();

        // Iterate over the active records to add clients or laboratories to the list of present entities
        for (EntryExitRecordEntity record : activeRecords) {
            if (record.getClientId() != null) {
                // Add client to the list if present
                addClientPresent(record.getClientId());
            } else if (record.getLaboratoryId() != null) {
                // Add laboratory to the list if present
                addLaboratoryPresent(record.getLaboratoryId());
            }
        }
    }
}
