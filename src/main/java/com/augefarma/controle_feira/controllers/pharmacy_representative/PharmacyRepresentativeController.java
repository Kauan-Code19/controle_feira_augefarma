package com.augefarma.controle_feira.controllers.pharmacy_representative;

import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeDto;
import com.augefarma.controle_feira.dtos.pharmacy_representative.PharmacyRepresentativeResponseDto;
import com.augefarma.controle_feira.services.pharmacy_representative.PharmacyRepresentativeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pharmacy-representative")
public class PharmacyRepresentativeController {

    private final PharmacyRepresentativeService pharmacyRepresentativeService;

    @Autowired
    public PharmacyRepresentativeController(PharmacyRepresentativeService pharmacyRepresentativeService) {
        this.pharmacyRepresentativeService = pharmacyRepresentativeService;
    }

    /**
     * Endpoint to register a new pharmacy representative.
     *
     * @param pharmacyRepresentativeDto the DTO containing the pharmacy representative's information
     * @return a ResponseEntity with status 201 (Created) and the body containing the pharmacy representative response DTO
     */
    @PostMapping
    public ResponseEntity<PharmacyRepresentativeResponseDto> registerPharmacyRepresentative(
            @Valid @RequestBody PharmacyRepresentativeDto pharmacyRepresentativeDto) {

        // Register the pharmacy representative and obtain the response DTO
        PharmacyRepresentativeResponseDto pharmacyRepresentativeResponseDto = pharmacyRepresentativeService
                .registerPharmacyRepresentative(pharmacyRepresentativeDto);

        // Build the URI of the newly created pharmacy representative resource
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pharmacyRepresentativeResponseDto.getId())
                .toUri();

        // Return a ResponseEntity with status 201 (Created) and the response body
        return ResponseEntity.created(uri).body(pharmacyRepresentativeResponseDto);
    }

    /**
     * Endpoint to retrieve a pharmacy representative by their ID.
     *
     * @param pharmacyRepresentativeId the ID of the pharmacy representative to retrieve
     * @return a ResponseEntity with status 200 (OK) and the body containing the pharmacy representative response DTO
     */
    @GetMapping("/{pharmacyRepresentativeId}")
    public ResponseEntity<PharmacyRepresentativeResponseDto> getPharmacyRepresentativeById(
            @PathVariable Long pharmacyRepresentativeId) {

        // Retrieve pharmacy representative details by ID
        PharmacyRepresentativeResponseDto pharmacyRepresentativeResponseDto = pharmacyRepresentativeService
                .getPharmacyRepresentativeById(pharmacyRepresentativeId);

        // Return a ResponseEntity with status 200 (OK) and the response body
        return ResponseEntity.ok(pharmacyRepresentativeResponseDto);
    }

    /**
     * Endpoint to generate a badge for a pharmacy representative.
     *
     * @param pharmacyRepresentativeId the ID of the pharmacy representative for whom to generate the badge
     * @return a ResponseEntity with status 200 (OK), headers, and the badge PDF as a byte array
     */
    @GetMapping("/{pharmacyRepresentativeId}/badge")
    public ResponseEntity<byte[]> generatePharmacyRepresentativeBadge(
            @PathVariable Long pharmacyRepresentativeId) {

        // Generate the badge for the pharmacy representative
        byte[] badge = pharmacyRepresentativeService.generatePharmacyRepresentativeBadge(pharmacyRepresentativeId);

        // Create HTTP headers for content type and attachment disposition
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set("Content-Disposition", "attachment; filename=pharmacy_representative_"
                + pharmacyRepresentativeId + "_badge.pdf");

        // Return a ResponseEntity with status 200 (OK), headers, and the badge byte array
        return ResponseEntity.ok().headers(headers).body(badge);
    }

    /**
     * Endpoint to retrieve pharmacy representatives by name or CPF.
     *
     * @param nameOrCpf the name or CPF of the pharmacy representative to search for
     * @return a ResponseEntity with status 200 (OK) and the body containing a list of matching pharmacy representative
     * response DTOs
     */
    @GetMapping("/search-by-name-or-cpf")
    public ResponseEntity<List<PharmacyRepresentativeResponseDto>> getPharmacyRepresentativeByNameOrCpf(
            @RequestParam String nameOrCpf) {

        // Retrieve pharmacy representatives matching the name or CPF
        List<PharmacyRepresentativeResponseDto> pharmacyRepresentatives = pharmacyRepresentativeService
                .getPharmacyRepresentativeByNameOrCpf(nameOrCpf);

        // Return a ResponseEntity with status 200 (OK) and the response body
        return ResponseEntity.ok(pharmacyRepresentatives);
    }

    @DeleteMapping("/{pharmacyRepresentativeId}")
    public ResponseEntity<Void> deletePharmacyRepresentative(@PathVariable Long pharmacyRepresentativeId) {
        pharmacyRepresentativeService.deletePharmacyRepresentative(pharmacyRepresentativeId);

        return ResponseEntity.noContent().build();
    }
}
