package com.augefarma.controle_feira.dtos.administrator;

import com.augefarma.controle_feira.entities.administrator.AdministratorEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AdministratorResponseDto {

    private Long id;

    private String fullName;

    private String email;

    public AdministratorResponseDto(AdministratorEntity administrator) {
        id = administrator.getId();
        fullName = administrator.getFullName();
        email = administrator.getEmail();
    }
}
