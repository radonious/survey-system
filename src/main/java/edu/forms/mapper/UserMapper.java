package edu.forms.mapper;

import edu.forms.dto.UserRequestDto;
import edu.forms.dto.UserResponseDto;
import edu.forms.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Преобразовывает Response в Entity
     * @param userResponseDto что нужно преобразовать
     * @return {@link User} результат преобразования
     */
    @Mappings({
            @Mapping(ignore = true, target = "password"),
            @Mapping(ignore = true, target = "refreshToken"),
            @Mapping(source = "role", target = "role"),
    })
    User toEntity(UserResponseDto userResponseDto);

    /**
     * Преобразовывает Request в Entity
     * @param userRequestDto что нужно преобразовать
     * @return {@link User} результат преобразования
     */
    @Mappings({
            @Mapping(ignore = true, target = "id"),
            @Mapping(ignore = true, target = "refreshToken"),
            @Mapping(source = "role", target = "role"),
    })
    User toEntity(UserRequestDto userRequestDto);

    /**
     * Преобразовывает Entity в Response
     * @param user что нужно преобразовать
     * @return {@link UserResponseDto} результат преобразования
     */
    @Mapping(source = "role", target = "role")
    UserResponseDto toDto(User user);
}