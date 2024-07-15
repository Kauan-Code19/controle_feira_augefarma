package com.augefarma.controle_feira.controllers.authentication;

import com.augefarma.controle_feira.dtos.authentication.LoginDto;
import com.augefarma.controle_feira.dtos.authentication.LoginResponseDto;
import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import com.augefarma.controle_feira.services.authentication.AuthenticationService;
import com.augefarma.controle_feira.services.authorization.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Autowired
    public LoginController(AuthenticationService authenticationService, AuthenticationManager authenticationManager,
                           TokenService tokenService) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    /**
     * Handles the login request for administrators.
     *
     * @param loginDto the login data transfer object containing email and password
     * @return a ResponseEntity containing the LoginResponseDto with the generated token
     */
    @PostMapping
    public ResponseEntity<LoginResponseDto> loginAdministrator(@Valid @RequestBody LoginDto loginDto) {

        // Authenticate the administrator using the provided email and password
        var authentication = authenticationService.authenticate(loginDto.email(), loginDto.password(),
                authenticationManager);

        // Generate a token for the authenticated administrator
        var token = tokenService.generateTokenAdministrator((AdministratorEntity) authentication.getPrincipal());

        // Return the generated token in the response
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
