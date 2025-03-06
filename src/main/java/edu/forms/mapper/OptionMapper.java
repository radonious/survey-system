package edu.forms.mapper;

import edu.forms.dto.OptionRequestDTO;
import edu.forms.dto.OptionResponseDTO;
import edu.forms.entities.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    /**
     * Преобразовывает Request в Entity
     * @param optionRequestDTO что нужно преобразовать
     * @return {@link Option} результат преобразования
     */
    @Mappings({
            @Mapping(ignore = true, target = "id"),
            @Mapping(ignore = true, target = "question")
    })
    Option toEntity(OptionRequestDTO optionRequestDTO);

    /**
     * Преобразовывает Entity в Response
     * @param optionEntity что нужно преобразовать
     * @return {@link OptionResponseDTO} результат преобразования
     */
    @Mapping(source = "question.id", target = "questionId")
    OptionResponseDTO toDto(Option optionEntity);
}
