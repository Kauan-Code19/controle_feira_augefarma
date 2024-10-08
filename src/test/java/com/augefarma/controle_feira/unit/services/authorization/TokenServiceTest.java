package com.augefarma.controle_feira.unit.services.authorization;

import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.exceptions.JWTGenerationException;
import com.augefarma.controle_feira.exceptions.JWTValidException;
import com.augefarma.controle_feira.services.authorization.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setup() {
        // Initialize TokenService and set the secret key for testing
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "testSecretKey");
    }

    @Test
    void testGenerateTokenAdministrator_Success() {
        // Create a test administrator with an email
        AdministratorEntity administrator = new AdministratorEntity();
        administrator.setEmail("admin@teste.com");

        // Generate a token for the administrator
        String token = tokenService.generateTokenAdministrator(administrator);

        // Assert that the token is not null
        assertNotNull(token);
    }

    @Test
    void testValidateToken_Success() {
        // Create a test administrator with an email
        AdministratorEntity administrator = new AdministratorEntity();
        administrator.setEmail("admin@teste.com");

        // Generate a token for the administrator
        String token = tokenService.generateTokenAdministrator(administrator);

        // Validate the token and retrieve the email
        String email = tokenService.validateToken(token);

        // Assert that the returned email matches the administrator's email
        assertEquals(administrator.getEmail(), email);
    }

    @Test
    void testGenerateTokenAdministrator_NullSecret() {
        // Create a test administrator with a valid email
        AdministratorEntity administrator = new AdministratorEntity();
        administrator.setEmail("admin@example.com");

        // Set the secret key to null
        ReflectionTestUtils.setField(tokenService, "secret", null);

        // Assert that JWTGenerationException is thrown when generating the token
        assertThrows(JWTGenerationException.class, () -> {
            tokenService.generateTokenAdministrator(administrator);
        });
    }

    @Test
    void testValidateToken_Failure() {
        // Define an invalid token for validation
        String invalidToken = "invalidToken";

        // Assert that JWTValidException is thrown when validating the token
        assertThrows(JWTValidException.class, () -> {
            tokenService.validateToken(invalidToken);
        });
    }
}