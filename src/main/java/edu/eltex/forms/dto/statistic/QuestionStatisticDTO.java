package edu.eltex.forms.dto.statistic;

import edu.eltex.forms.enums.QuestionType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionStatisticDTO {
    private QuestionType questionType;
    private String questionText;
    private Object statistic;
}