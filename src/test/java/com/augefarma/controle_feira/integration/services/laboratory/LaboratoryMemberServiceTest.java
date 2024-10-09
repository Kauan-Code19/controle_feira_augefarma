package com.augefarma.controle_feira.integration.services.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.augefarma.controle_feira.services.badge.QRCodeService;
import com.augefarma.controle_feira.services.laboratory.LaboratoryMemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LaboratoryMemberServiceTest {

    @Autowired
    private LaboratoryMemberService laboratoryMemberService;

    @Autowired
    private LaboratoryMemberRepository laboratoryMemberRepository;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private QRCodeService qrCodeService;

    private LaboratoryEntity laboratoryEntity;

    @BeforeEach
    void setUp() {
        // Clear all records in the repositories before each test
        laboratoryMemberRepository.deleteAll();
        laboratoryRepository.deleteAll();

        // Initializes a sample laboratory entity for testing
        laboratoryEntity = new LaboratoryEntity();
        laboratoryEntity.setCorporateReason("Sample Laboratory");
        laboratoryRepository.save(laboratoryEntity); // Save the laboratory to the database
    }

    @AfterEach
    void tearDown() {
        // Resets the BadgeService back to the original one after each test
        ReflectionTestUtils.setField(laboratoryMemberService, "badgeService", badgeService);
    }

    @Test
    void testRegisterLaboratoryMember_Success() {
        // Prepare the DTO for registering a laboratory member
        LaboratoryMemberDto laboratoryMemberDto = new LaboratoryMemberDto();
        laboratoryMemberDto.setName("John Doe");
        laboratoryMemberDto.setCpf("123.456.789-00");
        laboratoryMemberDto.setLaboratoryCorporateReason("Sample Laboratory");

        // Calls the method to register the laboratory member
        LaboratoryMemberResponseDto responseDto = laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);

        // Verifies that the laboratory member was saved in the database
        Optional<LaboratoryMemberEntity> savedMember = laboratoryMemberRepository.findByCpf("123.456.789-00");
        assertTrue(savedMember.isPresent(), "The laboratory member should be saved in the database.");
        assertEquals("John Doe", savedMember.get().getName(), "The name should match the input DTO.");
    }

    @Test
    void testRegisterLaboratoryMember_LaboratoryNotFound() {
        // Prepare the DTO with a non-existent laboratory
        LaboratoryMemberDto laboratoryMemberDto = new LaboratoryMemberDto();
        laboratoryMemberDto.setName("Jane Doe");
        laboratoryMemberDto.setCpf("098.765.432-11");
        laboratoryMemberDto.setLaboratoryCorporateReason("Unknown Laboratory");

        // Asserts that a ResourceNotFoundException is thrown when the laboratory is not found
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);
        });
    }

    @Test
    void testGetLaboratoryMemberById_Success() {
        // Register a laboratory member for testing
        LaboratoryMemberDto laboratoryMemberDto = new LaboratoryMemberDto();
        laboratoryMemberDto.setName("John Doe");
        laboratoryMemberDto.setCpf("123.456.789-00");
        laboratoryMemberDto.setLaboratoryCorporateReason("Sample Laboratory");
        laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);

        // Retrieve the saved member entity by CPF
        LaboratoryMemberEntity savedMember = laboratoryMemberRepository.findByCpf("123.456.789-00").orElseThrow();

        // Calls the method to get the laboratory member by ID
        LaboratoryMemberResponseDto responseDto = laboratoryMemberService.getLaboratoryMemberById(savedMember.getId());

        // Checks that the response contains the expected name
        assertEquals("John Doe", responseDto.getName(), "The name should match the laboratory member.");
    }

    @Test
    void testGetLaboratoryMemberById_NotFound() {
        // Asserts that a ResourceNotFoundException is thrown for a non-existent member ID
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.getLaboratoryMemberById(999L); // Non-existent ID
        });
    }

    @Test
    void testGenerateLaboratoryMemberBadge_Success() {
        // Create a LaboratoryMemberDto for registering a laboratory member
        LaboratoryMemberDto laboratoryMemberDto = new LaboratoryMemberDto();
        laboratoryMemberDto.setName("John Doe");
        laboratoryMemberDto.setCpf("123.456.789-00");
        laboratoryMemberDto.setLaboratoryCorporateReason("Sample Laboratory");

        // Register the laboratory member
        laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);

        // Retrieve the saved laboratory member entity from the database
        LaboratoryMemberEntity laboratoryMemberEntity = laboratoryMemberRepository
                .findByCpf("123.456.789-00").orElseThrow();

        // Calls the method to generate the member's badge
        byte[] result = laboratoryMemberService.generateLaboratoryMemberBadge(laboratoryMemberEntity.getId());

        // Verifies that the badge was generated
        assertNotNull(result, "The badge should not be null.");
        assertTrue(result.length > 0, "The badge data should have content.");
    }

    @Test
    void testGenerateLaboratoryMemberBadge_GenerationError() throws IOException {
        // Prepare a laboratory member for registration
        LaboratoryMemberDto laboratoryMemberDto = new LaboratoryMemberDto();
        laboratoryMemberDto.setName("John Doe");
        laboratoryMemberDto.setCpf("123.456.789-00");
        laboratoryMemberDto.setLaboratoryCorporateReason("Sample Laboratory");
        laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);

        // Retrieve the saved laboratory member entity from the database
        LaboratoryMemberEntity laboratoryMemberEntity = laboratoryMemberRepository
                .findByCpf("123.456.789-00").orElseThrow();

        // Create a custom BadgeService implementation that throws IOException to simulate a failure
        BadgeService faultyBadgeService = new BadgeService(qrCodeService) {
            @Override
            public byte[] generateBadge(LaboratoryMemberEntity member) throws IOException {
                throw new IOException("Simulated badge generation failure");
            }
        };

        // Replace the badgeService in the laboratoryMemberService with the faulty one
        ReflectionTestUtils.setField(laboratoryMemberService, "badgeService", faultyBadgeService);

        // Verifies that a RuntimeException is thrown when badge generation fails
        assertThrows(RuntimeException.class, () -> {
            laboratoryMemberService.generateLaboratoryMemberBadge(laboratoryMemberEntity.getId());
        });
    }

    @Test
    void testGetLaboratoryMemberByNameOrCpf_SuccessWithCpf() {
        // Register a laboratory member
        LaboratoryMemberDto laboratoryMemberDto = new LaboratoryMemberDto();
        laboratoryMemberDto.setName("John Doe");
        laboratoryMemberDto.setCpf("123.456.789-00");
        laboratoryMemberDto.setLaboratoryCorporateReason("Sample Laboratory");

        laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);

        // Calls the method to search for the member by CPF
        List<LaboratoryMemberResponseDto> responseDtoList = laboratoryMemberService
                .getLaboratoryMemberByNameOrCpf("123.456.789-00");

        // Checks that the list contains the expected member
        assertEquals(1, responseDtoList.size(),
                "There should be one laboratory member in the response.");
        assertEquals("John Doe", responseDtoList.get(0).getName(),
                "The name should match the laboratory member.");
    }

    @Test
    void testGetLaboratoryMemberByNameOrCpf_SuccessWithName() {
        // Register a laboratory member
        LaboratoryMemberDto laboratoryMemberDto = new LaboratoryMemberDto();
        laboratoryMemberDto.setName("John Doe");
        laboratoryMemberDto.setCpf("123.456.789-00");
        laboratoryMemberDto.setLaboratoryCorporateReason("Sample Laboratory");

        laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);

        // Calls the method to get members by name
        List<LaboratoryMemberResponseDto> responseDtoList = laboratoryMemberService
                .getLaboratoryMemberByNameOrCpf("John Doe");

        // Verifies that the list contains the expected member
        assertEquals(1, responseDtoList.size(),
                "There should be one laboratory member in the response.");
        assertEquals("John Doe", responseDtoList.get(0).getName(),
                "The name should match the laboratory member.");
    }

    @Test
    void testGetLaboratoryMemberByNameOrCpf_NotFound() {
        // Asserts that a ResourceNotFoundException is thrown for a non-existent CPF
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.getLaboratoryMemberByNameOrCpf("123.456.789-00"); // CPF that doesn't exist
        });

        // Asserts that a ResourceNotFoundException is thrown for a non-existent name
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.getLaboratoryMemberByNameOrCpf("Unknown Name");
        });
    }

}
