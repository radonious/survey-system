package edu.eltex.forms.service;

import edu.eltex.forms.dto.CompletionRequestDTO;
import edu.eltex.forms.dto.CompletionResponseDTO;
import edu.eltex.forms.entities.Completion;
import edu.eltex.forms.entities.Option;
import edu.eltex.forms.enums.QuestionType;
import edu.eltex.forms.exception.FormAlreadyCompletedException;
import edu.eltex.forms.exception.IncompleteRatingAnswerException;
import edu.eltex.forms.mapper.CompletionMapper;
import edu.eltex.forms.repository.CompletionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class CompletionService {

    private final OptionService optionService;
    private final CompletionRepository completionRepository;
    private final CompletionMapper completionMapper;

    /**
     * Получить все существующие прохождения из базы
     * @return список {@link CompletionResponseDTO}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CompletionResponseDTO> getAllCompletions() {
        return StreamSupport.stream(completionRepository.findAll().spliterator(), false)
                .map(completionMapper::toDTO)
                .toList();
    }

    /**
     * Получить прохождение по ID
     * @return {@link CompletionResponseDTO}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CompletionResponseDTO getCompletionById(Integer id) {
        Completion completionEntity = completionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Completion with given ID not found"));
        return completionMapper.toDTO(completionEntity);
    }

    /**
     * Добавляет информацию о новом прохождении опроса в базу.
     * @param completionRequestDTO запрос на создание
     * @return {@link CompletionResponseDTO}
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CompletionResponseDTO createCompletion(CompletionRequestDTO completionRequestDTO) {
        if (completionRequestDTO != null && getCompletionIdByUserAndForm(completionRequestDTO.getFormId(), completionRequestDTO.getUserId()) != -1) {
            throw new FormAlreadyCompletedException("Form with ID: "
                    + completionRequestDTO.getFormId() + " already completed by user with ID: " + completionRequestDTO.getUserId());
        }
        Completion completionEntity = completionMapper.toEntity(completionRequestDTO);
        // Save won't work without setting valid references and cascade
        // All child must have references to corresponding parents (not nulls or randomly generated)
        // Ignoring = NullPointerException or Hibernate Exception
        completionEntity.getAnswers().forEach(answer -> {
            answer.setCompletion(completionEntity);
            if (answer.getQuestion().getType() == QuestionType.RATING) {
                Set<String> optionsOfQuestion = answer.getQuestion().getOptions().stream().map(Option::getText).collect(Collectors.toSet());
                answer.getSelectedOptions().forEach(option -> {
                    if (!optionsOfQuestion.contains(option.getText())) {
                        throw new IncompleteRatingAnswerException("Not all options are selected for the question: " + answer.getQuestion().getText());
                    }
                });
            }
            if (answer.getSelectedOptions() != null) {
                // Everything except options is new and needed to be saved, but options are not
                // Replace options by already existing ones to stop creating new options on every answer
                // Ignoring = Creating options on every POST and bad statistics results
                var existingOptions = optionService.convertIntoExistingOptions(answer.getSelectedOptions(), answer.getQuestion());
                answer.setSelectedOptions(existingOptions);
            }
        });
        Completion completionEntitySaved = completionRepository.save(completionEntity);
        return completionMapper.toDTO(completionEntitySaved);
    }

    /**
     * Удаляет информацию о прохождении опроса из базы
     * @param id ID опроса
     * @return true - удалено, false - не удалено/нечего удалять
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean deleteCompletion(Integer id) {
        Optional<Completion> completionEntity = completionRepository.findById(id);
        if (completionEntity.isPresent()) {
            completionRepository.delete(completionEntity.get());
            return true;
        }
        return false;
    }

    /**
     * Определяет ID прохождения для определенного пользователя и формы
     * @param formId ID формы
     * @param userId ID пользователя
     * @return ID прохождения
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Integer getCompletionIdByUserAndForm(Integer formId, Integer userId) {
        return completionRepository.findCompletionIdByForm_IdAndUser_Id(formId, userId).orElse(-1);
    }

    public boolean isUserOwnerOfCompletion(Integer userId, Integer completionId) {
        Completion completion = completionRepository.findById(completionId).orElseThrow(()->new EntityNotFoundException("Completion with ID: " + completionId+" not found"));
        return completion.getUser().getId().equals(userId);
    }
}
