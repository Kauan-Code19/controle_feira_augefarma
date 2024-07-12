package com.augefarma.controle_feira.services.administrator;

import com.augefarma.controle_feira.dtos.administrator.AdministratorDto;
import com.augefarma.controle_feira.dtos.administrator.AdministratorResponseDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.repositories.administrator.AdministratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    @Autowired
    public AdministratorService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    /**
     * Registers a new administrator.
     *
     * @param administratorDto the DTO containing the administrator's information
     * @return the response DTO containing the registered administrator's details
     */
    @Transactional
    public AdministratorResponseDto registerAdministrator(AdministratorDto administratorDto) {
        AdministratorEntity administrator = new AdministratorEntity(); // Create a new AdministratorEntity object

        // Set the full name and email from the DTO
        administrator.setFullName(administratorDto.getName());
        administrator.setEmail(administratorDto.getEmail());

        // Encrypt the password using Argon2 and set it in the entity
        String encryptedPassword = new Argon2PasswordEncoder(16, 32, 2, 65536, 2)
                .encode(administratorDto.getPassword());

        administrator.setPassword(encryptedPassword);

        administratorRepository.save(administrator); // Save the administrator entity to the repository

        // Return a response DTO containing the registered administrator's details
        return new AdministratorResponseDto(administrator);
    }
}
