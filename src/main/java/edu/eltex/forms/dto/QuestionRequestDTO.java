package edu.eltex.forms.dto;

import edu.eltex.forms.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequestDTO {

    @NotEmpty(message = "Text is mandatory")
    private String text;

    @NotNull(message = "Type is mandatory. Types: NUMERIC, SINGLE_CHOICE, MULTIPLE_CHOICE, TEXT")
    private QuestionType type;

    private MultipartFile image;

    @Valid
    private List<OptionRequestDTO> options;

    @AssertTrue(message = "Must have no options for non-optional types. Must have at least 1 option for optional types.")
    private boolean isValid() {
        if ((type.equals(QuestionType.SINGLE_CHOICE) || type.equals(QuestionType.MULTIPLE_CHOICE) || type.equals(QuestionType.RATING)) && options != null && !options.isEmpty()) {
            return true;
        } else
            return !type.equals(QuestionType.RATING) && !type.equals(QuestionType.SINGLE_CHOICE) && !type.equals(QuestionType.MULTIPLE_CHOICE) && (options == null || options.isEmpty());
    }
}
