package edu.eltex.forms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletionRequestDTO {

    @NotNull(message = "User id is mandatory")
    @Positive(message = "User id must be positive")
    private Integer userId;

    @NotNull(message = "Form id is mandatory")
    @Positive(message = "Form id must be positive")
    private Integer formId;

    @Valid
    @NotNull(message = "Answers is mandatory")
    List<AnswerRequestDTO> answers;
}
