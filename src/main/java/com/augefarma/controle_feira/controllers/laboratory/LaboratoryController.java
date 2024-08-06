package com.augefarma.controle_feira.controllers.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryResponseDto;
import com.augefarma.controle_feira.services.laboratory.LaboratoryService;
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
@RequestMapping("/laboratory")
public class LaboratoryController {

    private final LaboratoryService laboratoryService;

    @Autowired
    public LaboratoryController(LaboratoryService laboratoryService) {
        this.laboratoryService = laboratoryService;
    }

    /**
     * Endpoint to register a new laboratory.
     *
     * @param laboratoryDto the DTO containing the laboratory's information
     * @return a ResponseEntity with status 201 (Created) and the body containing the laboratory response DTO
     */
    @PostMapping
    public ResponseEntity<LaboratoryResponseDto> registerLaboratory(
            @Valid @RequestBody LaboratoryDto laboratoryDto) {

        // Register the laboratory and obtain the response DTO
        LaboratoryResponseDto laboratoryResponseDto = laboratoryService.registerLaboratory(laboratoryDto);

        // Build the URI of the newly created laboratory resource
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(laboratoryResponseDto.getId())
                .toUri();

        // Return a ResponseEntity with status 201 (Created) and the response body
        return ResponseEntity.created(uri).body(laboratoryResponseDto);
    }
}
