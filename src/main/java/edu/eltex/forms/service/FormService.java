package edu.eltex.forms.service;

import edu.eltex.forms.FileStorageUtils;
import edu.eltex.forms.dto.FormRequestDTO;
import edu.eltex.forms.dto.FormResponseDTO;
import edu.eltex.forms.entities.Form;
import edu.eltex.forms.entities.Question;
import edu.eltex.forms.mapper.FormMapper;
import edu.eltex.forms.repository.FormRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class FormService {

    private final FormRepository formRepository;
    private final FormMapper formMapper;

    /**
     * Создает новую форму по запросу
     * @param formRequestDTO запрос
     * @return {@link FormResponseDTO} ответ
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public FormResponseDTO createForm(FormRequestDTO formRequestDTO) {
        if (formRepository.findByTitle(formRequestDTO.getTitle()).isPresent()) {
            throw new EntityExistsException("Form with title '" + formRequestDTO.getTitle() + "' already exists");
        }

        Form formEntity = formMapper.toEntity(formRequestDTO);

        formEntity.getQuestions().forEach(question -> {
            question.setForm(formEntity);
            if (question.getOptions() != null) {
                question.getOptions().forEach(option -> option.setQuestion(question));
            }
        });

        Form createdFormEntity = formRepository.save(formEntity);
        return formMapper.toDto(createdFormEntity);
    }

    /**
     * Запрашивает из базы полный список форм для опроса
     * @return список {@link FormResponseDTO}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FormResponseDTO> getAllForms() {
        return StreamSupport.stream(formRepository.findAll().spliterator(), false)
                .map(formMapper::toDto)
                .toList();
    }

    /**
     * Запрашивает из базы все формы созданные определенным пользователем
     * @param creatorName имя пользователя-создателя
     * @return список {@link FormResponseDTO}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<FormResponseDTO> getAllFormsByCreatorName(String creatorName) {
        return formRepository.findAllByCreator_Username(creatorName).stream()
                .map(formMapper::toDto)
                .toList();
    }

    /**
     * Запрашивает из базы форму с определенным названием
     * @param title название формы
     * @return {@link FormResponseDTO} ответ
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FormResponseDTO getFormByTitle(String title) {
        Form formEntity = formRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Form with title '" + title + "' not found"));
        return formMapper.toDto(formEntity);
    }

    /**
     * Запрашивает из базы форму с определенным ID
     * @param id ID формы
     * @return {@link FormResponseDTO}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public FormResponseDTO getFormById(Integer id) {
        Form formEntity = formRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Form not found"));
        return formMapper.toDto(formEntity);
    }

    /**
     * Удаляет из базы форму по ее ID
     * @param id ID формы
     * @return true - удалено, false - не найдено/не удалено
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean deleteForm(Integer id) {
        Optional<Form> formEntity = formRepository.findById(id);
        if (formEntity.isPresent()) {
            deleteImages(formEntity.get());
            formRepository.delete(formEntity.get());
            return true;
        }
        return false;
    }


    /**
     * Удаляет связанные с формой изображения из соответствующей серверной папки
     * @param formEntity форма опроса
     */
    private void deleteImages(Form formEntity) {
        if (formEntity.getQuestions() == null) {
            return;
        }

        for (Question question : formEntity.getQuestions()) {
            String imageUrl = question.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String basePath = FileStorageUtils.getUploadDir();
                Path filePath = Paths.get(basePath, imageUrl.replace("/images/", ""));

                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to delete file " + filePath, e);
                }
            }
        }
    }
}
