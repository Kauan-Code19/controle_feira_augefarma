package com.augefarma.controle_feira.dtos.laboratory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LaboratoryMemberDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF must be in the format XXX.XXX.XXX-XX")
    private String cpf;

    @NotBlank(message = "Laboratory is required")
    @Size(min = 2, max = 100, message = "Laboratory must be between 2 and 100 characters")
    private String laboratoryCorporateReason;
}
