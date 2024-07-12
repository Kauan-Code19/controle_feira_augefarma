package com.augefarma.controle_feira.services.client;

import com.augefarma.controle_feira.dtos.client.ClientDto;
import com.augefarma.controle_feira.dtos.client.ClientResponseDto;
import com.augefarma.controle_feira.entities.client.ClientEntity;
import com.augefarma.controle_feira.exceptions.ResourceNotFoundException;
import com.augefarma.controle_feira.repositories.client.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Registers a new client by converting the ClientDto to a ClientEntity,
     * saving it to the repository, and returning a ClientResponseDto.
     *
     * @param clientDto the data transfer object containing client information
     * @return a ClientResponseDto containing the saved client information
     */
    @Transactional
    public ClientResponseDto registerClient(ClientDto clientDto) {
        ClientEntity client = new ClientEntity(); // Create a new ClientEntity

        // Set the properties of the client entity from the DTO
        client.setFullName(clientDto.getFullName());
        client.setCpf(clientDto.getCpf());
        client.setCnpj(clientDto.getCnpj());
        client.setCorporateReason(clientDto.getCorporateReason());

        clientRepository.save(client); // Save the client entity to the repository

        return new ClientResponseDto(client); // Return a response DTO containing the saved client information
    }

    /**
     * Retrieves a client by its ID and returns a DTO representation of the client.
     *
     * @param clientId the ID of the client to retrieve
     * @return a DTO representing the client
     * @throws ResourceNotFoundException if the client with the given ID is not found
     */
    @Transactional(readOnly = true)
    public ClientResponseDto getClientById(Long clientId) {

        try {
            // Attempt to retrieve the client entity from the repository using the given ID
            ClientEntity client = clientRepository.getReferenceById(clientId);

            return new ClientResponseDto(client); // If found, convert the entity to a DTO and return it
        } catch (EntityNotFoundException exception) {
            // If the client entity is not found, throw a custom exception indicating the resource is not found
            throw new ResourceNotFoundException("Resource not found");
        }
    }
}
