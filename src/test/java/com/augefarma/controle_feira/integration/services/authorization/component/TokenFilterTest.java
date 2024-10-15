package com.augefarma.controle_feira.integration.services.authorization.component;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import com.augefarma.controle_feira.services.authorization.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class TokenFilterTest {

    private String validToken;
    private ObjectMapper objectMapper;

    @Autowired
    private TokenService tokenService;

    @Value("${administrator.password}")
    private String administratorPassword;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        // Clear the security context before each test
        SecurityContextHolder.clearContext();

        // Initialize ObjectMapper for JSON handling
        this.objectMapper = new ObjectMapper();

        // Generate a valid token for the administrator and store it for use in tests
        this.validToken = tokenService.generateTokenAdministrator(
                createAndSaveAdministrator("Kauan Pereira", "pkauprofissional@gmail.com",
                        administratorPassword));
    }

    @Test
    public void testValidTokenAuthentication() throws Exception {
        // Create a LaboratoryDto object
        LaboratoryDto laboratoryDto = new LaboratoryDto("VR Lima");

        // Convert the LaboratoryDto object to JSON
        String laboratoryDtoJson = objectMapper.writeValueAsString(laboratoryDto);

        // Perform a POST request with a valid token and expect the response status to be "Created"
        mockMvc.perform(MockMvcRequestBuilders.post("/laboratory")
                        .content(laboratoryDtoJson)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testWithoutToken() throws Exception {
        // Create a LaboratoryDto object
        LaboratoryDto laboratoryDto = new LaboratoryDto("VR Lima");

        // Convert the LaboratoryDto object to JSON
        String laboratoryDtoJson = objectMapper.writeValueAsString(laboratoryDto);

        // Perform a POST request without a token and expect the response status to be "Unauthorized"
        mockMvc.perform(MockMvcRequestBuilders.post("/laboratory")
                        .content(laboratoryDtoJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
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