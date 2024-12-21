package edu.eltex.forms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormRequestDTO {

    @NotNull(message = "Creator id is mandatory")
    private Integer creatorId;

    @NotNull(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Description is mandatory")
    private String description;

    @Valid
    @NotNull(message = "Questions is mandatory")
    @Size(min = 1, message = "Questions count should be 1 or more")
    private List<QuestionRequestDTO> questions;
}
