package com.augefarma.controle_feira.services.authentication;

import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.client.ClientEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.checkin.EntryExitRecordRepository;
import com.augefarma.controle_feira.repositories.client.ClientRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ValidateEntryExitService(ClientRepository clientRepository, LaboratoryRepository laboratoryRepository,
                                    EntryExitRecordRepository entryExitRecordRepository) {
        this.clientRepository = clientRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.entryExitRecordRepository = entryExitRecordRepository;
    }

    /**
     * Validates entry based on the entity type (Client or Laboratory).
     *
     * @param cpf the CPF to validate
     * @return a message indicating success or failure
     */
    public String validateEntry(String cpf) {
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
     * Validates exit based on the entity type (Client or Laboratory).
     *
     * @param cpf the CPF to validate
     * @return a message indicating success or failure
     */
    public String validateExit(String cpf) {
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
     * Handles check-in for a ClientEntity.
     *
     * @param clientEntity the ClientEntity to handle
     * @return a message indicating success or failure
     */
    private String handleClientCheckIn(ClientEntity clientEntity) {
        List<EntryExitRecordEntity> entryExitRecordEntityList = clientEntity.getCheckIns();

        // Check if client is allowed to check in based on previous records
        if (entryExitRecordEntityList.isEmpty() || lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckIn(clientEntity);
        } else {
            return "Access denied because the CPF "
                    + clientEntity.getCpf()
                    + " with id " + clientEntity.getId() + " has already been granted access";
        }
    }

    /**
     * Handles check-in for a LaboratoryEntity.
     *
     * @param laboratoryEntity the LaboratoryEntity to handle
     * @return a message indicating success or failure
     */
    private String handleLaboratoryCheckIn(LaboratoryEntity laboratoryEntity) {
        List<EntryExitRecordEntity> entryExitRecordEntityList = laboratoryEntity.getCheckIns();

        // Check if laboratory is allowed to check in based on previous records
        if (entryExitRecordEntityList.isEmpty() || lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckIn(laboratoryEntity);
        } else {
            return "Access denied because the CPF "
                    + laboratoryEntity.getCpf()
                    + " with id " + laboratoryEntity.getId() + " has already been granted access";
        }
    }

    /**
     * Handles check-out for a ClientEntity.
     *
     * @param clientEntity the ClientEntity to handle
     * @return a message indicating success or failure
     */
    private String handleClientCheckOut(ClientEntity clientEntity) {
        List<EntryExitRecordEntity> entryExitRecordEntityList = clientEntity.getCheckIns();

        // Check if client is allowed to check out based on previous records
        if (!lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckOut(entryExitRecordEntityList.get(entryExitRecordEntityList.size() - 1));
        } else {
            return "Departure denied because the CPF "
                    + clientEntity.getCpf()
                    + " with id " + clientEntity.getId() + " already had the exit registered";
        }
    }

    /**
     * Handles check-out for a LaboratoryEntity.
     *
     * @param laboratoryEntity the LaboratoryEntity to handle
     * @return a message indicating success or failure
     */
    private String handleLaboratoryCheckOut(LaboratoryEntity laboratoryEntity) {
        List<EntryExitRecordEntity> entryExitRecordEntityList = laboratoryEntity.getCheckIns();

        // Check if laboratory is allowed to check out based on previous records
        if (!lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckOut(entryExitRecordEntityList.get(entryExitRecordEntityList.size() - 1));
        } else {
            return "Departure denied because the CPF "
                    + laboratoryEntity.getCpf()
                    + " with id " + laboratoryEntity.getId() + " already had the exit registered";
        }
    }

    /**
     * Checks if the last check-out is completed for the provided list of entry/exit records.
     *
     * @param entryExitRecordEntityList the list of entry/exit records
     * @return true if the last check-out is completed, false otherwise
     */
    private boolean lastCheckOutCompleted(List<EntryExitRecordEntity> entryExitRecordEntityList) {
        EntryExitRecordEntity lastCheckIn = entryExitRecordEntityList.get(entryExitRecordEntityList.size() - 1);
        return lastCheckIn.getCheckoutTime() != null;
    }

    /**
     * Performs check-in for the provided entity.
     *
     * @param entity the entity to perform check-in
     * @return a message indicating success or failure
     */
    @Transactional
    private String performCheckIn(Object entity) {
        EntryExitRecordEntity checkIn = new EntryExitRecordEntity();

        checkIn.setCheckinTime(LocalDateTime.now());

        if (entity instanceof ClientEntity) {
            checkIn.setClientId((ClientEntity) entity);
        } else if (entity instanceof LaboratoryEntity) {
            checkIn.setLaboratoryId((LaboratoryEntity) entity);
        }

        entryExitRecordRepository.save(checkIn);

        return "Access granted";
    }

    /**
     * Performs check-out for the last entry/exit record.
     *
     * @param lastCheckIn the last entry/exit record to perform check-out
     * @return a message indicating success or failure
     */
    @Transactional
    private String performCheckOut(EntryExitRecordEntity lastCheckIn) {
        lastCheckIn.setCheckoutTime(LocalDateTime.now());
        entryExitRecordRepository.save(lastCheckIn);
        return "Exit released";
    }

    /**
     * Retrieves an entity (Client or Laboratory) based on the provided CPF.
     *
     * @param cpf the CPF to search for
     * @return the corresponding ClientEntity or LaboratoryEntity
     * @throws ResourceNotFoundException if no entity is found for the given CPF
     */
    @Transactional(readOnly = true)
    private Object getEntityByCpf(String cpf) {
        Optional<ClientEntity> client = clientRepository.findByCpf(cpf);
        if (client.isPresent()) {
            return client.get();
        }

        Optional<LaboratoryEntity> laboratory = laboratoryRepository.findByCpf(cpf);
        if (laboratory.isPresent()) {
            return laboratory.get();
        }

        throw new ResourceNotFoundException("Resource not found");
    }
}
