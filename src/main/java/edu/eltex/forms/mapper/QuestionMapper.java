package edu.eltex.forms.mapper;

import edu.eltex.forms.FileStorageUtils;
import edu.eltex.forms.dto.QuestionRequestDTO;
import edu.eltex.forms.dto.QuestionResponseDTO;
import edu.eltex.forms.entities.Question;
import edu.eltex.forms.exception.InvalidFileTypeException;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.mapstruct.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = OptionMapper.class, builder = @Builder(disableBuilder = true))
public interface QuestionMapper {

    /**
     * Преобразовывает Request в Entity
     * @param questionRequestDTO что нужно преобразовать
     * @return {@link edu.eltex.forms.entities.Question} результат преобразования
     */
    @Mappings({
            @Mapping(ignore = true, target = "id"),
            @Mapping(ignore = true, target = "form"),
            @Mapping(ignore = true, target = "imageUrl"),
            @Mapping(source = "options", target = "options"),
    })
    Question toEntity(QuestionRequestDTO questionRequestDTO);

    /**
     * Преобразовывает Entity в Response
     * @param questionEntity что нужно преобразовать
     * @return {@link edu.eltex.forms.dto.QuestionResponseDTO} результат преобразования
     */
    @Mappings({
            @Mapping(source = "form.id", target = "formId"),
            @Mapping(source = "options", target = "options"),
    })
    QuestionResponseDTO toDto(Question questionEntity);


    /**
     * Обработка сохранения картинки при создании вопроса
     * @param question созданный entity
     * @param questionRequestDTO запрос
     */
    @AfterMapping
    default void handleImage(@MappingTarget Question question, QuestionRequestDTO questionRequestDTO) {
        MultipartFile image = questionRequestDTO.getImage();
        if (image != null && !image.isEmpty()) {
            if (!isValidImageMimeType(image)) {
                throw new InvalidFileTypeException("Invalid image type. Only JPEG, PNG, and GIF are allowed.");
            }

            try {
                String basePath = FileStorageUtils.getUploadDir();

                String fileName = UUID.randomUUID() + "." + FilenameUtils.getExtension(image.getOriginalFilename());
                Path filePath = Paths.get(basePath, fileName);

                Files.createDirectories(filePath.getParent());

                image.transferTo(filePath.toFile());

                question.setImageUrl("/images/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save file", e);
            }
        }
    }

    private boolean isValidImageMimeType(MultipartFile file) {
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            return mimeType.matches("image/(jpeg|jpg|png|gif)");
        } catch (IOException e) {
            throw new RuntimeException("Failed to detect file type", e);
        }
    }
}
