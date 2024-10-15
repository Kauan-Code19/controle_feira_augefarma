package com.augefarma.controle_feira.integration.services.socket;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EventSegment;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.exceptions.EntityAlreadyPresentException;
import com.augefarma.controle_feira.exceptions.EntityNotPresentException;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.authentication.ValidateEntryExitService;
import com.augefarma.controle_feira.services.socket.RealTimeUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class RealTimeUpdateServiceTest {

    @Autowired
    private RealTimeUpdateService realTimeUpdateService;

    @Autowired
    private PharmacyRepresentativeRepository pharmacyRepresentativeRepository;

    @Autowired
    private LaboratoryMemberRepository laboratoryMemberRepository;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private ValidateEntryExitService validateEntryExitService;

    private PharmacyRepresentativeEntity pharmacyRepresentative;
    private LaboratoryMemberEntity laboratoryMember;
    private LaboratoryEntity laboratory;

    @BeforeEach
    void setUp() {
        realTimeUpdateService.getEntitiesListResponseDto().getLaboratoryMembers().clear();
        realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives().clear();

        laboratoryRepository.deleteAll();

        pharmacyRepresentative = new PharmacyRepresentativeEntity(1L, "kauan Pereira", "567.765.678-00",
                "56.678,789/0011-78", "razao social", new ArrayList<>());
        laboratory = new LaboratoryEntity(2L, "Laboratory", new ArrayList<>());
        laboratoryMember = new LaboratoryMemberEntity(1L,"member", "456.786.765-00", laboratory, new ArrayList<>());
    }

    // Helper method to add a PharmacyRepresentative and assert its presence in the response DTO
    private void addAndAssertPharmacyRepresentative() {
        // Add the pharmacy representative to the service
        realTimeUpdateService.addPharmacyRepresentativePresent(pharmacyRepresentative);

        // Assert that the added representative is present in the response DTO
        assertTrue(realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives()
                .contains(new PharmacyRepresentativeResponseDto(pharmacyRepresentative)));
    }

    // Helper method to remove a PharmacyRepresentative and assert its absence in the response DTO
    private void removeAndAssertPharmacyRepresentative() {
        // Remove the pharmacy representative from the service
        realTimeUpdateService.removePharmacyRepresentativePresent(pharmacyRepresentative);

        // Assert that the representative is no longer present in the response DTO
        assertFalse(realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives()
                .contains(new PharmacyRepresentativeResponseDto(pharmacyRepresentative)));
    }

    // Helper method to add a LaboratoryMember and assert its presence in the response DTO
    private void addAndAssertLaboratoryMember() {
        // Add the laboratory member to the service
        realTimeUpdateService.addLaboratoryMemberPresent(laboratoryMember);

        // Assert that the added member is present in the response DTO
        assertTrue(realTimeUpdateService.getEntitiesListResponseDto().getLaboratoryMembers()
                .contains(new LaboratoryMemberResponseDto(laboratoryMember)));
    }

    // Helper method to remove a LaboratoryMember and assert its absence in the response DTO
    private void removeAndAssertLaboratoryMember() {
        // Remove the laboratory member from the service
        realTimeUpdateService.removeLaboratoryMemberPresent(laboratoryMember);

        // Assert that the member is no longer present in the response DTO
        assertFalse(realTimeUpdateService.getEntitiesListResponseDto().getLaboratoryMembers()
                .contains(new LaboratoryMemberResponseDto(laboratoryMember)));
    }

    // Test to ensure a PharmacyRepresentative can be added successfully
    @Test
    void shouldAddPharmacyRepresentativeSuccessfully() {
        // Attempt to add and then immediately remove the pharmacy representative
        addAndAssertPharmacyRepresentative();
    }

    // Test to ensure adding a duplicate PharmacyRepresentative throws an exception
    @Test
    void shouldThrowExceptionWhenAddingDuplicatePharmacyRepresentative() {
        // Add the representative first
        addAndAssertPharmacyRepresentative();

        // Attempt to add the same representative again and expect an exception
        EntityAlreadyPresentException exception = assertThrows(EntityAlreadyPresentException.class, () ->
                realTimeUpdateService.addPharmacyRepresentativePresent(pharmacyRepresentative)
        );
        // Assert that the correct exception message is returned
        assertEquals("The entity is already present and cannot be added again.", exception.getMessage());

        // Clean up by removing the representative

    }

    // Test to ensure a PharmacyRepresentative can be removed successfully
    @Test
    void shouldRemovePharmacyRepresentativeSuccessfully() {
        // Attempt to remove a pharmacy representative that has not been added
        addAndAssertPharmacyRepresentative();
        removeAndAssertPharmacyRepresentative();
    }

    // Test to ensure removing a non-existent PharmacyRepresentative throws an exception
    @Test
    void shouldThrowExceptionWhenRemovingNonExistentPharmacyRepresentative() {
        // Attempt to remove a representative that does not exist and expect an exception
        EntityNotPresentException exception = assertThrows(EntityNotPresentException.class, () ->
                realTimeUpdateService.removePharmacyRepresentativePresent(pharmacyRepresentative)
        );
        // Assert that the correct exception message is returned
        assertEquals("The entity is not present and cannot be removed.", exception.getMessage());
    }

    // Test to ensure a LaboratoryMember can be added successfully
    @Test
    void shouldAddLaboratoryMemberSuccessfully() {
        // Attempt to add and then immediately remove the laboratory member
        addAndAssertLaboratoryMember();
    }

    // Test to ensure adding a duplicate LaboratoryMember throws an exception
    @Test
    void shouldThrowExceptionWhenAddingDuplicateLaboratoryMember() {
        // Add the laboratory member first
        addAndAssertLaboratoryMember();

        // Attempt to add the same laboratory member again and expect an exception
        EntityAlreadyPresentException exception = assertThrows(EntityAlreadyPresentException.class, () ->
                realTimeUpdateService.addLaboratoryMemberPresent(laboratoryMember)
        );
        // Assert that the correct exception message is returned
        assertEquals("The entity is already present and cannot be added again.", exception.getMessage());

        // Clean up by removing the member
    }

    // Test to ensure a LaboratoryMember can be removed successfully
    @Test
    void shouldRemoveLaboratoryMemberSuccessfully() {
        // Attempt to remove a laboratory member that has not been added
        addAndAssertLaboratoryMember();
        removeAndAssertLaboratoryMember();
    }

    // Test to ensure removing a non-existent LaboratoryMember throws an exception
    @Test
    void shouldThrowExceptionWhenRemovingNonExistentLaboratoryMember() {
        // Attempt to remove a member that does not exist and expect an exception
        EntityNotPresentException exception = assertThrows(EntityNotPresentException.class, () ->
                realTimeUpdateService.removeLaboratoryMemberPresent(laboratoryMember)
        );
        // Assert that the correct exception message is returned
        assertEquals("The entity is not present and cannot be removed.", exception.getMessage());
    }

    // Test to ensure the initial state can be successfully initialized
    @Test
    void shouldInitializeStateSuccessfully() {
        // Save initial entities to the repository
        pharmacyRepresentativeRepository.save(pharmacyRepresentative);
        laboratoryRepository.save(laboratory);
        laboratoryMemberRepository.save(laboratoryMember);

        // Validate entry for both representatives
        validateEntryExitService.validateEntry("567.765.678-00", EventSegment.BUFFET);
        validateEntryExitService.validateEntry("456.786.765-00", EventSegment.BUFFET);

        // Remove both representatives
        realTimeUpdateService.removePharmacyRepresentativePresent(pharmacyRepresentative);
        realTimeUpdateService.removeLaboratoryMemberPresent(laboratoryMember);

        // Initialize the state
        realTimeUpdateService.initializeState();

        // Assert that the initial representatives are restored
        assertTrue(realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives()
                .contains(new PharmacyRepresentativeResponseDto(pharmacyRepresentative)));
        assertTrue(realTimeUpdateService.getEntitiesListResponseDto().getLaboratoryMembers()
                .contains(new LaboratoryMemberResponseDto(laboratoryMember)));

        // Validate exits for both representatives
        validateEntryExitService.validateExit("567.765.678-00");
        validateEntryExitService.validateExit("456.786.765-00");
    }

    // Test to ensure initializing state with null entities results in empty lists
    @Test
    void shouldInitializeStateWithNullEntities() {

        // Initialize the state with no saved entities
        realTimeUpdateService.initializeState();

        // Assert that the representatives lists are empty
        assertTrue(realTimeUpdateService.getEntitiesListResponseDto().getPharmacyRepresentatives().isEmpty());
        assertTrue(realTimeUpdateService.getEntitiesListResponseDto().getLaboratoryMembers().isEmpty());
    }
}