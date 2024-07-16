package com.augefarma.controle_feira.controllers.administrator;

import com.augefarma.controle_feira.dtos.administrator.AdministratorDto;
import com.augefarma.controle_feira.dtos.administrator.AdministratorResponseDto;
import com.augefarma.controle_feira.services.administrator.AdministratorService;
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
@RequestMapping("/administrator")
public class AdministratorController {

    private final AdministratorService administratorService;

    @Autowired
    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    /**
     * Endpoint to register a new administrator.
     *
     * @param administratorDto the DTO containing the administrator's information
     * @return a ResponseEntity containing the administrator response DTO and the URI of the new resource
     */
    @PostMapping
    public ResponseEntity<AdministratorResponseDto> registerAdministrator(
            @Valid @RequestBody AdministratorDto administratorDto) {

        // Call the service to register the administrator and obtain the response DTO
        AdministratorResponseDto administratorResponseDto = administratorService
                .registerAdministrator(administratorDto);

        // Create the URI of the new resource created
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(administratorResponseDto.getId())
                .toUri();

        // Return a ResponseEntity with status 201 (Created) and the response body containing the administrator DTO
        return ResponseEntity.created(uri).body(administratorResponseDto);
    }
}
