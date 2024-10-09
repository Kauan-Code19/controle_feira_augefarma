package com.augefarma.controle_feira.unit.services.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryEntity;
import com.augefarma.controle_feira.entities.laboratory.LaboratoryMemberEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryMemberRepository;
import com.augefarma.controle_feira.repositories.laboratory.LaboratoryRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.augefarma.controle_feira.services.laboratory.LaboratoryMemberService;
import com.google.zxing.WriterException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LaboratoryMemberServiceTest {

    @Mock
    private LaboratoryMemberRepository laboratoryMemberRepository;

    @Mock
    private LaboratoryRepository laboratoryRepository;

    @Mock
    private BadgeService badgeService;

    @InjectMocks
    private LaboratoryMemberService laboratoryMemberService;

    private LaboratoryMemberDto laboratoryMemberDto;
    private LaboratoryEntity laboratoryEntity;

    @BeforeEach
    void setUp() {
        // Initializes a sample laboratory entity for testing
        laboratoryEntity = new LaboratoryEntity();
        laboratoryEntity.setCorporateReason("Sample Laboratory");

        // Initializes a sample laboratory member DTO for testing
        laboratoryMemberDto = new LaboratoryMemberDto();
        laboratoryMemberDto.setName("John Doe");
        laboratoryMemberDto.setCpf("12345678900");
        laboratoryMemberDto.setLaboratoryCorporateReason("Sample Laboratory");
    }

    @Test
    void testRegisterLaboratoryMember_Success() {
        // Simulates finding the laboratory in the repository
        when(laboratoryRepository.findByCorporateReason(laboratoryMemberDto.getLaboratoryCorporateReason()))
                .thenReturn(Optional.of(laboratoryEntity));

        // Calls the method to register the laboratory member
        LaboratoryMemberResponseDto responseDto = laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);

        // Verifies that the laboratory member was saved
        verify(laboratoryMemberRepository, times(1)).save(any(LaboratoryMemberEntity.class));

        // Checks that the response contains the expected name
        assertEquals("John Doe", responseDto.getName(), "The name should match the input DTO.");
    }

    @Test
    void testRegisterLaboratoryMember_LaboratoryNotFound() {
        // Simulates not finding the laboratory in the repository
        when(laboratoryRepository.findByCorporateReason(laboratoryMemberDto.getLaboratoryCorporateReason()))
                .thenReturn(Optional.empty());

        // Asserts that a ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.registerLaboratoryMember(laboratoryMemberDto);
        });
    }

    @Test
    void testGetLaboratoryMemberById_Success() {
        LaboratoryMemberEntity laboratoryMemberEntity = new LaboratoryMemberEntity();
        laboratoryMemberEntity.setId(1L);
        laboratoryMemberEntity.setName("John Doe");
        laboratoryMemberEntity.setLaboratory(this.laboratoryEntity);

        // Simulates finding the laboratory member in the repository
        when(laboratoryMemberRepository.getReferenceById(1L)).thenReturn(laboratoryMemberEntity);

        // Calls the method to get the laboratory member
        LaboratoryMemberResponseDto responseDto = laboratoryMemberService.getLaboratoryMemberById(1L);

        // Checks that the response contains the expected name
        assertEquals("John Doe", responseDto.getName(), "The name should match the laboratory member.");
    }

    @Test
    void testGetLaboratoryMemberById_NotFound() {
        // Simulates not finding the laboratory member in the repository
        when(laboratoryMemberRepository.getReferenceById(1L)).thenThrow(new EntityNotFoundException());

        // Asserts that a ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.getLaboratoryMemberById(1L);
        });
    }

    @Test
    void testGenerateLaboratoryMemberBadge_Success() throws Exception {
        // Simulates finding the laboratory member in the repository
        LaboratoryMemberEntity laboratoryMemberEntity = new LaboratoryMemberEntity();
        laboratoryMemberEntity.setId(1L);
        when(laboratoryMemberRepository.getReferenceById(1L)).thenReturn(laboratoryMemberEntity);

        // Simulates the badge generation
        byte[] badgeData = new byte[]{1, 2, 3}; // Example badge data
        when(badgeService.generateBadge(laboratoryMemberEntity)).thenReturn(badgeData);

        // Calls the method to generate the badge
        byte[] result = laboratoryMemberService.generateLaboratoryMemberBadge(1L);

        // Checks that the badge data is as expected
        assertArrayEquals(badgeData, result, "The badge data should match the generated badge.");
    }

    @Test
    void testGenerateLaboratoryMemberBadge_NotFound() {
        // Simulates not finding the laboratory member in the repository
        when(laboratoryMemberRepository.getReferenceById(1L)).thenThrow(new EntityNotFoundException());

        // Asserts that a ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.generateLaboratoryMemberBadge(1L);
        });
    }

    @Test
    void testGenerateLaboratoryMemberBadge_GenerationError() throws IOException, WriterException {
        // Simulates finding the laboratory member in the repository
        LaboratoryMemberEntity laboratoryMemberEntity = new LaboratoryMemberEntity();
        laboratoryMemberEntity.setId(1L);
        when(laboratoryMemberRepository.getReferenceById(1L)).thenReturn(laboratoryMemberEntity);

        // Simulates an IOException during badge generation
        when(badgeService.generateBadge(laboratoryMemberEntity)).thenThrow(new IOException());

        // Asserts that a RuntimeException is thrown
        assertThrows(RuntimeException.class, () -> {
            laboratoryMemberService.generateLaboratoryMemberBadge(1L);
        });
    }

    @Test
    void testGetLaboratoryMemberByNameOrCpf_SuccessWithCpf() {
        LaboratoryMemberEntity laboratoryMemberEntity = new LaboratoryMemberEntity();
        laboratoryMemberEntity.setId(1L);
        laboratoryMemberEntity.setName("John Doe");
        laboratoryMemberEntity.setCpf("123.456.789-00");
        laboratoryMemberEntity.setLaboratory(this.laboratoryEntity);

        // Simulates finding the laboratory member by CPF
        when(laboratoryMemberRepository.findByCpf("123.456.789-00"))
                .thenReturn(Optional.of(laboratoryMemberEntity));

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
        LaboratoryMemberEntity laboratoryMemberEntity = new LaboratoryMemberEntity();
        laboratoryMemberEntity.setId(1L);
        laboratoryMemberEntity.setName("John Doe");
        laboratoryMemberEntity.setLaboratory(this.laboratoryEntity);

        List<LaboratoryMemberEntity> laboratoryMemberEntities = new ArrayList<>();
        laboratoryMemberEntities.add(laboratoryMemberEntity);

        // Simulates finding the laboratory members by name
        when(laboratoryMemberRepository.findByName("John Doe"))
                .thenReturn(laboratoryMemberEntities);

        List<LaboratoryMemberResponseDto> responseDtoList = laboratoryMemberService
                .getLaboratoryMemberByNameOrCpf("John Doe");

        // Checks that the list contains the expected member
        assertEquals(1, responseDtoList.size(),
                "There should be one laboratory member in the response.");
        assertEquals("John Doe", responseDtoList.get(0).getName(),
                "The name should match the laboratory member.");
    }

    @Test
    void testGetLaboratoryMemberByNameOrCpf_NotFound() {
        // Simulates not finding the laboratory member by CPF or name
        when(laboratoryMemberRepository.findByCpf("123.456.789-00"))
                .thenReturn(Optional.empty());

        when(laboratoryMemberRepository.findByName("Unknown Name"))
                .thenReturn(new ArrayList<>());

        // Asserts that a ResourceNotFoundException is thrown for CPF
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.getLaboratoryMemberByNameOrCpf("123.456.789-00");
        });

        // Asserts that a ResourceNotFoundException is thrown for Name
        assertThrows(ResourceNotFoundException.class, () -> {
            laboratoryMemberService.getLaboratoryMemberByNameOrCpf("Unknown Name");
        });
    }
}
