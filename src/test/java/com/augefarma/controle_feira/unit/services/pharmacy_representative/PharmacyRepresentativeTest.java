package com.augefarma.controle_feira.unit.services.pharmacy_representative;

import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.entities.pharmacy_representative.PharmacyRepresentativeEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.pharmacy_representative.PharmacyRepresentativeRepository;
import com.augefarma.controle_feira.services.badge.BadgeService;
import com.augefarma.controle_feira.services.pharmacy_representative.PharmacyRepresentativeService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PharmacyRepresentativeTest {

    @Mock
    private PharmacyRepresentativeRepository pharmacyRepresentativeRepository;

    @Mock
    private BadgeService badgeService;

    @InjectMocks
    private PharmacyRepresentativeService pharmacyRepresentativeService;

    private PharmacyRepresentativeDto pharmacyRepresentativeDto;
    private PharmacyRepresentativeEntity pharmacyRepresentative;

    @BeforeEach
    void setUp() {
        pharmacyRepresentativeDto = new PharmacyRepresentativeDto();
        pharmacyRepresentativeDto.setName("Kauan Pereira");
        pharmacyRepresentativeDto.setCpf("789.567-00");
        pharmacyRepresentativeDto.setCnpj("67.456.345/0012-98");
        pharmacyRepresentativeDto.setCorporateReason("Razao Social");

        pharmacyRepresentative = new PharmacyRepresentativeEntity();
        pharmacyRepresentative.setName("Kauan Pereira");
        pharmacyRepresentative.setCpf("789.567-00");
        pharmacyRepresentative.setCnpj("67.456.345/0012-98");
        pharmacyRepresentative.setCorporateReason("Razao Social");
    }

    @Test
    public void registerPharmacyRepresentative_Success() {
        PharmacyRepresentativeResponseDto pharmacyRepresentativeResponseDto = new PharmacyRepresentativeResponseDto
                (this.pharmacyRepresentative);

        when(pharmacyRepresentativeRepository.save(any(PharmacyRepresentativeEntity.class)))
                .thenReturn(this.pharmacyRepresentative);

        PharmacyRepresentativeResponseDto responseDto = pharmacyRepresentativeService
                .registerPharmacyRepresentative(pharmacyRepresentativeDto);

        assertNotNull(responseDto, "The response should not be null.");

        assertEquals(pharmacyRepresentativeDto.getName(), responseDto.getName(),
                "The name should match the input DTO.");

        assertEquals(pharmacyRepresentativeDto.getCpf(), responseDto.getCpf(),
                "The CPF should match the input DTO.");

        assertEquals(pharmacyRepresentativeDto.getCnpj(), responseDto.getCnpj(),
                "The CNPJ should match the input DTO.");

        verify(pharmacyRepresentativeRepository, times(1))
                .save(any(PharmacyRepresentativeEntity.class));
    }

    @Test
    void testGetPharmacyRepresentativeById_Success() {
        this.pharmacyRepresentative.setId(1L);

        when(pharmacyRepresentativeRepository.getReferenceById(1L)).thenReturn(this.pharmacyRepresentative);

        PharmacyRepresentativeResponseDto responseDto = pharmacyRepresentativeService
                .getPharmacyRepresentativeById(1L);

        assertNotNull(responseDto, "The response should not be null.");

        // Verifica se o ID no responseDto corresponde ao ID esperado
        assertEquals(1L, responseDto.getId(), "The ID should match the expected ID.");

        // Verifica se o nome no responseDto corresponde ao nome da entidade
        assertEquals(pharmacyRepresentative.getName(), responseDto.getName(),
                "The name should match the entity name.");

        // Verifica se o CPF no responseDto corresponde ao CPF da entidade
        assertEquals(pharmacyRepresentative.getCpf(), responseDto.getCpf(),
                "The CPF should match the entity CPF.");

        // Verifica se o CNPJ no responseDto corresponde ao CNPJ da entidade
        assertEquals(pharmacyRepresentative.getCnpj(), responseDto.getCnpj(),
                "The CNPJ should match the entity CNPJ.");

        // Verifica se o método getReferenceById() do repositório foi chamado uma vez
        verify(pharmacyRepresentativeRepository, times(1)).getReferenceById(1L);
    }

    @Test
    void testGetPharmacyRepresentativeById_NotFound() {
        // Simulates not finding the laboratory member in the repository
        when(pharmacyRepresentativeRepository.getReferenceById(1L)).thenThrow(new EntityNotFoundException());

        // Asserts that a ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            pharmacyRepresentativeService.getPharmacyRepresentativeById(1L);
        });
    }

    @Test
    void testGenerateLaboratoryMemberBadge_Success() throws Exception {
        this.pharmacyRepresentative.setId(1L);

        when(pharmacyRepresentativeRepository.getReferenceById(1L)).thenReturn(this.pharmacyRepresentative);

        byte[] badgeData = new byte[]{1, 2, 3}; // Example badge data
        when(badgeService.generateBadge(this.pharmacyRepresentative)).thenReturn(badgeData);

        byte[] result = pharmacyRepresentativeService.generatePharmacyRepresentativeBadge(1L);

        assertArrayEquals(badgeData, result, "The badge data should match the generated badge.");
    }

    @Test
    void testGenerateLaboratoryMemberBadge_NotFound() {
        // Simulates not finding the pharmacy representative in the repository
        when(pharmacyRepresentativeRepository.getReferenceById(1L)).thenThrow(new EntityNotFoundException());

        // Asserts that a ResourceNotFoundException is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            pharmacyRepresentativeService.generatePharmacyRepresentativeBadge(1L);
        });
    }

    @Test
    void testGenerateLaboratoryMemberBadge_GenerationError() throws IOException, WriterException {

        this.pharmacyRepresentative.setId(1L);
        when(pharmacyRepresentativeRepository.getReferenceById(1L)).thenReturn(this.pharmacyRepresentative);

        // Simulates an IOException during badge generation
        when(badgeService.generateBadge(this.pharmacyRepresentative)).thenThrow(new IOException());

        // Asserts that a RuntimeException is thrown
        assertThrows(RuntimeException.class, () -> {
            pharmacyRepresentativeService.generatePharmacyRepresentativeBadge(1L);
        });
    }

    @Test
    void testGetLaboratoryMemberByNameOrCpf_SuccessWithCpf() {
        // Simulates finding the pharmacy representative by CPF
        when(pharmacyRepresentativeRepository.findByCpf("123.456.789-00"))
                .thenReturn(Optional.of(this.pharmacyRepresentative));

        List<PharmacyRepresentativeResponseDto> responseDtoList = pharmacyRepresentativeService
                .getPharmacyRepresentativeByNameOrCpf("123.456.789-00");

        // Checks that the list contains the expected member
        assertEquals(1, responseDtoList.size(),
                "There should be one laboratory member in the response.");
        assertEquals("Kauan Pereira", responseDtoList.get(0).getName(),
                "The name should match the laboratory member.");
    }

    @Test
    void testGetLaboratoryMemberByNameOrCpf_SuccessWithName() {

        List<PharmacyRepresentativeEntity> pharmacyRepresentativeEntities = new ArrayList<>();
        pharmacyRepresentativeEntities.add(this.pharmacyRepresentative);

        // Simulates finding the pharmacy representative by name
        when(pharmacyRepresentativeRepository.findByName("John Doe"))
                .thenReturn(pharmacyRepresentativeEntities);

        List<PharmacyRepresentativeResponseDto> responseDtoList = pharmacyRepresentativeService
                .getPharmacyRepresentativeByNameOrCpf("John Doe");

        // Checks that the list contains the expected member
        assertEquals(1, responseDtoList.size(),
                "There should be one laboratory member in the response.");
        assertEquals("Kauan Pereira", responseDtoList.get(0).getName(),
                "The name should match the laboratory member.");
    }

    @Test
    void testGetLaboratoryMemberByNameOrCpf_NotFound() {
        // Simulates not finding the laboratory member by CPF or name
        when(pharmacyRepresentativeRepository.findByCpf("123.456.789-00"))
                .thenReturn(Optional.empty());

        when(pharmacyRepresentativeRepository.findByName("Unknown Name"))
                .thenReturn(new ArrayList<>());

        // Asserts that a ResourceNotFoundException is thrown for CPF
        assertThrows(ResourceNotFoundException.class, () -> {
            pharmacyRepresentativeService.getPharmacyRepresentativeByNameOrCpf("123.456.789-00");
        });

        // Asserts that a ResourceNotFoundException is thrown for Name
        assertThrows(ResourceNotFoundException.class, () -> {
            pharmacyRepresentativeService.getPharmacyRepresentativeByNameOrCpf("Unknown Name");
        });
    }
}
