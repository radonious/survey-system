package edu.eltex.forms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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
public class AnswerRequestDTO {

    @NotNull(message = "Question id is mandatory")
    @Positive(message = "Question id must be positive")
    private Integer questionId;

    private String answerText;

    @Valid
    private List<OptionRequestDTO> selectedOptions;

    @AssertTrue(message = "Must have only text OR options")
    private boolean isValid() {
        return answerText == null ^ selectedOptions == null;
    }
}