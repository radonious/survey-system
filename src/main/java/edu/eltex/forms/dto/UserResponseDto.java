package edu.eltex.forms.dto;

import edu.eltex.forms.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {

    private Integer id;
    private String username;
    private UserRole role;
}
