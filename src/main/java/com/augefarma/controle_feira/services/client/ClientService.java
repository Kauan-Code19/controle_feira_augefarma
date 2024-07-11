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

    @Transactional
    public ClientResponseDto registerClient(ClientDto clientDto) {
        ClientEntity client = new ClientEntity();

        client.setFullName(clientDto.getFullName());
        client.setCpf(clientDto.getCpf());
        client.setCnpj(clientDto.getCnpj());
        client.setCorporateReason(clientDto.getCorporateReason());

        clientRepository.save(client);

        return new ClientResponseDto(client);
    }
}
