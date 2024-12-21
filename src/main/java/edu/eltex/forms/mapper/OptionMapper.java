package edu.eltex.forms.mapper;

import edu.eltex.forms.dto.OptionRequestDTO;
import edu.eltex.forms.dto.OptionResponseDTO;
import edu.eltex.forms.entities.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    /**
     * Преобразовывает Request в Entity
     * @param optionRequestDTO что нужно преобразовать
     * @return {@link edu.eltex.forms.entities.Option} результат преобразования
     */
    @Mappings({
            @Mapping(ignore = true, target = "id"),
            @Mapping(ignore = true, target = "question")
    })
    Option toEntity(OptionRequestDTO optionRequestDTO);

    /**
     * Преобразовывает Entity в Response
     * @param optionEntity что нужно преобразовать
     * @return {@link edu.eltex.forms.dto.OptionResponseDTO} результат преобразования
     */
    @Mapping(source = "question.id", target = "questionId")
    OptionResponseDTO toDto(Option optionEntity);
}
