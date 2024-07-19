package com.augefarma.controle_feira.controllers.authentication;

import com.augefarma.controle_feira.dtos.authentication.CpfEntityDto;
import com.augefarma.controle_feira.services.authentication.ValidateEntryExitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> validateEntry(@RequestBody CpfEntityDto cpfEntityDto) {
        String message = validateEntryExitService.validateEntry(cpfEntityDto.cpf());
        return ResponseEntity.ok(message);
    }

    /**
     * Endpoint to validate exit based on CPF.
     *
     * @param cpfEntityDto the DTO containing CPF information
     * @return ResponseEntity with validation message
     */
    @PostMapping("/exit")
    public ResponseEntity<String> validateExit(@RequestBody CpfEntityDto cpfEntityDto) {
        String message = validateEntryExitService.validateExit(cpfEntityDto.cpf());
        return ResponseEntity.ok(message);
    }
}
