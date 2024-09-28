package com.augefarma.controle_feira.unit.services.authentication;

import com.augefarma.controle_feira.dtos.authentication.LoginDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.exceptions.InvalidCredentialsException;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.authentication.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@SpringBootTest
public class AuthenticationServiceTest {

    @MockBean
    private AdministratorRepository administratorRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testLoadUserByUsername_ValidEmail() {
        String email = "admin@test.com";
        AdministratorEntity administratorEntity = new AdministratorEntity();
        administratorEntity.setEmail(email);

        when(administratorRepository.findByEmail(email)).thenReturn(administratorEntity);

        UserDetails userDetails = authenticationService.loadUserByUsername(email);
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    public void testAuthenticate_ValidCredentials() {
        LoginDto loginDto = createLoginDto("admin@test.com", "Password@123");
        String encryptedPassword = passwordEncoder.encode(loginDto.password());

        Authentication authRequest = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
        Authentication authResult = new UsernamePasswordAuthenticationToken(loginDto.email(), encryptedPassword);

        when(authenticationManager.authenticate(authRequest)).thenReturn(authResult);

        Authentication result = authenticationService
                .authenticate(loginDto.email(), loginDto.password(), authenticationManager);

        assertAuthenticationValidResponse(result, loginDto);
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        LoginDto loginDto = createLoginDto("admin@test.com", "wrongPassword");

        Authentication authRequest = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());

        when(authenticationManager.authenticate(authRequest))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.authenticate(loginDto.email(), loginDto.password(), authenticationManager);
        });
    }

    private LoginDto createLoginDto(String email, String password) {
        return new LoginDto(email, password);
    }

    private void assertAuthenticationValidResponse(Authentication result, LoginDto loginDto) {
        assertNotNull(result);
        assertEquals(loginDto.email(), result.getName());

        verifyPasswordEncoding(loginDto.password(), (String) result.getCredentials());
    }

    private void verifyPasswordEncoding(String rawPassword, String encodedPassword) {
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }
}
