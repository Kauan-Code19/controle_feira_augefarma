package com.augefarma.controle_feira.controllers.authentication;

import com.augefarma.controle_feira.dtos.authentication.CpfEntityDto;
import com.augefarma.controle_feira.dtos.authentication.validate_entry_exit.ValidateEntryExitResponseDto;
import com.augefarma.controle_feira.enums.EventSegment;
import com.augefarma.controle_feira.services.authentication.entry_exit.ValidateEntryService;
import com.augefarma.controle_feira.services.authentication.entry_exit.ValidateExitService;
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

    private final ValidateEntryService validateEntryService;
    private final ValidateExitService validateExitService;

    @Autowired
    public ValidateEntryExitController(ValidateEntryService validateEntryService,
                                       ValidateExitService validateExitService) {
        this.validateEntryService = validateEntryService;
        this.validateExitService = validateExitService;
    }


    @PostMapping("/entry")
    public ResponseEntity<ValidateEntryExitResponseDto> validateEntry(@RequestBody CpfEntityDto cpfEntityDto,
                                                                      @RequestParam EventSegment eventSegment) {
        ValidateEntryExitResponseDto validateEntryExitResponseDto = validateEntryService
                .validateEntryFair(cpfEntityDto.cpf(), eventSegment);

        return ResponseEntity.ok(validateEntryExitResponseDto);
    }


    @PostMapping("/exit")
    public ResponseEntity<ValidateEntryExitResponseDto> validateExit(@RequestBody CpfEntityDto cpfEntityDto,
                                                                     @RequestParam EventSegment eventSegment) {
        ValidateEntryExitResponseDto validateEntryExitResponseDto = validateExitService
                .validateExitBuffet(cpfEntityDto.cpf(), eventSegment);

        return ResponseEntity.ok(validateEntryExitResponseDto);
    }
}
