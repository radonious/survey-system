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
public class CompletionResponseDTO {

    private Integer id;
    private Integer userId;
    private Integer formId;
    private List<AnswerResponseDTO> answers;
}
