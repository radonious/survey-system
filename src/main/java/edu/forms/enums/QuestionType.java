package edu.forms.enums;

import edu.forms.dto.QuestionRequestDTO;

/**
 * Enum для выбора типа создаваемого вопроса.
 * @see QuestionRequestDTO
 */
public enum QuestionType {
    NUMERIC,
    SINGLE_CHOICE,
    MULTIPLE_CHOICE,
    RATING,
    TEXT
}
