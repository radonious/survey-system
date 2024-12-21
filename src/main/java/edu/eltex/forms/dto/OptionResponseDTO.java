package edu.eltex.forms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionResponseDTO {

    private Integer id;
    private Integer questionId;
    private String text;
}
