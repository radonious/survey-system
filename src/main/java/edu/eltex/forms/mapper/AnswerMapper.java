package edu.eltex.forms.mapper;

import edu.eltex.forms.dto.AnswerRequestDTO;
import edu.eltex.forms.dto.AnswerResponseDTO;
import edu.eltex.forms.entities.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {QuestionMapper.class, OptionMapper.class})
public interface AnswerMapper {

    /**
     * Преобразовывает Request в Entity
     * @param answerRequestDTO что нужно преобразовать
     * @return {@link edu.eltex.forms.entities.Answer} результат преобразования
     */
    @Mappings({
            @Mapping(ignore = true, target = "id"),
            @Mapping(ignore = true, target = "completion"),
            @Mapping(source = "questionId", target = "question.id"),
            @Mapping(source = "selectedOptions", target = "selectedOptions"),
    })
    Answer toEntity(AnswerRequestDTO answerRequestDTO);

    /**
     * Преобразовывает Entity в Response
     * @param answerEntity что нужно преобразовать
     * @return {@link edu.eltex.forms.dto.AnswerResponseDTO} результат преобразования
     */
    @Mappings({
            @Mapping(source = "completion.id", target = "completionId"),
            @Mapping(source = "question.id", target = "questionId"),
            @Mapping(source = "selectedOptions", target = "selectedOptions"),
    })
    AnswerResponseDTO toDto(Answer answerEntity);
}
