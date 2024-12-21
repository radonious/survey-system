package edu.eltex.forms.service;

import edu.eltex.forms.entities.Option;
import edu.eltex.forms.entities.Question;
import edu.eltex.forms.mapper.OptionMapper;
import edu.eltex.forms.repository.OptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;

    /**
     * Конвертирует выбранные пользователем опции в созданные вместе с формой опции
     * @param options список опция вопроса
     * @param question вопрос
     * @return список {@link Option}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<Option> convertIntoExistingOptions(List<Option> options, Question question) {
        return options.stream()
                .map(option -> optionRepository.findByTextAndQuestion(option.getText(), question))
                .toList();
    }
}
