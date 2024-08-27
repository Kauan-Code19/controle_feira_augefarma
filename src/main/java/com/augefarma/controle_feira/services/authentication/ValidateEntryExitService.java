package com.augefarma.controle_feira.services.authentication;

import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.entry_exit.EventSegment;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryExitRecordRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
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

    private final PharmacyRepresentativeRepository pharmacyRepresentativeRepository;
    private final LaboratoryMemberRepository laboratoryMemberRepository;
    private final EntryExitRecordRepository entryExitRecordRepository;
    private final RealTimeUpdateService realTimeUpdateService;

    @Autowired
    public ValidateEntryExitService(PharmacyRepresentativeRepository pharmacyRepresentativeRepository,
                                    LaboratoryMemberRepository laboratoryMemberRepository,
                                    EntryExitRecordRepository entryExitRecordRepository,
                                    @Lazy RealTimeUpdateService realTimeUpdateService) {
        this.pharmacyRepresentativeRepository = pharmacyRepresentativeRepository;
        this.laboratoryMemberRepository = laboratoryMemberRepository;
        this.entryExitRecordRepository = entryExitRecordRepository;
        this.realTimeUpdateService = realTimeUpdateService;
    }

    /**
     * Validates and handles the entry of an entity based on its CPF.
     * Determines if the entity is a pharmacy representative or laboratory member and processes the check-in accordingly.
     *
     * @param cpf          the CPF to validate
     * @param eventSegment the event segment associated with the check-in
     * @return a message indicating success or failure of the check-in
     */
    public ValidateEntryExitResponseDto validateEntry(String cpf, EventSegment eventSegment) {
        Object entity = getEntityByCpf(cpf);

        if (entity instanceof PharmacyRepresentativeEntity) {
            return handlePharmacyRepresentativeCheckIn((PharmacyRepresentativeEntity) entity, eventSegment);
        } else if (entity instanceof LaboratoryMemberEntity) {
            return handleLaboratoryMemberCheckIn((LaboratoryMemberEntity) entity, eventSegment);
        } else {
            throw new IllegalStateException("Unexpected entity type or invalid check-out state");
        }
    }

    /**
     * Validates and handles the exit of an entity based on its CPF.
     * Determines if the entity is a pharmacy representative or laboratory member and processes
     * the check-out accordingly.
     *
     * @param cpf the CPF to validate
     * @return a message indicating success or failure of the check-out
     */
    public ValidateEntryExitResponseDto validateExit(String cpf) {
        Object entity = getEntityByCpf(cpf);

        if (entity instanceof PharmacyRepresentativeEntity) {
            return handlePharmacyRepresentativeCheckOut((PharmacyRepresentativeEntity) entity);
        } else if (entity instanceof LaboratoryMemberEntity) {
            return handleLaboratoryMemberCheckOut((LaboratoryMemberEntity) entity);
        } else {
            throw new IllegalStateException("Unexpected entity type or invalid check-out state");
        }
    }

    /**
     * Handles the check-in process for a PharmacyRepresentativeEntity.
     * Checks if the representative can check in based on previous check-in records.
     *
     * @param pharmacyRepresentative the PharmacyRepresentativeEntity to handle
     * @param eventSegment           the event segment associated with the check-in
     * @return a message indicating success or failure of the check-in
     */
    private ValidateEntryExitResponseDto handlePharmacyRepresentativeCheckIn(
            PharmacyRepresentativeEntity pharmacyRepresentative, EventSegment eventSegment) {

        List<EntryExitRecordEntity> entryExitRecordEntityList = pharmacyRepresentative.getEntryExitRecords();

        if (entryExitRecordEntityList.isEmpty() || lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckIn(pharmacyRepresentative, eventSegment);
        } else {
            return new ValidateEntryExitResponseDto("Access denied: CPF " + pharmacyRepresentative.getCpf()
                    + " with ID " + pharmacyRepresentative.getId() + " has already been granted access");
        }
    }

    /**
     * Handles the check-in process for a LaboratoryMemberEntity.
     * Checks if the laboratory member can check in based on previous check-in records.
     *
     * @param laboratoryMember the LaboratoryMemberEntity to handle
     * @param eventSegment     the event segment associated with the check-in
     * @return a message indicating success or failure of the check-in
     */
    private ValidateEntryExitResponseDto handleLaboratoryMemberCheckIn(LaboratoryMemberEntity laboratoryMember,
                                                                       EventSegment eventSegment) {

        List<EntryExitRecordEntity> entryExitRecordEntityList = laboratoryMember.getEntryExitRecords();

        if (entryExitRecordEntityList.isEmpty() || lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckIn(laboratoryMember, eventSegment);
        } else {
            return new ValidateEntryExitResponseDto("Access denied: CPF " + laboratoryMember.getCpf()
                    + " with ID " + laboratoryMember.getId() + " has already been granted access");
        }
    }

    /**
     * Handles the check-out process for a PharmacyRepresentativeEntity.
     * Verifies if the representative has completed their previous check-out before allowing a new check-out.
     *
     * @param pharmacyRepresentative the PharmacyRepresentativeEntity to handle
     * @return a message indicating success or failure of the check-out
     */
    private ValidateEntryExitResponseDto handlePharmacyRepresentativeCheckOut(PharmacyRepresentativeEntity pharmacyRepresentative) {
        List<EntryExitRecordEntity> entryExitRecordEntityList = pharmacyRepresentative.getEntryExitRecords();

        if (!lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckOut(entryExitRecordEntityList.get(entryExitRecordEntityList.size() - 1),
                    pharmacyRepresentative);
        } else {
            return new ValidateEntryExitResponseDto("Departure denied: CPF " + pharmacyRepresentative.getCpf()
                    + " with ID " + pharmacyRepresentative.getId() + " already had the exit registered");
        }
    }

    /**
     * Handles the check-out process for a LaboratoryMemberEntity.
     * Verifies if the laboratory member has completed their previous check-out before allowing a new check-out.
     *
     * @param laboratoryMember the LaboratoryMemberEntity to handle
     * @return a message indicating success or failure of the check-out
     */
    private ValidateEntryExitResponseDto handleLaboratoryMemberCheckOut(LaboratoryMemberEntity laboratoryMember) {
        List<EntryExitRecordEntity> entryExitRecordEntityList = laboratoryMember.getEntryExitRecords();

        if (!lastCheckOutCompleted(entryExitRecordEntityList)) {
            return performCheckOut(entryExitRecordEntityList.get(entryExitRecordEntityList.size() - 1),
                    laboratoryMember);
        } else {
            return new ValidateEntryExitResponseDto("Departure denied: CPF " + laboratoryMember.getCpf()
                    + " with ID " + laboratoryMember.getId() + " already had the exit registered");
        }
    }

    /**
     * Determines if the last check-out record in the list has been completed.
     *
     * @param entryExitRecordList the list of entry/exit records
     * @return true if the last check-out has a non-null checkout time, false otherwise
     */
    private boolean lastCheckOutCompleted(List<EntryExitRecordEntity> entryExitRecordList) {
        if (entryExitRecordList.isEmpty()) {
            return false;
        }

        EntryExitRecordEntity lastCheckIn = entryExitRecordList.get(entryExitRecordList.size() - 1);
        return lastCheckIn.getCheckoutTime() != null;
    }

    /**
     * Performs the check-in process for the specified entity.
     * Creates a new entry/exit record with the current time and updates the real-time service.
     *
     * @param entity       the entity to check in
     * @param eventSegment the event segment associated with the check-in
     * @return a message indicating success or failure of the check-in
     */
    @Transactional
    private ValidateEntryExitResponseDto performCheckIn(Object entity, EventSegment eventSegment) {
        EntryExitRecordEntity entryExitRecord = new EntryExitRecordEntity();
        entryExitRecord.setCheckinTime(LocalDateTime.now());
        entryExitRecord.setEventSegment(eventSegment);

        if (entity instanceof PharmacyRepresentativeEntity) {
            entryExitRecord.setPharmacyRepresentative((PharmacyRepresentativeEntity) entity);
            realTimeUpdateService.addPharmacyRepresentativePresent((PharmacyRepresentativeEntity) entity);
        } else if (entity instanceof LaboratoryMemberEntity) {
            entryExitRecord.setLaboratoryMember((LaboratoryMemberEntity) entity);
            realTimeUpdateService.addLaboratoryMemberPresent((LaboratoryMemberEntity) entity);
        }

        entryExitRecordRepository.save(entryExitRecord);
        return new ValidateEntryExitResponseDto("Access granted");
    }

    /**
     * Performs the check-out process for the specified entity.
     * Updates the latest entry/exit record with the current time and updates the real-time service.
     *
     * @param entryExitRecord the entry/exit record to update
     * @param entity          the entity to check out
     * @return a message indicating success or failure of the check-out
     */
    @Transactional
    private ValidateEntryExitResponseDto performCheckOut(EntryExitRecordEntity entryExitRecord, Object entity) {
        entryExitRecord.setCheckoutTime(LocalDateTime.now());
        entryExitRecordRepository.save(entryExitRecord);

        if (entity instanceof PharmacyRepresentativeEntity) {
            realTimeUpdateService.removePharmacyRepresentativePresent((PharmacyRepresentativeEntity) entity);
        } else if (entity instanceof LaboratoryMemberEntity) {
            realTimeUpdateService.removeLaboratoryMemberPresent((LaboratoryMemberEntity) entity);
        }

        return new ValidateEntryExitResponseDto("Exit recorded successfully");
    }

    /**
     * Retrieves an entity based on CPF from the repositories.
     *
     * @param cpf the CPF to retrieve
     * @return the entity if found, otherwise throws ResourceNotFoundException
     */
    @Transactional(readOnly = true)
    private Object getEntityByCpf(String cpf) {
        // Attempt to find the client by CPF
        Optional<PharmacyRepresentativeEntity> pharmacyRepresentative = pharmacyRepresentativeRepository
                .findByCpf(cpf);

        if (pharmacyRepresentative.isPresent()) {
            return pharmacyRepresentative.get();
        }

        // Attempt to find the laboratory by CPF
        Optional<LaboratoryMemberEntity> laboratoryMember = laboratoryMemberRepository.findByCpf(cpf);

        if (laboratoryMember.isPresent()) {
            return laboratoryMember.get();
        }

        // Throw an exception if neither entity is found
        throw new ResourceNotFoundException("Resource not found");
    }
}
