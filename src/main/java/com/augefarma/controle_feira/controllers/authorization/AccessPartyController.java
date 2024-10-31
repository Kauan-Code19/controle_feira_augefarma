package com.augefarma.controle_feira.controllers.authorization;

import com.augefarma.controle_feira.dtos.authentication.CpfEntityDto;
import com.augefarma.controle_feira.dtos.authorization.WristbandsResponseDto;
import com.augefarma.controle_feira.services.authorization.WristbandsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/access-party")
public class AccessPartyController {

    private final WristbandsService wristbandsService;

    @Autowired
    public AccessPartyController(WristbandsService wristbandsService) {
        this.wristbandsService = wristbandsService;
    }

    /**
     * Checks the delivery status of a wristband based on the provided CPF.
     *
     * @param cpfEntityDto the DTO containing the CPF to check
     * @return a ResponseEntity containing the WristbandsResponseDto with the delivery status
     */
    @PostMapping
    public ResponseEntity<WristbandsResponseDto> checkDeliveryOfWristband(@RequestBody CpfEntityDto cpfEntityDto) {
        // Call the service method to check the wristband delivery based on CPF
        WristbandsResponseDto wristbandsResponseDto = wristbandsService.checkDeliveryOfWristband(cpfEntityDto.cpf());

        // Return the response with an OK status and the wristband delivery status
        return ResponseEntity.ok(wristbandsResponseDto);
    }
}
