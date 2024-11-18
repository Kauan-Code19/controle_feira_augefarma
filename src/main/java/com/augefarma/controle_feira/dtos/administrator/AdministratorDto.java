package com.augefarma.controle_feira.dtos.administrator;

import com.augefarma.controle_feira.entities.administrator.AdministratorRole;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AdministratorDto {

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must have a maximum of 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, " +
                    "one special character, and be at least 8 characters long")
    private String password;

    @NotNull
    private AdministratorRole role;
}
