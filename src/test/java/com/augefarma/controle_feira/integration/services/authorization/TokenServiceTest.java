package com.augefarma.controle_feira.integration.services.authorization;

import com.augefarma.controle_feira.dtos.authentication.LoginDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.exceptions.JWTGenerationException;
import com.augefarma.controle_feira.exceptions.JWTValidException;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.authorization.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class TokenServiceTest {

    private AdministratorEntity administrator;
    private ObjectMapper objectMapper;

    @Value("${administrator.password}")
    private String administratorPassword;

    @Value("${api.security.token.secret}")
    private String secret;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private TokenService tokenService;

    @BeforeEach
    void setup() {
        // Clear the security context and repository before each test
        SecurityContextHolder.clearContext();
        administratorRepository.deleteAll();

        // Initialize ObjectMapper for JSON handling
        this.objectMapper = new ObjectMapper();

        // Create and save an administrator to be used in the tests
        this.administrator = createAndSaveAdministrator("Kauan Pereira", "pkauprofissional@gmail.com",
                administratorPassword);
    }

    @AfterEach
    void restoreSecret() {
        // Restore the original secret after the tests, in case it was modified
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    void testGenerateTokenAdministrator_Success() throws Exception {
        // Create a LoginDto with the administrator's credentials
        LoginDto loginDto = new LoginDto(this.administrator.getEmail(), this.administratorPassword);

        // Convert the LoginDto object to JSON
        String loginDtoJson = objectMapper.writeValueAsString(loginDto);

        // Perform a POST request to the /login endpoint and expect the token to be generated
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .content(loginDtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void testValidateToken_Success() throws Exception {
        // Create a LaboratoryDto object
        LaboratoryDto laboratoryDto = new LaboratoryDto("VR Lima");

        // Convert the LaboratoryDto object to JSON
        String laboratoryDtoJson = objectMapper.writeValueAsString(laboratoryDto);

        // Generate a token for the administrator
        String token = tokenService.generateTokenAdministrator(this.administrator);

        // Perform a POST request with the generated token and expect the response status to be "Created"
        mockMvc.perform(MockMvcRequestBuilders.post("/laboratory")
                        .content(laboratoryDtoJson)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void testGenerateTokenAdministrator_NullSecret() {
        // Set the token secret to null using ReflectionTestUtils
        ReflectionTestUtils.setField(tokenService, "secret", null);

        // Assert that JWTGenerationException is thrown when generating a token with a null secret
        assertThrows(JWTGenerationException.class, () -> {
            tokenService.generateTokenAdministrator(this.administrator);
        });
    }

    @Test
    void testValidateToken_Failure() {
        // Define an invalid token for validation
        String invalidToken = "invalidToken";

        // Assert that JWTValidException is thrown when validating the invalid token
        assertThrows(JWTValidException.class, () -> {
            tokenService.validateToken(invalidToken);
        });
    }

    // Helper method to create and save an administrator entity in the database
    private AdministratorEntity createAndSaveAdministrator(String fullName, String email, String rawPassword) {
        AdministratorEntity administrator = new AdministratorEntity();

        // Set administrator details
        administrator.setFullName(fullName);
        administrator.setEmail(email);

        // Encode the password before saving
        administrator.setPassword(passwordEncoder.encode(rawPassword));

        // Save the administrator entity in the repository
        administratorRepository.save(administrator);
        return administrator;
    }
}
