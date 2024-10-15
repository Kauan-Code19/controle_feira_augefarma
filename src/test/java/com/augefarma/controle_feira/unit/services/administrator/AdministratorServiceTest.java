package com.augefarma.controle_feira.unit.services.administrator;

import com.augefarma.controle_feira.dtos.administrator.AdministratorDto;
import com.augefarma.controle_feira.dtos.administrator.AdministratorResponseDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.administrator.AdministratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdministratorServiceTest {

    @Mock
    private AdministratorRepository administratorRepository; // Mock the repository to simulate database operations

    @Mock
    private PasswordEncoder passwordEncoder; // Mock the password encoder

    @InjectMocks
    private AdministratorService administratorService; // Inject the service being tested

    /**
     * Tests the registration of an administrator, ensuring that the service correctly
     * saves the administrator and encodes the password.
     */
    @Test
    public void testRegisterAdministrator() {
        // Create a DTO representing the administrator's information
        AdministratorDto administratorDto = createAdministratorDto("Admin Test",
                "admin@test.com", "Password@123");

        // Create a mock entity based on the DTO
        AdministratorEntity administratorEntityMock = createMockEntity(administratorDto);

        // Simulate the behavior of saving the administrator entity in the repository
        when(administratorRepository.save(any(AdministratorEntity.class))).thenReturn(administratorEntityMock);

        // Call the service method to register the administrator
        AdministratorResponseDto administratorResponseDto = administratorService
                .registerAdministrator(administratorDto);

        // Verify the response DTO contains the expected administrator details
        assertAdministratorResponse(administratorResponseDto, administratorEntityMock);

        // Ensure the repository's save method was called exactly once
        verify(administratorRepository, times(1)).save(any(AdministratorEntity.class));
    }

    /**
     * Creates an AdministratorDto with the given details.
     *
     * @param name the full name of the administrator
     * @param email the email address of the administrator
     * @param password the password in plain text
     * @return an AdministratorDto with the specified details
     */
    private AdministratorDto createAdministratorDto(String name, String email, String password) {
        // Create and populate the DTO with the given values
        AdministratorDto administratorDto = new AdministratorDto();
        administratorDto.setName(name);
        administratorDto.setEmail(email);
        administratorDto.setPassword(password);
        return administratorDto;
    }

    /**
     * Creates a mock AdministratorEntity based on the given DTO.
     *
     * @param dto the DTO containing the administrator's details
     * @return an AdministratorEntity with the encoded password and details from the DTO
     */
    private AdministratorEntity createMockEntity(AdministratorDto dto) {
        // Create the entity and set the values from the DTO
        AdministratorEntity administratorEntity = new AdministratorEntity();
        administratorEntity.setFullName(dto.getName());
        administratorEntity.setEmail(dto.getEmail());

        // Encode the password using the password encoder
        administratorEntity.setPassword(dto.getPassword()); // Here you can just set the raw password for the mock
        return administratorEntity;
    }

    /**
     * Asserts that the AdministratorResponseDto matches the AdministratorEntity.
     *
     * @param responseDto the response DTO returned from the service
     * @param mockEntity the mock entity used in the test
     */
    private void assertAdministratorResponse(AdministratorResponseDto responseDto, AdministratorEntity mockEntity) {
        // Ensure the response is not null
        assertNotNull(responseDto);

        // Ensure the full name and email in the response match the mock entity
        assertEquals(mockEntity.getFullName(), responseDto.getFullName());
        assertEquals(mockEntity.getEmail(), responseDto.getEmail());
    }
}