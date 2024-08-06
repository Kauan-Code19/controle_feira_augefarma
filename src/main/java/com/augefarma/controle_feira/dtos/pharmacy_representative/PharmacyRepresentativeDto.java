package com.augefarma.controle_feira.dtos.pharmacy_representative;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PharmacyRepresentativeDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$", message = "CPF must be in the format XXX.XXX.XXX-XX")
    private String cpf;

    @NotBlank(message = "CNPJ is required")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
            message = "CNPJ must be in the format XX.XXX.XXX/XXXX-XX")
    private String cnpj;

    @NotBlank(message = "Corporate reason is required")
    @Size(min = 2, max = 100, message = "Corporate reason must be between 2 and 100 characters")
    private String corporateReason;
}
