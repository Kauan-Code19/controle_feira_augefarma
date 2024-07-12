package com.augefarma.controle_feira.services.client;

import com.augefarma.controle_feira.dtos.client.ClientDto;
import com.augefarma.controle_feira.dtos.client.ClientResponseDto;
import com.augefarma.controle_feira.entities.client.ClientEntity;
import com.augefarma.controle_feira.repositories.client.ClientRepository;
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
}
