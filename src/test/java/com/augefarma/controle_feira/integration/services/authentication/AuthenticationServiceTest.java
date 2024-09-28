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
        createAndSaveAdministrator("Admin Test", "admin@test.com", "Password@123");

        LoginDto loginDto = createLoginDto("admin@test.com", "Password@123");

        Authentication authResult = authenticationService
                .authenticate(loginDto.email(), loginDto.password(), authenticationManager);

        assertAuthenticationValidResponse(authResult, loginDto);
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        LoginDto loginDto = createLoginDto("admin@test.com", "wrongPassword");

        assertThrows(InvalidCredentialsException.class, () -> authenticationService
                .authenticate(loginDto.email(), loginDto.password(), authenticationManager));
    }

    @Test
    public void testLoadUserByUsername_ValidEmail() {
        createAndSaveAdministrator("Admin Test", "admin@test.com", "Password@123");

        LoginDto loginDto = createLoginDto("admin@test.com", "Password@123");

        UserDetails userDetails = authenticationService.loadUserByUsername(loginDto.email());
        assertNotNull(userDetails);
        assertEquals(loginDto.email(), userDetails.getUsername());
    }

    private void createAndSaveAdministrator(String fullName, String email, String rawPassword) {
        AdministratorEntity administrator = new AdministratorEntity();

        administrator.setFullName(fullName);
        administrator.setEmail(email);
        administrator.setPassword(passwordEncoder.encode(rawPassword));

        administratorRepository.save(administrator);
    }

    private LoginDto createLoginDto(String email, String password) {
        return new LoginDto(email, password);
    }

    private void assertAuthenticationValidResponse(Authentication result, LoginDto loginDto) {
        assertNotNull(result);
        assertEquals(loginDto.email(), result.getName());

        UserDetails userDetails = (UserDetails) result.getPrincipal();

        verifyPasswordEncoding(loginDto.password(), userDetails.getPassword());
    }

    private void verifyPasswordEncoding(String rawPassword, String encodedPassword) {
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
