package com.augefarma.controle_feira.integration.services.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.services.laboratory.LaboratoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LaboratoryServiceTest {

    @Autowired
    private LaboratoryService laboratoryService;

    @Autowired
    private LaboratoryRepository laboratoryRepository;

    private LaboratoryDto laboratoryDto;

    @BeforeEach
    void setUp() {
        // Initialize the DTO for testing
        laboratoryDto = new LaboratoryDto();
        laboratoryDto.setCorporateReason("Test Corporate Reason");
    }

    @Test
    public void testRegisterLaboratory_Success() {
        LaboratoryResponseDto response = laboratoryService.registerLaboratory(this.laboratoryDto);

        // Verifies that the response is not null
        assertNotNull(response, "The response should not be null.");

        // Verifies that the laboratory ID was generated
        assertNotNull(response.getId(), "The laboratory ID should be generated.");

        // Verifies that the corporate reason in the response matches the input DTO
        assertEquals("Test Corporate Reason", response.getCorporateReason(),
                "The corporate reason should match the input DTO.");

        // Check if the laboratory was saved in the database by querying
        Optional<LaboratoryEntity> savedLaboratory = laboratoryRepository.findById(response.getId());
        assertTrue(savedLaboratory.isPresent(), "The laboratory should be saved in the database.");
        assertEquals("Test Corporate Reason", savedLaboratory.get().getCorporateReason(),
                "The corporate reason should match the saved entity.");
    }
}
