package com.augefarma.controle_feira.controllers.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryResponseDto;
import com.augefarma.controle_feira.services.laboratory.LaboratoryService;
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
     * @return a ResponseEntity containing the laboratory response DTO and the URI of the new resource
     */
    @PostMapping
    public ResponseEntity<LaboratoryResponseDto> registerLaboratory(@Valid @RequestBody LaboratoryDto laboratoryDto) {
        // Call the service to register the laboratory and obtain the response DTO
        LaboratoryResponseDto laboratoryResponseDto = laboratoryService.registerLaboratory(laboratoryDto);

        // Create the URI of the new resource created
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(laboratoryResponseDto.getId())
                .toUri();

        // Return a ResponseEntity with status 201 (Created) and the response body containing the laboratory DTO
        return ResponseEntity.created(uri).body(laboratoryResponseDto);
    }

    /**
     * Endpoint to get a laboratory by its ID.
     *
     * @param laboratoryId the ID of the laboratory to retrieve
     * @return a ResponseEntity containing the laboratory response DTO
     */
    @GetMapping("/{laboratoryId}")
    public ResponseEntity<LaboratoryResponseDto> getLaboratoryById(@PathVariable Long laboratoryId) {
        // Call the service to get the laboratory by ID and obtain the response DTO
        LaboratoryResponseDto laboratoryResponseDto = laboratoryService.getLaboratoryById(laboratoryId);

        // Return a ResponseEntity with status 200 (OK) and the response body containing the laboratory DTO
        return ResponseEntity.ok(laboratoryResponseDto);
    }

    /**
     * Endpoint to generate a badge for a laboratory.
     *
     * @param laboratoryId the ID of the laboratory for which to generate the badge
     * @return a ResponseEntity containing the badge PDF as a byte array
     */
    @GetMapping("/{laboratoryId}/badge")
    public ResponseEntity<byte[]> generateLaboratoryBadge(@PathVariable Long laboratoryId) {
        // Generate the badge for the laboratory by calling the service
        byte[] badge = laboratoryService.generateLaboratoryBadge(laboratoryId);

        // Create HTTP headers to set the content type and the disposition as attachment with a filename
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set("Content-Disposition", "attachment; filename=laboratory_" + laboratoryId + "_badge.pdf");

        // Return a ResponseEntity with status 200 (OK), the headers, and the badge byte array
        return ResponseEntity.ok().headers(headers).body(badge);
    }
}
