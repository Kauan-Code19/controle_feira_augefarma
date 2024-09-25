package com.augefarma.controle_feira.integration.services.administrator;

import com.augefarma.controle_feira.dtos.administrator.AdministratorDto;
import com.augefarma.controle_feira.dtos.administrator.AdministratorResponseDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.administrator.AdministratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class AdministratorServiceTest {

    @Autowired
    private AdministratorRepository administratorRepository; // Injects the actual repository for database operations

    @Autowired
    private AdministratorService administratorService; // Injects the service that is being tested

    @Autowired
    private PasswordEncoder passwordEncoder; // Injects the password encoder to handle password encryption and verification

    /**
     * Tests the registration of an administrator and verifies the saved entity in the database.
     * Ensures that the password is encoded correctly and that the administrator details are stored as expected.
     */
    @Test
    public void testRegisterAdministrator() {
        // Create an AdministratorDto representing the administrator's input data
        AdministratorDto administratorDto = createAdministratorDto("Admin Test",
                "admin@test.com", "Password@123");

        // Call the service method to register the administrator
        AdministratorResponseDto administratorResponseDto = administratorService
                .registerAdministrator(administratorDto);

        // Retrieve the saved administrator entity from the repository by email
        AdministratorEntity savedAdministrator = (AdministratorEntity) administratorRepository
                .findByEmail(administratorDto.getEmail());

        // If the saved administrator is not found, throw an assertion error
        if (savedAdministrator == null) throw new AssertionError("Administrator not found");

        // Verify that the response DTO matches the saved administrator entity
        assertAdministratorResponse(administratorResponseDto, savedAdministrator);

        // Verify that the saved password was encoded correctly
        verifyPasswordEncoding(administratorDto.getPassword(), savedAdministrator.getPassword());
    }

    /**
     * Creates an AdministratorDto with the given details.
     *
     * @param name     the full name of the administrator
     * @param email    the email address of the administrator
     * @param password the password in plain text
     * @return an AdministratorDto with the specified details
     */
    private AdministratorDto createAdministratorDto(String name, String email, String password) {
        // Create and populate the DTO with the provided name, email, and password
        AdministratorDto administratorDto = new AdministratorDto();
        administratorDto.setName(name);
        administratorDto.setEmail(email);
        administratorDto.setPassword(password);
        return administratorDto;
    }

    /**
     * Asserts that the AdministratorResponseDto returned from the service
     * matches the saved AdministratorEntity in the repository.
     *
     * @param responseDto      the response DTO returned from the service
     * @param savedAdministrator the entity that was saved in the repository
     */
    private void assertAdministratorResponse(AdministratorResponseDto responseDto,
                                             AdministratorEntity savedAdministrator) {
        // Ensure the response DTO is not null
        assertNotNull(responseDto);

        // Ensure the full name and email in the response DTO match the saved administrator entity
        assertEquals(savedAdministrator.getFullName(), responseDto.getFullName());
        assertEquals(savedAdministrator.getEmail(), responseDto.getEmail());
    }

    /**
     * Verifies that the raw password matches the encoded password stored in the database.
     *
     * @param rawPassword    the original plain text password provided by the administrator
     * @param encodedPassword the encoded password stored in the AdministratorEntity
     */
    private void verifyPasswordEncoding(String rawPassword, String encodedPassword) {
        // Assert that the encoded password matches the raw password when decoded
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
