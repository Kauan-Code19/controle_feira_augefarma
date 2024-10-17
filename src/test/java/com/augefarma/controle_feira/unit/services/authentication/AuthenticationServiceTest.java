package com.augefarma.controle_feira.unit.services.authentication;

import com.augefarma.controle_feira.dtos.authentication.LoginDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.exceptions.InvalidCredentialsException;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AdministratorRepository administratorRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    /**
     * Tests loading a user by a valid email.
     */
    @Test
    public void testLoadUserByUsername_ValidEmail() {
        String email = "admin@test.com"; // Administrator's email
        AdministratorEntity administratorEntity = new AdministratorEntity(); // Create administrator entity
        administratorEntity.setEmail(email); // Set the administrator's email

        // Set up the mock behavior to return the administrator entity when the email is queried
        when(administratorRepository.findByEmail(email)).thenReturn(administratorEntity);

        // Call the loadUserByUsername method and obtain the user details
        UserDetails userDetails = authenticationService.loadUserByUsername(email);

        // Verify that the user details are not null and that the email is correct
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    /**
     * Tests authentication with valid credentials.
     */
    @Test
    public void testAuthenticate_ValidCredentials() {
        LoginDto loginDto = createLoginDto("admin@test.com", "Password@123"); // Create login DTO
        String encryptedPassword = "encryptedPassword"; // Simulated encrypted password

        // Set up the mock to return true when comparing the password
        when(passwordEncoder.matches(loginDto.password(), encryptedPassword)).thenReturn(true);

        // Create authentication request
        Authentication authRequest = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        // Create expected authentication result
        Authentication authResult = new UsernamePasswordAuthenticationToken(loginDto.email(), encryptedPassword);

        // Set up the mock to return the authentication result
        when(authenticationManager.authenticate(authRequest)).thenReturn(authResult);

        // Call the authenticate method and obtain the result
        Authentication result = authenticationService.authenticate(loginDto.email(), loginDto.password(),
                authenticationManager);

        // Verify that the authentication response is valid
        assertAuthenticationValidResponse(result, loginDto);
    }

    /**
     * Tests authentication with invalid credentials.
     */
    @Test
    public void testAuthenticate_InvalidCredentials() {
        // Create login DTO with invalid credentials
        LoginDto loginDto = createLoginDto("admin@test.com", "wrongPassword");

        // Create authentication request
        Authentication authRequest = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());

        // Set up the mock to throw an exception when trying to authenticate with invalid credentials
        when(authenticationManager.authenticate(authRequest))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        // Verify that an invalid credentials exception is thrown
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.authenticate(loginDto.email(), loginDto.password(), authenticationManager);
        });
    }

    /**
     * Creates a LoginDto object with the given email and password.
     *
     * @param email    the email to set
     * @param password the password to set
     * @return a LoginDto object with the specified email and password
     */
    private LoginDto createLoginDto(String email, String password) {
        return new LoginDto(email, password); // Returns a new login DTO
    }

    /**
     * Asserts that the authentication response is valid.
     *
     * @param result   the Authentication result to check
     * @param loginDto the LoginDto used for authentication
     */
    private void assertAuthenticationValidResponse(Authentication result, LoginDto loginDto) {
        assertNotNull(result); // Verify that the result is not null
        assertEquals(loginDto.email(), result.getName()); // Verify that the name matches the email from the DTO

        // Verify that the password was encoded correctly
        verifyPasswordEncoding(loginDto.password(), (String) result.getCredentials());
    }

    /**
     * Verifies that the raw password matches the encoded password.
     *
     * @param rawPassword     the raw password to check
     * @param encodedPassword the encoded password to compare with
     */
    private void verifyPasswordEncoding(String rawPassword, String encodedPassword) {
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword)); // Verify that the password comparison is true
    }
}