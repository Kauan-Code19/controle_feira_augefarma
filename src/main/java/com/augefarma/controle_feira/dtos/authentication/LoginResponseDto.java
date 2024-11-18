package com.augefarma.controle_feira.dtos.authentication;

import com.augefarma.controle_feira.entities.administrator.AdministratorRole;

public record LoginResponseDto(String token, AdministratorRole role) {
}
