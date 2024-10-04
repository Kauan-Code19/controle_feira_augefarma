package com.augefarma.controle_feira.integration.services.authentication;

import com.augefarma.controle_feira.dtos.authentication.LoginDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.exceptions.InvalidCredentialsException;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthenticationServiceTest {

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testAuthenticate_ValidCredentials() {
        // Create and save a test administrator with valid credentials
        createAndSaveAdministrator("Admin Test", "admin@test.com", "Password@123");

        // Create a LoginDto object with the credentials to be tested
        LoginDto loginDto = createLoginDto("admin@test.com", "Password@123");

        // Authenticate the user using the authentication service
        Authentication authResult = authenticationService
                .authenticate(loginDto.email(), loginDto.password(), authenticationManager);

        // Assert that the authentication result is valid
        assertAuthenticationValidResponse(authResult, loginDto);
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        // Create a LoginDto object with invalid credentials
        LoginDto loginDto = createLoginDto("admin@test.com", "wrongPassword");

        // Assert that an InvalidCredentialsException is thrown when attempting to authenticate
        assertThrows(InvalidCredentialsException.class, () -> authenticationService
                .authenticate(loginDto.email(), loginDto.password(), authenticationManager));
    }

    @Test
    public void testLoadUserByUsername_ValidEmail() {
        // Create and save a test administrator to ensure the user exists
        createAndSaveAdministrator("Admin Test", "admin@test.com", "Password@123");

        // Create a LoginDto object with valid credentials
        LoginDto loginDto = createLoginDto("admin@test.com", "Password@123");

        // Load the user details by the given email
        UserDetails userDetails = authenticationService.loadUserByUsername(loginDto.email());

        // Assert that the user details are not null and match the expected email
        assertNotNull(userDetails);
        assertEquals(loginDto.email(), userDetails.getUsername());
    }

    // Helper method to create and save an Administrator entity
    private void createAndSaveAdministrator(String fullName, String email, String rawPassword) {
        AdministratorEntity administrator = new AdministratorEntity();

        // Set the properties for the administrator
        administrator.setFullName(fullName);
        administrator.setEmail(email);
        administrator.setPassword(passwordEncoder.encode(rawPassword)); // Encode the password before saving

        // Save the administrator entity to the repository
        administratorRepository.save(administrator);
    }

    // Helper method to create a LoginDto with email and password
    private LoginDto createLoginDto(String email, String password) {
        return new LoginDto(email, password); // Create and return the LoginDto
    }

    // Helper method to assert that the authentication result is valid
    private void assertAuthenticationValidResponse(Authentication result, LoginDto loginDto) {
        assertNotNull(result); // Assert that the result is not null
        assertEquals(loginDto.email(), result.getName()); // Assert that the authenticated name matches the login email

        UserDetails userDetails = (UserDetails) result.getPrincipal(); // Get the user details from the authentication result

        // Verify that the provided password matches the encoded password in the user details
        verifyPasswordEncoding(loginDto.password(), userDetails.getPassword());
    }

    // Helper method to verify password encoding
    private void verifyPasswordEncoding(String rawPassword, String encodedPassword) {
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword)); // Assert that the raw password matches the encoded password
    }
}
