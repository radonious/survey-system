package edu.eltex.forms.dto;

import edu.eltex.forms.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {

    private String accessToken;
    private String refreshToken;
    private UserRole role;
    private Long userId;
    private String username;
}
