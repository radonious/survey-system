package edu.forms.dto;

import edu.forms.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponseDTO {

    private Integer id;
    private Integer formId;
    private String text;
    private QuestionType type;
    private String imageUrl;
    private List<OptionResponseDTO> options;
}
