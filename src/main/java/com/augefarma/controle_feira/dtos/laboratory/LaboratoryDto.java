package com.augefarma.controle_feira.dtos.laboratory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LaboratoryDto {

    @NotBlank(message = "Corporate reason is required")
    @Size(min = 2, max = 100, message = "Corporate reason must be between 2 and 100 characters")
    private String corporateReason;
}
