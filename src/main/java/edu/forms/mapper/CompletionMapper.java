package edu.forms.mapper;

import edu.forms.dto.CompletionRequestDTO;
import edu.forms.dto.CompletionResponseDTO;
import edu.forms.entities.Completion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {AnswerMapper.class})
public interface CompletionMapper {

    /**
     * Преобразовывает Request в Entity
     * @param completionRequestDTO что нужно преобразовать
     * @return {@link Completion} результат преобразования
     */
    @Mappings({
            @Mapping(ignore = true, target = "id"),
            @Mapping(source = "userId", target = "user.id"),
            @Mapping(source = "formId", target = "form.id"),
            @Mapping(source = "answers", target = "answers"),
    })
    Completion toEntity(CompletionRequestDTO completionRequestDTO);

    /**
     * Преобразовывает Entity в Response
     * @param completionEntity что нужно преобразовать
     * @return {@link CompletionResponseDTO} результат преобразования
     */
    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "form.id", target = "formId"),
            @Mapping(source = "answers", target = "answers"),
    })
    CompletionResponseDTO toDTO(Completion completionEntity);
}
