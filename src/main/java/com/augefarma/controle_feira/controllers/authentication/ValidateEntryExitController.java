package com.augefarma.controle_feira.controllers.authentication;

import com.augefarma.controle_feira.dtos.authentication.CpfEntityDto;
import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.entities.entry_exit.EventSegment;
import com.augefarma.controle_feira.services.authentication.ValidateEntryExitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/validate")
public class ValidateEntryExitController {

    private final ValidateEntryExitService validateEntryExitService;

    @Autowired
    public ValidateEntryExitController(ValidateEntryExitService validateEntryExitService) {
        this.validateEntryExitService = validateEntryExitService;
    }

    /**
     * Endpoint to validate entry based on CPF.
     *
     * @param cpfEntityDto the DTO containing CPF information
     * @return ResponseEntity with validation message
     */
    @PostMapping("/entry")
    public ResponseEntity<ValidateEntryExitResponseDto> validateEntry(@RequestBody CpfEntityDto cpfEntityDto,
                                                                      @RequestParam EventSegment eventSegment) {
        ValidateEntryExitResponseDto validateEntryExitResponseDto = validateEntryExitService
                .validateEntry(cpfEntityDto.cpf(), eventSegment);

        return ResponseEntity.ok(validateEntryExitResponseDto);
    }

    /**
     * Endpoint to validate exit based on CPF.
     *
     * @param cpfEntityDto the DTO containing CPF information
     * @return ResponseEntity with validation message
     */
    @PostMapping("/exit")
    public ResponseEntity<ValidateEntryExitResponseDto> validateExit(@RequestBody CpfEntityDto cpfEntityDto) {
        ValidateEntryExitResponseDto validateEntryExitResponseDto = validateEntryExitService.validateExit(cpfEntityDto.cpf());
        return ResponseEntity.ok(validateEntryExitResponseDto);
    }
}
