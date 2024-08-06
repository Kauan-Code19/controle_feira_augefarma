package com.augefarma.controle_feira.controllers.laboratory;

import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberDto;
import com.augefarma.controle_feira.dtos.laboratory.LaboratoryMemberResponseDto;
import com.augefarma.controle_feira.services.laboratory.LaboratoryMemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/laboratory-member")
public class LaboratoryMemberController {

    private final LaboratoryMemberService laboratoryMemberService;

    @Autowired
    public LaboratoryMemberController(LaboratoryMemberService laboratoryMemberService) {
        this.laboratoryMemberService = laboratoryMemberService;
    }

    /**
     * Endpoint to register a new laboratory member.
     *
     * @param laboratoryMemberDto the DTO containing the laboratory member's information
     * @return a ResponseEntity with status 201 (Created) and the body containing the laboratory member response DTO
     */
    @PostMapping
    public ResponseEntity<LaboratoryMemberResponseDto> registerLaboratoryMember(
            @Valid @RequestBody LaboratoryMemberDto laboratoryMemberDto) {

        // Register the laboratory member and obtain the response DTO
        LaboratoryMemberResponseDto laboratoryMemberResponseDto = laboratoryMemberService
                .registerLaboratoryMember(laboratoryMemberDto);

        // Build the URI of the newly created laboratory member resource
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(laboratoryMemberResponseDto.getId())
                .toUri();

        // Return a ResponseEntity with status 201 (Created) and the response body
        return ResponseEntity.created(uri).body(laboratoryMemberResponseDto);
    }

    /**
     * Endpoint to retrieve a laboratory member by its ID.
     *
     * @param laboratoryMemberId the ID of the laboratory member to retrieve
     * @return a ResponseEntity with status 200 (OK) and the body containing the laboratory member response DTO
     */
    @GetMapping("/{laboratoryMemberId}")
    public ResponseEntity<LaboratoryMemberResponseDto> getLaboratoryMemberById(
            @PathVariable Long laboratoryMemberId) {

        // Retrieve the laboratory member by ID and obtain the response DTO
        LaboratoryMemberResponseDto laboratoryMemberResponseDto = laboratoryMemberService
                .getLaboratoryMemberById(laboratoryMemberId);

        // Return a ResponseEntity with status 200 (OK) and the response body
        return ResponseEntity.ok(laboratoryMemberResponseDto);
    }

    /**
     * Endpoint to generate a badge for a laboratory member.
     *
     * @param laboratoryMemberId the ID of the laboratory member for whom to generate the badge
     * @return a ResponseEntity with status 200 (OK), headers, and the badge PDF as a byte array
     */
    @GetMapping("/{laboratoryMemberId}/badge")
    public ResponseEntity<byte[]> generateLaboratoryMemberBadge(
            @PathVariable Long laboratoryMemberId) {

        // Generate the badge for the laboratory member
        byte[] badge = laboratoryMemberService.generateLaboratoryMemberBadge(laboratoryMemberId);

        // Create HTTP headers for content type and attachment disposition
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set("Content-Disposition", "attachment; filename=laboratory_" + laboratoryMemberId + "_badge.pdf");

        // Return a ResponseEntity with status 200 (OK), headers, and the badge byte array
        return ResponseEntity.ok().headers(headers).body(badge);
    }
}
