package edu.eltex.forms.repository;

import edu.eltex.forms.entities.Answer;
import edu.eltex.forms.entities.Option;
import edu.eltex.forms.entities.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatisticRepository extends CrudRepository<Answer, Integer> {

    /**
     * Получить количество прохождений определенной формы
     * @param formId ID формы
     * @return количество прохождений
     */
    @Query("SELECT COUNT(c) FROM Completion c WHERE c.form.id = :formId")
    Integer countNumberOfCompletions(@Param("formId") int formId);

    /**
     * Получить все полученные на форму ответы
     * @param formId ID формы
     * @return список ответов
     */
    @Query("SELECT a FROM Answer a WHERE a.question.form.id = :formId")
    List<Answer> getAllAnswers(@Param("formId") int formId);

    /**
     * Получить полный список опций для формы
     * @param formId ID формы
     * @return список опций
     */
    @Query("SELECT o FROM Option o WHERE o.question.form.id = :formId AND o.question.type in ('SINGLE_CHOICE', 'MULTIPLE_CHOICE', 'RATING')")
    List<Option> getAllOptions(@Param("formId") int formId);

    /**
     * Получить полный список вопросов для формы
     * @param formId ID формы
     * @return список вопросов
     */
    @Query("SELECT q FROM Question q WHERE q.form.id = :formId ORDER BY q.id")
    List<Question> getAllQuestions(@Param("formId") int formId);
}