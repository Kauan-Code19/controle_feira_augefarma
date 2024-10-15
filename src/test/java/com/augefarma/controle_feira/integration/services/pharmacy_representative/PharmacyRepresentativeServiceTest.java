package com.augefarma.controle_feira.integration.services.pharmacy_representative;

import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.augefarma.controle_feira.services.badge.QRCodeService;
import com.augefarma.controle_feira.services.pharmacy_representative.PharmacyRepresentativeService;
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
public class PharmacyRepresentativeServiceTest {
    @Autowired
    private PharmacyRepresentativeService pharmacyRepresentativeService;

    @Autowired
    private PharmacyRepresentativeRepository pharmacyRepresentativeRepository;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private QRCodeService qrCodeService;

    private PharmacyRepresentativeDto pharmacyRepresentativeDto;

    @BeforeEach
    void setUp() {
        // Clear all records before each test
        pharmacyRepresentativeRepository.deleteAll();

        // Prepare a sample DTO for testing
        pharmacyRepresentativeDto = new PharmacyRepresentativeDto();
        pharmacyRepresentativeDto.setName("John Doe");
        pharmacyRepresentativeDto.setCpf("123.456.789-00");
        pharmacyRepresentativeDto.setCnpj("12.345.678/0001-00");
        pharmacyRepresentativeDto.setCorporateReason("Sample Pharmacy");
    }

    @AfterEach
    void tearDown() {
        // Reset BadgeService to the original one after each test
        ReflectionTestUtils.setField(pharmacyRepresentativeService, "badgeService", badgeService);
    }

    @Test
    void testRegisterPharmacyRepresentative_Success() {
        // Register the pharmacy representative
        PharmacyRepresentativeResponseDto responseDto = pharmacyRepresentativeService
                .registerPharmacyRepresentative(pharmacyRepresentativeDto);

        // Verify that the pharmacy representative was saved in the database
        Optional<PharmacyRepresentativeEntity> savedRepresentative = pharmacyRepresentativeRepository
                .findByCpf("123.456.789-00");
        assertTrue(savedRepresentative.isPresent(), "The pharmacy representative should be saved.");
        assertEquals("John Doe", savedRepresentative.get().getName(), "The name should match the input DTO.");
    }

    @Test
    void testGetPharmacyRepresentativeById_Success() {
        // Register a pharmacy representative
        pharmacyRepresentativeService.registerPharmacyRepresentative(pharmacyRepresentativeDto);

        // Retrieve the saved representative by CPF
        PharmacyRepresentativeEntity savedRepresentative = pharmacyRepresentativeRepository
                .findByCpf("123.456.789-00").orElseThrow();

        // Get the pharmacy representative by ID
        PharmacyRepresentativeResponseDto responseDto = pharmacyRepresentativeService
                .getPharmacyRepresentativeById(savedRepresentative.getId());

        // Verify the response
        assertEquals("John Doe", responseDto.getName(), "The name should match the representative.");
    }

    @Test
    void testGetPharmacyRepresentativeById_NotFound() {
        // Asserts that a ResourceNotFoundException is thrown for a non-existent ID
        assertThrows(ResourceNotFoundException.class, () -> {
            pharmacyRepresentativeService.getPharmacyRepresentativeById(999L);
        });

        assertThrows(ResourceNotFoundException.class, () -> {
            pharmacyRepresentativeService.generatePharmacyRepresentativeBadge(999L);
        });
    }

    @Test
    void testGeneratePharmacyRepresentativeBadge_Success() {
        // Register the pharmacy representative
        pharmacyRepresentativeService.registerPharmacyRepresentative(pharmacyRepresentativeDto);

        // Retrieve the saved representative
        PharmacyRepresentativeEntity savedRepresentative = pharmacyRepresentativeRepository
                .findByCpf("123.456.789-00").orElseThrow();

        // Generate the badge for the representative
        byte[] result = pharmacyRepresentativeService.generatePharmacyRepresentativeBadge(savedRepresentative.getId());

        // Verify that the badge was generated
        assertNotNull(result, "The badge should not be null.");
        assertTrue(result.length > 0, "The badge data should have content.");
    }

    @Test
    void testGeneratePharmacyRepresentativeBadge_GenerationError() throws IOException {
        // Register the pharmacy representative
        pharmacyRepresentativeService.registerPharmacyRepresentative(pharmacyRepresentativeDto);

        // Retrieve the saved representative
        PharmacyRepresentativeEntity savedRepresentative = pharmacyRepresentativeRepository
                .findByCpf("123.456.789-00").orElseThrow();

        // Create a faulty BadgeService that simulates a failure
        BadgeService faultyBadgeService = new BadgeService(qrCodeService) {
            @Override
            public byte[] generateBadge(PharmacyRepresentativeEntity representative) throws IOException {
                throw new IOException("Simulated badge generation failure");
            }
        };

        // Replace BadgeService with the faulty one
        ReflectionTestUtils.setField(pharmacyRepresentativeService, "badgeService", faultyBadgeService);

        // Verify that a RuntimeException is thrown when badge generation fails
        assertThrows(RuntimeException.class, () -> {
            pharmacyRepresentativeService.generatePharmacyRepresentativeBadge(savedRepresentative.getId());
        });
    }

    @Test
    void testGetPharmacyRepresentativeByNameOrCpf_SuccessWithCpf() {
        // Register the pharmacy representative
        pharmacyRepresentativeService.registerPharmacyRepresentative(pharmacyRepresentativeDto);

        // Get the representative by CPF
        List<PharmacyRepresentativeResponseDto> responseDtoList = pharmacyRepresentativeService
                .getPharmacyRepresentativeByNameOrCpf("123.456.789-00");

        // Verify that the response contains the expected representative
        assertEquals(1, responseDtoList.size(), "There should be one representative in the response.");
        assertEquals("John Doe", responseDtoList.get(0).getName(), "The name should match.");
    }

    @Test
    void testGetPharmacyRepresentativeByNameOrCpf_SuccessWithName() {
        // Register the pharmacy representative
        pharmacyRepresentativeService.registerPharmacyRepresentative(pharmacyRepresentativeDto);

        // Get the representative by name
        List<PharmacyRepresentativeResponseDto> responseDtoList = pharmacyRepresentativeService
                .getPharmacyRepresentativeByNameOrCpf("John Doe");

        // Verify that the response contains the expected representative
        assertEquals(1, responseDtoList.size(), "There should be one representative in the response.");
        assertEquals("John Doe", responseDtoList.get(0).getName(), "The name should match.");
    }

    @Test
    void testGetPharmacyRepresentativeByNameOrCpf_NotFound() {
        // Asserts that a ResourceNotFoundException is thrown for a non-existent CPF
        assertThrows(ResourceNotFoundException.class, () -> {
            pharmacyRepresentativeService.getPharmacyRepresentativeByNameOrCpf("000.000.000-00");
        });

        // Asserts that a ResourceNotFoundException is thrown for a non-existent name
        assertThrows(ResourceNotFoundException.class, () -> {
            pharmacyRepresentativeService.getPharmacyRepresentativeByNameOrCpf("Unknown Name");
        });
    }
}
