package com.augefarma.controle_feira.services.authorization;

import com.augefarma.controle_feira.dtos.authorization.WristbandsResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EntryExitRecordEntity;
import com.augefarma.controle_feira.entities.entry_exit.EventSegment;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.entry_exit.EntryExitRecordRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class WristbandsService {

    private final EntryExitRecordRepository entryExitRecordRepository;
    private final PharmacyRepresentativeRepository pharmacyRepresentativeRepository;
    private final LaboratoryMemberRepository laboratoryMemberRepository;

    @Autowired
    public WristbandsService(EntryExitRecordRepository entryExitRecordRepository,
                             PharmacyRepresentativeRepository pharmacyRepresentativeRepository,
                             LaboratoryMemberRepository laboratoryMemberRepository) {
        this.entryExitRecordRepository = entryExitRecordRepository;
        this.pharmacyRepresentativeRepository = pharmacyRepresentativeRepository;
        this.laboratoryMemberRepository = laboratoryMemberRepository;
    }

    /**
     * Checks the delivery status of a wristband for the entity associated with the provided CPF.
     * Determines whether the CPF belongs to a PharmacyRepresentativeEntity or a LaboratoryMemberEntity,
     * and checks the wristband delivery status accordingly.
     *
     * @param cpfEntity the CPF of the entity to check
     * @return a response indicating the wristband delivery status
     */
    @Transactional(readOnly = true)
    public WristbandsResponseDto checkDeliveryOfWristband(String cpfEntity) {
        // Try to find the entity as a PharmacyRepresentativeEntity by CPF
        Optional<PharmacyRepresentativeEntity> pharmacyRepresentative = pharmacyRepresentativeRepository
                .findByCpf(cpfEntity);

        // If the PharmacyRepresentativeEntity is not found, try to find it as a LaboratoryMemberEntity
        if (pharmacyRepresentative.isEmpty()) {
            Optional<LaboratoryMemberEntity> laboratoryMember = laboratoryMemberRepository.findByCpf(cpfEntity);

            // If neither entity is found, throw a ResourceNotFoundException
            if (laboratoryMember.isEmpty()) {
                throw new ResourceNotFoundException("Entity not found, please provide a valid CPF");
            }

            // Check wristband delivery status for the LaboratoryMemberEntity
            return checkWristbandDeliveryForLaboratoryMember(laboratoryMember.get());
        }

        // Check wristband delivery status for the PharmacyRepresentativeEntity
        return checkWristbandDeliveryForPharmacyRepresentative(pharmacyRepresentative.get());
    }

    /**
     * Checks the wristband delivery status for a LaboratoryMemberEntity.
     * If a wristband has already been delivered for the FAIR event segment, access is denied.
     *
     * @param laboratoryMember the LaboratoryMemberEntity to check
     * @return a response indicating the wristband delivery status
     */
    private WristbandsResponseDto checkWristbandDeliveryForLaboratoryMember(LaboratoryMemberEntity laboratoryMember) {
        // Retrieve the entry and exit records associated with the laboratory member
        Optional<List<EntryExitRecordEntity>> entryExitRecordEntityList = entryExitRecordRepository
                .findByLaboratoryMember(laboratoryMember);

        // Check if any entry and exit records exist for the FAIR event segment
        if (entryExitRecordEntityList.isPresent() && entryExitRecordEntityList.get().stream()
                .anyMatch(record -> record.getEventSegment() == EventSegment.FAIR)) {
            // If a record exists, deny access and return a response indicating the wristband
            // has already been delivered
            return new WristbandsResponseDto("Access denied: CPF " + laboratoryMember.getCpf()
                    + " with ID " + laboratoryMember.getId() + " has already received the wristband");
        }

        // If no record exists, return a response indicating that the wristband has not been delivered
        return new WristbandsResponseDto("Wristband not delivered: CPF "
                + laboratoryMember.getCpf() + " with ID " + laboratoryMember.getId()
                + " has no check-in record");
    }

    /**
     * Checks the wristband delivery status for a PharmacyRepresentativeEntity.
     * If a wristband has already been delivered for the FAIR event segment, access is denied.
     *
     * @param pharmacyRepresentative the PharmacyRepresentativeEntity to check
     * @return a response indicating the wristband delivery status
     */
    private WristbandsResponseDto checkWristbandDeliveryForPharmacyRepresentative(
            PharmacyRepresentativeEntity pharmacyRepresentative) {
        // Retrieve the entry and exit records associated with the pharmacy representative
        Optional<List<EntryExitRecordEntity>> entryExitRecordEntityList = entryExitRecordRepository
                .findByPharmacyRepresentative(pharmacyRepresentative);

        // Check if any entry and exit records exist for the FAIR event segment
        if (entryExitRecordEntityList.isPresent() && entryExitRecordEntityList.get().stream()
                .anyMatch(record -> record.getEventSegment() == EventSegment.FAIR)) {
            // If a record exists, deny access and return a response indicating the wristband
            // has already been delivered
            return new WristbandsResponseDto("Access denied: CPF " + pharmacyRepresentative.getCpf()
                    + " with ID " + pharmacyRepresentative.getId() + " has already received the wristband");
        }

        // If no record exists, return a response indicating that the wristband has not been delivered
        return new WristbandsResponseDto("Wristband not delivered: CPF "
                + pharmacyRepresentative.getCpf() + " with ID " + pharmacyRepresentative.getId()
                + " has no check-in record");
    }
}
