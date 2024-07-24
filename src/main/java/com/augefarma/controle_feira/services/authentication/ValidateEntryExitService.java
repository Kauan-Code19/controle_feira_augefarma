package com.augefarma.controle_feira.services.authentication;

import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.client.ClientEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryExitRecordRepository;
import com.augefarma.controle_feira.repositories.client.ClientRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ValidateEntryExitService {

    private final ClientRepository clientRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final EntryExitRecordRepository entryExitRecordRepository;
    private final RealTimeUpdateService realTimeUpdateService;

    @Autowired
    public ValidateEntryExitService(ClientRepository clientRepository, LaboratoryRepository laboratoryRepository,
                                    EntryExitRecordRepository entryExitRecordRepository,
                                    @Lazy RealTimeUpdateService realTimeUpdateService) {
        this.clientRepository = clientRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.entryExitRecordRepository = entryExitRecordRepository;
        this.realTimeUpdateService = realTimeUpdateService;
    }

    /**
     * Validates and handles the entry of an entity based on its CPF.
     * Determines if the entity is a client or laboratory and processes the check-in accordingly.
     *
     * @param cpf the CPF to validate
     * @return a message indicating success or failure of the check-in
     */
    public String validateEntry(String cpf) {
        // Retrieve the entity associated with the provided CPF
        Object entity = getEntityByCpf(cpf);

        // Determine entity type and handle check-in accordingly
        if (entity instanceof ClientEntity) {
            return handleClientCheckIn((ClientEntity) entity);
        } else if (entity instanceof LaboratoryEntity) {
            return handleLaboratoryCheckIn((LaboratoryEntity) entity);
        } else {
            throw new IllegalStateException("Unexpected entity type or invalid check-out state");
        }
    }

    /**
     * Validates and handles the exit of an entity based on its CPF.
     * Determines if the entity is a client or laboratory and processes the check-out accordingly.
     *
     * @param cpf the CPF to validate
     * @return a message indicating success or failure of the check-out
     */
    public String validateExit(String cpf) {
        // Retrieve the entity associated with the provided CPF
        Object entity = getEntityByCpf(cpf);

        // Determine entity type and handle check-out accordingly
        if (entity instanceof ClientEntity) {
            return handleClientCheckOut((ClientEntity) entity);
        } else if (entity instanceof LaboratoryEntity) {
            return handleLaboratoryCheckOut((LaboratoryEntity) entity);
        } else {
            throw new IllegalStateException("Unexpected entity type or invalid check-out state");
        }
    }

    /**
     * Handles the check-in process for a ClientEntity.
     * Checks if the client is allowed to check in based on previous check-in records.
     *
     * @param clientEntity the ClientEntity to handle
     * @return a message indicating success or failure of the check-in
     */
    private String handleClientCheckIn(ClientEntity clientEntity) {
        // Retrieve the list of check-in records for the client
        List<EntryExitRecordEntity> entryExitRecordEntityList = clientEntity.getCheckIns();

        // Check if the client can check in based on previous records
        if (entryExitRecordEntityList.isEmpty() || lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckIn(clientEntity);
        } else {
            return "Access denied because the CPF "
                    + clientEntity.getCpf()
                    + " with id " + clientEntity.getId() + " has already been granted access";
        }
    }

    /**
     * Handles the check-in process for a LaboratoryEntity.
     * Checks if the laboratory is allowed to check in based on previous check-in records.
     *
     * @param laboratoryEntity the LaboratoryEntity to handle
     * @return a message indicating success or failure of the check-in
     */
    private String handleLaboratoryCheckIn(LaboratoryEntity laboratoryEntity) {
        // Retrieve the list of check-in records for the laboratory
        List<EntryExitRecordEntity> entryExitRecordEntityList = laboratoryEntity.getCheckIns();

        // Check if the laboratory can check in based on previous records
        if (entryExitRecordEntityList.isEmpty() || lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckIn(laboratoryEntity);
        } else {
            return "Access denied because the CPF "
                    + laboratoryEntity.getCpf()
                    + " with id " + laboratoryEntity.getId() + " has already been granted access";
        }
    }

    /**
     * Handles the check-out process for a ClientEntity.
     * Verifies if the client has completed their previous check-out before allowing a new check-out.
     *
     * @param clientEntity the ClientEntity to handle
     * @return a message indicating success or failure of the check-out
     */
    private String handleClientCheckOut(ClientEntity clientEntity) {
        // Retrieve the list of check-in records for the client
        List<EntryExitRecordEntity> entryExitRecordEntityList = clientEntity.getCheckIns();

        // Check if the client can check out based on previous records
        if (!lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckOut(entryExitRecordEntityList.get(entryExitRecordEntityList.size() - 1), clientEntity);
        } else {
            return "Departure denied because the CPF "
                    + clientEntity.getCpf()
                    + " with id " + clientEntity.getId() + " already had the exit registered";
        }
    }

    /**
     * Handles the check-out process for a LaboratoryEntity.
     * Verifies if the laboratory has completed its previous check-out before allowing a new check-out.
     *
     * @param laboratoryEntity the LaboratoryEntity to handle
     * @return a message indicating success or failure of the check-out
     */
    private String handleLaboratoryCheckOut(LaboratoryEntity laboratoryEntity) {
        // Retrieve the list of check-in records for the laboratory
        List<EntryExitRecordEntity> entryExitRecordEntityList = laboratoryEntity.getCheckIns();

        // Check if the laboratory can check out based on previous records
        if (!lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckOut(entryExitRecordEntityList.get(entryExitRecordEntityList.size() - 1),
                    laboratoryEntity);
        } else {
            return "Departure denied because the CPF "
                    + laboratoryEntity.getCpf()
                    + " with id " + laboratoryEntity.getId() + " already had the exit registered";
        }
    }

    /**
     * Determines if the last check-out record in the list has been completed.
     *
     * @param entryExitRecordEntityList the list of entry/exit records
     * @return true if the last check-out has a non-null checkout time, false otherwise
     */
    private boolean lastCheckOutCompleted(List<EntryExitRecordEntity> entryExitRecordEntityList) {
        // Get the last entry/exit record
        EntryExitRecordEntity lastCheckIn = entryExitRecordEntityList.get(entryExitRecordEntityList.size() - 1);

        // Check if the checkout time is set
        return lastCheckIn.getCheckoutTime() != null;
    }

    /**
     * Performs the check-in process for the specified entity.
     * Creates a new entry/exit record with the current time and updates the real-time service.
     *
     * @param entity the entity to check in
     * @return a message indicating success or failure of the check-in
     */
    @Transactional
    private String performCheckIn(Object entity) {
        // Create a new entry/exit record with the current check-in time
        EntryExitRecordEntity checkIn = new EntryExitRecordEntity();
        checkIn.setCheckinTime(LocalDateTime.now());

        if (entity instanceof ClientEntity) {
            // Set the client ID and update the real-time service
            checkIn.setClientId((ClientEntity) entity);
            realTimeUpdateService.addClientPresent((ClientEntity) entity);
        } else if (entity instanceof LaboratoryEntity) {
            // Set the laboratory ID and update the real-time service
            checkIn.setLaboratoryId((LaboratoryEntity) entity);
            realTimeUpdateService.addLaboratoryPresent((LaboratoryEntity) entity);
        }

        // Save the entry/exit record to the repository
        entryExitRecordRepository.save(checkIn);

        return "Access granted";
    }

    /**
     * Performs the check-out process for the last entry/exit record.
     * Updates the record with the current time and notifies the real-time service.
     *
     * @param lastCheckIn the last entry/exit record to check out
     * @param entity the entity to check out
     * @return a message indicating success or failure of the check-out
     */
    @Transactional
    private String performCheckOut(EntryExitRecordEntity lastCheckIn, Object entity) {
        // Set the checkout time to the current time
        lastCheckIn.setCheckoutTime(LocalDateTime.now());

        // Save the updated entry/exit record to the repository
        entryExitRecordRepository.save(lastCheckIn);

        // Update the real-time service to reflect the departure
        if (entity instanceof ClientEntity) {
            realTimeUpdateService.removeClientPresent((ClientEntity) entity);
        } else if (entity instanceof LaboratoryEntity) {
            realTimeUpdateService.removeLaboratoryPresent((LaboratoryEntity) entity);
        }

        return "Exit released";
    }

    /**
     * Retrieves an entity (Client or Laboratory) based on the provided CPF.
     * Throws an exception if no entity is found for the given CPF.
     *
     * @param cpf the CPF to search for
     * @return the corresponding ClientEntity or LaboratoryEntity
     * @throws ResourceNotFoundException if no entity is found for the given CPF
     */
    @Transactional(readOnly = true)
    private Object getEntityByCpf(String cpf) {
        // Attempt to find the client by CPF
        Optional<ClientEntity> client = clientRepository.findByCpf(cpf);
        if (client.isPresent()) {
            return client.get();
        }

        // Attempt to find the laboratory by CPF
        Optional<LaboratoryEntity> laboratory = laboratoryRepository.findByCpf(cpf);
        if (laboratory.isPresent()) {
            return laboratory.get();
        }

        // Throw an exception if neither entity is found
        throw new ResourceNotFoundException("Resource not found");
    }
}
