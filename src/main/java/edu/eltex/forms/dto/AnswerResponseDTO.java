package edu.eltex.forms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponseDTO {

    private Integer id;
    private Integer completionId;
    private Integer questionId;
    private String answerText;
    private List<OptionResponseDTO> selectedOptions;
}
