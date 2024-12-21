package edu.eltex.forms.repository;

import edu.eltex.forms.entities.Option;
import edu.eltex.forms.entities.Question;
import org.springframework.data.repository.CrudRepository;

public interface OptionRepository extends CrudRepository<Option, Integer> {

    /**
     * Найти опцию по тексту и вопросу
     * @param text текст опции
     * @param question вопрос, в котором находится опция
     * @return entity опции
     */
    Option findByTextAndQuestion(String text, Question question);
}