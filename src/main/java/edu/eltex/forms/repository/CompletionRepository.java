package edu.eltex.forms.repository;

import edu.eltex.forms.entities.Completion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompletionRepository extends CrudRepository<Completion, Integer> {

    /**
     * Запрос на получения информации о том, проходил ли пользователь опрос
     * @param formId ID формы
     * @param userId ID пользователя
     * @return Optional ID прохождения (empty = не проходил)
     */
    @Query("SELECT c.id FROM Completion c WHERE c.form.id =:formId AND c.user.id = :userId ")
    Optional<Integer> findCompletionIdByForm_IdAndUser_Id(@Param("formId") Integer formId, @Param("userId") Integer userId);
}
