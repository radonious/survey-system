package edu.eltex.forms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequestDto {

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Username is mandatory")
    private String password;
}
