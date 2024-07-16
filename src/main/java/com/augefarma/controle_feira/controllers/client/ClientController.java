package com.augefarma.controle_feira.controllers.client;

import com.augefarma.controle_feira.dtos.client.ClientDto;
import com.augefarma.controle_feira.dtos.client.ClientResponseDto;
import com.augefarma.controle_feira.services.client.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * Endpoint to retrieve a client by their ID.
     *
     * @param clientId the ID of the client to retrieve
     * @return a ResponseEntity containing the client response DTO
     */
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long clientId) {
        // Retrieve client details from the service using the provided client ID
        ClientResponseDto clientResponseDto = clientService.getClientById(clientId);

        // Return a ResponseEntity with status 200 (OK) and the client response DTO
        return ResponseEntity.ok(clientResponseDto);
    }

    @GetMapping("/{clientId}/badge")
    public ResponseEntity<byte[]> generateClientBadge(@PathVariable Long clientId) {
        byte[] badge = clientService.generateClientBadge(clientId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set("Content-Disposition", "attachment; filename=" + "client" + clientId + "_badge.pdf");

        return ResponseEntity.ok().headers(headers).body(badge);
    }
}
