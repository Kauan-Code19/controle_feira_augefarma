package com.augefarma.controle_feira.controllers.client;

import com.augefarma.controle_feira.dtos.client.ClientDto;
import com.augefarma.controle_feira.dtos.client.ClientResponseDto;
import com.augefarma.controle_feira.services.client.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> registerClient(@Valid @RequestBody ClientDto clientDto) {
        ClientResponseDto clientResponseDto = clientService.registerClient(clientDto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("{/id}")
                .buildAndExpand(clientResponseDto.getId()).toUri();

        return ResponseEntity.created(uri).body(clientResponseDto);
    }
}
