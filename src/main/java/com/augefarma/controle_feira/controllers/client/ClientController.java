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

    /**
     * Endpoint to register a new client.
     *
     * @param clientDto the DTO containing the client's information
     * @return a ResponseEntity containing the client response DTO and the URI of the new resource
     */
    @PostMapping
    public ResponseEntity<ClientResponseDto> registerClient(@Valid @RequestBody ClientDto clientDto) {
        // Call the service to register the client and obtain the response DTO
        ClientResponseDto clientResponseDto = clientService.registerClient(clientDto);

        // Create the URI of the new resource created
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{/id}")
                .buildAndExpand(clientResponseDto.getId())
                .toUri();

        // Return a ResponseEntity with status 201 (Created) and the response body containing the client DTO
        return ResponseEntity.created(uri).body(clientResponseDto);
    }
}
