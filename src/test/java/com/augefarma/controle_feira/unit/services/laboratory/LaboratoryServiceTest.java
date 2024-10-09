package com.augefarma.controle_feira.unit.services.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.services.laboratory.LaboratoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LaboratoryServiceTest {

    @Mock
    private LaboratoryRepository laboratoryRepository;

    @InjectMocks
    private LaboratoryService laboratoryService;

    private LaboratoryDto laboratoryDto;
    private LaboratoryEntity laboratoryEntity;

    @BeforeEach
    void setUp() {
        // Initialize the DTO for testing
        laboratoryDto = new LaboratoryDto();
        laboratoryDto.setCorporateReason("Test Corporate Reason");

        // Initialize the entity for testing
        laboratoryEntity = new LaboratoryEntity();
        laboratoryEntity.setCorporateReason("Test Corporate Reason");
    }

    @Test
    @Transactional
    void testRegisterLaboratory_Success() {
        // Mocking the save behavior of the repository
        when(laboratoryRepository.save(any(LaboratoryEntity.class))).thenReturn(laboratoryEntity);

        // Call the service method to register the laboratory
        LaboratoryResponseDto responseDto = laboratoryService.registerLaboratory(laboratoryDto);

        // Verify that the repository's save method was called once
        verify(laboratoryRepository, times(1)).save(any(LaboratoryEntity.class));

        // Check if the returned DTO has the correct corporate reason
        assertEquals("Test Corporate Reason", responseDto.getCorporateReason(),
                "The corporate reason in the response DTO should match the input DTO.");
    }
}
